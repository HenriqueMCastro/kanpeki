package com.github.henriquemcastro.record.reader;

import com.github.henriquemcastro.Processor;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;


/**
 * Created by hcastro on 31/05/16.
 */
public class PathReader {

    private final String path;
    private final OffsetManager offsetManager;
    private final String fileFormat;
    private final PathMatcher pathMatcher;
    private Processor processor;

    public PathReader(String path, String fileFormat, Processor processor, OffsetManager offsetManager){
        this.path = path;
        this.fileFormat = fileFormat;
        this.processor = processor;
        this.offsetManager = offsetManager;
        pathMatcher = FileSystems.getDefault().getPathMatcher("glob:" + path + "/" + fileFormat);
    }

    public void processPath() throws IOException {
        processFolder(removeStarsFromPath(path));
    }

    private void processFolder(String folderPath) throws IOException {
        File folder = new File(folderPath);
        if(folder.exists()) {
            File[] listOfFiles = folder.listFiles();
            for (int i = 0; i < listOfFiles.length; i++) {
                File file = listOfFiles[i];
                if (file.isFile() && pathMatcher.matches(Paths.get(file.toString()))) {
                    RecordReader recordReader = new RecordReader(file.toString(), processor, offsetManager);
                    recordReader.processFile();
                } else if (file.isDirectory()) {
                    processFolder(file.getPath());
                }
            }
            processor.close();
        }
    }

    private String removeStarsFromPath(String folderPath){
        String res = folderPath;
        if(folderPath.endsWith("**")){
            res = folderPath.substring(0, folderPath.length() - "**".length());
        }
        else if(folderPath.endsWith("**/")){
            res = folderPath.substring(0, folderPath.length() - "**/".length());
        }
        return res;
    }

}
