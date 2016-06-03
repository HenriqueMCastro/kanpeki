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
    private long startOffset;

    @Inject
    public RecordReader(@Named(FILE_PATH) String filePath, Processor processor, OffsetManager offsetManager){
        this.filePath = filePath;
        this.processor = processor;
        this.offsetManager = offsetManager;
        startOffset = getStartOffset();
    }

    @Inject
    public RecordReader(@Named(FILE_PATH) String filePath, Processor processor){
        this(filePath, processor, new OffsetManagerNoOp());
    }

    public void processFile() throws IOException {
        LOG.info("Going to process file " + filePath + ". Starting offset = " + startOffset);
        File file = new File(filePath);
        String fileName = file.getName();
        try(RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
            randomAccessFile.seek(startOffset);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(randomAccessFile.getFD())));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                Processor.Offset commitOffset = processor.process(line, fileName);
                if (Processor.Offset.COMMIT.equals(commitOffset)) {
                    offsetManager.commitOffset(filePath, randomAccessFile.getFilePointer());
                }
            }
            long endOffset = randomAccessFile.getFilePointer();
            if(endOffset != startOffset) {
                LOG.info("Processed from " + startOffset + " to " + endOffset + " + for file " + filePath);
                startOffset = endOffset;
            }
        }


    }

    private long getStartOffset(){
        return offsetManager.getLastOffset(filePath);
    }


}
