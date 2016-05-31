package com.github.henriquemcastro.record.reader;

import com.github.henriquemcastro.Processor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.function.Consumer;


/**
 * Created by hcastro on 31/05/16.
 */
public class PathReader {

    public static final String FILES_PATH = "files.path";

    public static final String MANAGE_OFFSETS_ENABLED = "manage.offsets.enabled";

    public static final String MANAGE_OFFSETS_ENABLED_DEFAULT = "false";

    public static final String OFFSETS_DB_PATH = "offsets.db.path";

    private final String path;
    private final OffsetManager offsetManager;
    private Processor processor;

    public PathReader(String path, Processor processor, OffsetManager offsetManager){
        this.path = path;
        this.processor = processor;
        this.offsetManager = offsetManager;
    }

    public void processPath() throws IOException {
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                RecordReader recordReader = new RecordReader(listOfFiles[i].toString(), processor, offsetManager);
                recordReader.processFile();
            }
//            else if (listOfFiles[i].isDirectory()) {
//                System.out.println("Directory " + listOfFiles[i].getName());
//            }
        }


//
//        Paths.get(this.path).forEach(file -> {
//            new RecordReader(file.toString(), processor, new OffsetManagerNoOp());
//        });

    }
}
