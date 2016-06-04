package com.github.henriquemcastro.record.reader;

/**
 * Created by hcastro on 29/05/16.
 */
public interface OffsetManager {

    void addOffset(String filePath, long offset);

    void commitOffsets();

    long getLastInMemoryOffset(String filePath);

    long getLastCommittedOffset(String filePath);

    void close();

}
