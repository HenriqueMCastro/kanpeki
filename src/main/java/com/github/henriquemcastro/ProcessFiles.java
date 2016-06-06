package com.github.henriquemcastro;

import com.github.henriquemcastro.record.reader.*;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.Properties;

/**
 * Created by hcastro on 31/05/16.
 */
public class ProcessFiles {


    public static void processFiles(Properties properties, Processor processor) throws IOException, InterruptedException {
        String path = properties.getProperty(KanpekiConfig.FILES_PATH);
        String fileFormat = properties.getProperty(KanpekiConfig.FILES_FORMAT, KanpekiConfig.FILES_FORMAT_DEFAULT);
        boolean manageOffsetsEnabled = Boolean.valueOf(properties.getProperty(KanpekiConfig.MANAGE_OFFSETS_ENABLED, KanpekiConfig.MANAGE_OFFSETS_ENABLED_DEFAULT));
        String offsetsDbPath = properties.getProperty(KanpekiConfig.OFFSETS_DB_PATH);
        OffsetManager offsetManager;
        if(manageOffsetsEnabled){
            offsetManager = new OffsetManagerTextFile(offsetsDbPath);
        }
        else {
            offsetManager = new OffsetManagerNoOp();
        }
        boolean onePassOnly = Boolean.valueOf(properties.getProperty(KanpekiConfig.ONE_PASS_ONLY_ENABLED, KanpekiConfig.ONE_PASS_ONLY_ENABLED_DEFAULT));

        try {
            PathReader pathReader = new PathReader(path, fileFormat, processor, offsetManager, onePassOnly);
            pathReader.processPath();
        } catch (InterruptedException e) {
            offsetManager.close();
            processor.close();
            throw new InterruptedException();
        }
    }

}
