package com.github.henriquemcastro;

/**
 * Created by hcastro on 29/05/16.
 */
public interface Processor {

    /**
     *
     * @param record
     * @param filename Name of the file from which the record was read
     * @return Wether or not to commit the file position offset to disk
     */
    Offset process(String record, String filename);

    /**
     * Called when all files have been processed
     */
    void close();

    enum Offset{
        COMMIT,
        DO_NOT_COMMIT;
    }
}
