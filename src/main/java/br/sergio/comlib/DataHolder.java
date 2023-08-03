package br.sergio.comlib;

import java.io.Serializable;
import java.util.Optional;

public interface DataHolder<T extends Serializable> extends Serializable {
    
    String getSender();
    String getReceiver();
    Optional<T> getContent();

}
