package com.github.henriquemcastro.record.reader;

/**
 * Created by hcastro on 29/05/16.
 */
public class OffsetManagerNoOp implements OffsetManager {

    @Override
    public void commitOffset(String filePath, long offset) {
        // do nothing
    }

    @Override
    public long getLastOffset(String filePath) {
        return 0;
    }
}
