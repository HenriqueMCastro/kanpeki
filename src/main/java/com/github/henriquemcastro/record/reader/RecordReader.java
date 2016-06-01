package com.github.henriquemcastro.record.reader;

import com.github.henriquemcastro.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.*;
import java.util.Properties;

/**
 * Created by hcastro on 29/05/16.
 */
public class RecordReader {

    public static final String FILE_PATH = "record.reader.file.path";

    private static final Logger LOG = LoggerFactory.getLogger(RecordReader.class);

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
        LOG.info("Going to process file " + filePath + ". Starting offset = " + startOffset);
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

        LOG.info("Processed file " + filePath);
    }

    private long getStartOffset(){
        return offsetManager.getLastOffset(filePath);
    }


}
