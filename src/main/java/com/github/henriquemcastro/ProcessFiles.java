package com.github.henriquemcastro;

import com.github.henriquemcastro.record.reader.OffsetManager;
import com.github.henriquemcastro.record.reader.OffsetManagerDb;
import com.github.henriquemcastro.record.reader.OffsetManagerNoOp;
import com.github.henriquemcastro.record.reader.PathReader;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by hcastro on 31/05/16.
 */
public class ProcessFiles {


    public static void processFiles(Properties properties, Processor processor) throws IOException {
        String path = properties.getProperty(KanpekiConfig.FILES_PATH);
        String fileFormat = properties.getProperty(KanpekiConfig.FILES_FORMAT, KanpekiConfig.FILES_FORMAT_DEFAULT);
        boolean manageOffsetsEnabled = Boolean.valueOf(properties.getProperty(KanpekiConfig.MANAGE_OFFSETS_ENABLED, KanpekiConfig.MANAGE_OFFSETS_ENABLED_DEFAULT));
        String offsetsDbPath = properties.getProperty(KanpekiConfig.OFFSETS_DB_PATH);
        OffsetManager offsetManager;
        if(manageOffsetsEnabled){
            offsetManager = new OffsetManagerDb(offsetsDbPath);
        }
        else {
            offsetManager = new OffsetManagerNoOp();
        }

        new PathReader(path, fileFormat, processor, offsetManager).processPath();
        processor.close();
    }

}
