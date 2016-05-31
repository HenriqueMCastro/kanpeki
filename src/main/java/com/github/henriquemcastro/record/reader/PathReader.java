package com.github.henriquemcastro.record.reader;

import com.github.henriquemcastro.Processor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;


/**
 * Created by hcastro on 31/05/16.
 */
public class PathReader {

    private final String path;
    private Processor processor;

    public PathReader(String path, Processor processor){
        this.path = path;
        this.processor = processor;
    }

    public void processPath() throws IOException {
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                RecordReader recordReader = new RecordReader(listOfFiles[i].toString(), processor, new OffsetManagerNoOp());
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
