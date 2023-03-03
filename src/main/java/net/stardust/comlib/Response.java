package net.stardust.comlib;

import java.io.Serializable;

public interface Response<T> extends DataContainer<T>, Serializable {
    
    int getStatus();
    
}
