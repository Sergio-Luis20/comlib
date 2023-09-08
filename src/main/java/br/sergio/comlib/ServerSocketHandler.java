package br.sergio.comlib;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ServerSocketHandler implements ServerHandler {
    
    private ServerSocket server;
    private Map<String, SocketConnectionHandler> connections;
    private SocketInfo info;
    private ExecutorService threadPool;
    private Logger logger;
    private AtomicBoolean closeServer;
    private boolean started;

    private static ServerSocketHandler instance;

    public synchronized static ServerSocketHandler get(SocketInfo info) {
        if(instance == null) {
            instance = new ServerSocketHandler(info);
        }
        return instance;
    }

    public synchronized static ServerSocketHandler get(SocketInfo info, Logger logger) {
        if(instance == null) {
            instance = new ServerSocketHandler(info, logger);
        }
        return instance;
    }

    private ServerSocketHandler(SocketInfo info) {
        this(info, Logger.getLogger(ServerSocketHandler.class.getSimpleName()));
    }

    private ServerSocketHandler(SocketInfo info, Logger logger) {
        this.info = Objects.requireNonNull(info);
        this.logger = Objects.requireNonNull(logger, "logger = null");
    }

    @Override
    public void start() {
        if(started) {
            return;
        }
        buildServerSocket();
        buildConnectionsAndThreadPool();
        logger.info("ServerSocket online");
        logger.info("Starting connection listener task");
        closeServer = new AtomicBoolean();
        threadPool.execute(() -> connectionListenerTask());
        started = true;
    }

    @Override
    public void stop() {
        if(!started) {
            return;
        }
        closeServer.set(true);
        try(ServerSocket server = this.server) {
            closeConnections();
        } catch(Exception e) {
            logger.log(Level.SEVERE, "Error closing the server and its connections", e);
            writeLog(e);
        } finally {
            if(threadPool != null) {
                threadPool.shutdown();
            }
        }
        started = false;
    }

    private void buildServerSocket() {
        try {
            logger.info("Starting communication server");
            InetSocketAddress address = new InetSocketAddress(info.getIP(), info.getPort());
            server = new ServerSocket();
            server.bind(address);
            logger.info("ServerSocket started at " + address);
        } catch(Exception e) {
            logger.log(Level.SEVERE, "Fail at server initialization");
            writeLog(e);
            throw new RuntimeException(e);
        }
    }

    private void buildConnectionsAndThreadPool() {
        logger.info("Creating connections map");
        connections = new ConcurrentHashMap<>();
        logger.info("Creating thread pool");
        threadPool = Executors.newCachedThreadPool(runnable -> {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            return thread;
        });
    }

    private void connectionListenerTask() {
        while(!server.isClosed()) {
            try {
                newConnection(server.accept());
            } catch(IOException e) {
                if(!closeServer.get()) {
                    logger.log(Level.SEVERE, "Exception at handling new connection", e);
                    writeLog(e);
                }
                closeConnections();
                break;
            }
        }
    }

    private void newConnection(Socket socket) {
        threadPool.execute(() -> {
            listen(socket);
            if(closeServer.get()) {
                try {
                    server.close();
                } catch(IOException e) {
                    writeLog(e);
                }
            }
        });
    }

    private void closeConnections() {
        logger.info("Closing connections");
        for(SocketConnectionHandler connection : connections.values()) {
            try {
                if(connection != null) {
                    connection.close();
                }
            } catch(IOException e) {
                logger.log(Level.WARNING, "Fail to close a connection", e);
                writeLog(e);
            }
        }
        logger.info("Connections closed");
    }

    private void listen(Socket socket) {
        try {
            socket.setSoTimeout(info.getSoTimeout());
            SocketConnectionHandler sender = new SocketConnectionHandler(socket);
            Object obj = sender.getInputStream().readObject();
            if(obj == null) {
                sender.close();
            } else if(obj instanceof String command) {
                handleCommand(sender, command);
            } else if(obj instanceof Request<?> request) {
                handleRequest(sender, request);
            } else {
                sender.close();
                throw new RuntimeException("Unnacceptable value: " + obj + " from " + obj.getClass());
            }
        } catch(IOException e) {
            if(socket != null && !socket.isClosed()) {
                try {
                    socket.close();
                } catch(IOException e1) {
                    closeServer.set(true);
                    logger.log(Level.SEVERE, "Could not close a connection while handling an exception at the listen method", e1);
                    writeLog(e1);
                }
            }
            writeLog(e);
        } catch(Exception e) {
            logger.log(Level.SEVERE, "Exception at listen method", e);
            writeLog(e);
        }
    }

    private void handleCommand(SocketConnectionHandler sender, String command) throws IOException {
        boolean close = true;
        try {
            RuntimeException ex = new RuntimeException("Unknown command: " + command);
            if(command.equals("closeServer")) {
                closeServer.set(true);
            } else if(command.contains("=")) {
                String[] split = command.split("=");
                String id = split[1];
                synchronized(connections) {
                    SocketConnectionHandler current = connections.get(id);
                    if(current == null) {
                        connections.remove(id);
                    }
                    close = switch(split[0]) {
                        case "register" -> registerIdCommand(sender, id);
                        case "close" -> closeIdCommand(id);
                        case "read" -> registerReadChannel(sender, id);
                        case "write" -> registerWriteChannel(sender, id);
                        default -> throw ex;
                    };
                }
            } else {
                throw ex;
            }
        } finally {
            ObjectOutputStream output = sender.getOutputStream();
            output.writeBoolean(!close);
            output.flush();
            if(close) {
                sender.close();
            }
        }
    }

    private void handleRequest(SocketConnectionHandler sender, Request<?> request) throws Exception {
        SocketConnectionHandler receiver = connections.get(request.getReceiver());
        try(sender) {
            if(receiver != null) {
                synchronized(receiver) {
                    ObjectOutputStream receiverOutput = receiver.getOutputStream();
                    receiverOutput.writeObject(request);
                    receiverOutput.flush();
                    ObjectInputStream input = receiver.getInputStream();
                    if(request.getMethod() != null) {
                        synchronized(sender) {
                            ObjectOutputStream output = sender.getOutputStream();
                            output.writeObject(input.readObject());
                            output.flush();
                        }
                    } else {
                        input.readObject();
                    }
                }
            }
        }
    }

    private boolean registerIdCommand(SocketConnectionHandler sender, String id) {
        try {
            SocketConnectionHandler current = connections.get(id);
            if(current != null) {
                current.close();   
            }
        } catch(IOException e) {
            logger.log(Level.SEVERE, "Could not close a connection during \"register\" command", e);
            writeLog(e);
            return true;
        }
        connections.put(id, sender);
        logger.info("Registered ID: " + id);
        return false;
    }

    private boolean closeIdCommand(String id) {
        try {
            SocketConnectionHandler current = connections.get(id);
            if(current != null) {
                current.close();   
            }
        } catch(IOException e) {
            logger.log(Level.SEVERE, "Could not close a connection during \"close\" command", e);
            writeLog(e);
            return true;
        }
        connections.remove(id);
        return false;
    }

    private boolean registerReadChannel(SocketConnectionHandler handler, String id) {
        connections.put(id, handler);
        logger.info("Registered ReadChannel: " + id);
        return true;
    }

    private boolean registerWriteChannel(SocketConnectionHandler handler, String id) {
        connections.put(id, handler);
        threadPool.execute(() -> {
            ObjectInputStream input = handler.getInputStream();
            SocketConnectionHandler receiver;
            synchronized(connections) {
                try {
                    receiver = connections.get((String) input.readObject());
                } catch(ClassNotFoundException | IOException | ClassCastException e) {
                    logger.log(Level.SEVERE, "Error identifying recipient ID while registering a WriteChannel");
                    writeLog(e);
                    try {
                        handler.close();
                    } catch(IOException e1) {
                        logger.log(Level.SEVERE, "Error when trying to close connection during exception handling during WriteChannel registration", e1);
                        writeLog(e1);
                    }
                    synchronized(connections) {
                        connections.remove(id);
                    }
                    return;
                }
            }
            if(receiver == null) {
                try {
                    handler.close();
                } catch(IOException e) {
                    logger.log(Level.SEVERE, "Could not close a connection during a WriteChannel registration thread", e);
                    writeLog(e);
                }
            } else {
                try {
                    ObjectOutputStream output = receiver.getOutputStream();
                    while(!receiver.isClosed()) {
                        output.writeObject(input.readObject());
                    }
                } catch(IOException | ClassNotFoundException e) {
                    // Connection closed
                }
            }
        });
        logger.info("Registered WriteChannel: " + id);
        return true;
    }

    private static synchronized void writeLog(Exception ex) {
        try {
            File dir = new File("communication-server-throwables");
            if(!dir.exists()) {
                dir.mkdir();
            }
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss-SSS");
            String fileName = format.format(new Date()) + " " + ex.getClass() + ".twb";
            File file = new File(dir, fileName);
            FileWriter fw = new FileWriter(file, StandardCharsets.UTF_8, true);
            PrintWriter pw = new PrintWriter(fw);
            ex.printStackTrace(pw);
            pw.flush();
            pw.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

}
