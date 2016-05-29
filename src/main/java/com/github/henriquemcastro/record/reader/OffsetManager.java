package com.github.henriquemcastro.record.reader;

/**
 * Created by hcastro on 29/05/16.
 */
public interface OffsetManager {

    void commitOffset(String filePath, long offset);

    long getLastOffset(String filePath);

}
