package com.github.henriquemcastro.record.reader;

/**
 * Created by hcastro on 29/05/16.
 */
public class OffsetManagerNoOp implements OffsetManager {

    @Override
    public void addOffset(String filePath, long offset) {
        // do nothing
    }

    @Override
    public void commitOffsets() {
        // do nothing
    }

    @Override
    public long getLastInMemoryOffset(String filePath) {
        return 0;
    }

    @Override
    public long getLastCommittedOffset(String filePath) {
        return 0;
    }
}
