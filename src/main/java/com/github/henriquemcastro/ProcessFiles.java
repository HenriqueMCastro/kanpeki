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

    public static final String FILES_PATH = "files.path";

    public static final String MANAGE_OFFSETS_ENABLED = "manage.offsets.enabled";

    public static final String MANAGE_OFFSETS_ENABLED_DEFAULT = "false";

    public static final String OFFSETS_DB_PATH = "offsets.db.path";

    public static final String FILES_FORMAT = "files.format";

    public static final String FILES_FORMAT_DEFAULT = "*";

    public static void processFiles(Properties properties, Processor processor) throws IOException {
        String path = properties.getProperty(FILES_PATH);
        String fileFormat = properties.getProperty(FILES_FORMAT, FILES_FORMAT_DEFAULT);
        boolean manageOffsetsEnabled = Boolean.valueOf(properties.getProperty(MANAGE_OFFSETS_ENABLED, MANAGE_OFFSETS_ENABLED_DEFAULT));
        String offsetsDbPath = properties.getProperty(OFFSETS_DB_PATH);
        OffsetManager offsetManager;
        if(manageOffsetsEnabled){
            offsetManager = new OffsetManagerDb(offsetsDbPath);
        }
        else {
            offsetManager = new OffsetManagerNoOp();
        }

        new PathReader(path, fileFormat, processor, offsetManager).processPath();
    }

}
