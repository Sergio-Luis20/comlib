package net.stardust.comlib;

import java.io.Serializable;

public interface Request<T extends Serializable> extends DataHolder<T> {
    
    RequestMethod getMethod();

}
