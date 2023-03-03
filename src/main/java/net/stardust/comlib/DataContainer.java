package net.stardust.comlib;

import java.util.Optional;

public interface DataContainer<T> {
    
    Optional<T> getContent();

}
