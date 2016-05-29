package com.github.henriquemcastro;

/**
 * Created by hcastro on 29/05/16.
 */
public interface Processor {

    /**
     *
     * @param record
     * @return Wether or not to commit the file position offset to disk
     */
    boolean process(String record);
}
