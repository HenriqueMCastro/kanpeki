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

    public ExitStatus processFile() throws IOException, InterruptedException {
        File file = new File(filePath);
        String fileName = file.getName();
        try(RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
            randomAccessFile.seek(startOffset);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(randomAccessFile.getFD())));
            int numOfMessages = 0;
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if(!Thread.interrupted()){
                    Processor.Offset commitOffset = processor.process(line, fileName);
                    offsetManager.addOffset(filePath, randomAccessFile.getFilePointer());
                    if (Processor.Offset.COMMIT.equals(commitOffset)) {
                        offsetManager.commitOffsets();
                    }
                    numOfMessages++;
                }
                else{
                    throw new InterruptedException();
                }
            }
            long endOffset = randomAccessFile.getFilePointer();
            if(endOffset != startOffset) {
                LOG.info("Processed {} messages with {} ({} to {}) for file {}", numOfMessages, humanReadableByteCount(endOffset - startOffset), startOffset, endOffset, filePath);
                startOffset = endOffset;
            }
            return ExitStatus.OK;
        }
        catch(FileNotFoundException e){
            LOG.info("File {} no longer exists.", filePath);
            return ExitStatus.FILE_NO_LONGER_EXISTS;
        }
    }

    public String getFilePath(){
        return filePath;
    }

    private long getStartOffset(){
        return offsetManager.getLastInMemoryOffset(filePath);
    }

    public static String humanReadableByteCount(long bytes) {
        int unit = 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = ("KMGTPE").charAt(exp-1) + ("i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public enum ExitStatus{
        OK,
        FILE_NO_LONGER_EXISTS;
    }
}
