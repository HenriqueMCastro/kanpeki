package com.github.henriquemcastro.record.reader;

import com.github.henriquemcastro.Processor;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.*;
import java.util.Properties;

/**
 * Created by hcastro on 29/05/16.
 */
public class RecordReader {

    public static final String FILE_PATH = "record.reader.file.path";

    private final String filePath;
    private final Processor processor;
    private final OffsetManager offsetManager;

    @Inject
    public RecordReader(@Named(FILE_PATH) String filePath, Processor processor, OffsetManager offsetManager){
        this.filePath = filePath;
        this.processor = processor;
        this.offsetManager = offsetManager;
    }

    @Inject
    public RecordReader(@Named(FILE_PATH) String filePath, Processor processor){
        this(filePath, processor, new OffsetManagerNoOp());
    }

    public void processFile() throws IOException {
        long startOffset = getStartOffset();

        try(RandomAccessFile randomAccessFile = new RandomAccessFile(new File(filePath), "r")) {
            randomAccessFile.seek(startOffset);
            String line;
            while ((line = randomAccessFile.readLine()) != null) {
                boolean commitOffset = processor.process(line);
                if (commitOffset) {
                    offsetManager.commitOffset(filePath, randomAccessFile.getFilePointer());
                }
            }
        }
    }

    private long getStartOffset(){
        return offsetManager.getLastOffset(filePath);
    }


}
