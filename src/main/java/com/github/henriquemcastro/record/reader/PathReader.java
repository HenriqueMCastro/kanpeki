package com.github.henriquemcastro.record.reader;

import com.github.henriquemcastro.Processor;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by hcastro on 31/05/16.
 */
public class PathReader {

    private final String path;
    private final OffsetManager offsetManager;
    private boolean onePassOnly;
    private final String fileFormat;
    private final PathMatcher pathMatcher;
    private Processor processor;
    private final Map<String, RecordReader> fileProcessors;
    private volatile boolean isStopped = false;


    public PathReader(String path, String fileFormat, Processor processor, OffsetManager offsetManager, boolean onePassOnly) throws IOException {
        this.path = path;
        this.fileFormat = fileFormat;
        this.processor = processor;
        this.offsetManager = offsetManager;
        this.onePassOnly = onePassOnly;
        this.fileProcessors = new HashMap();
        pathMatcher = FileSystems.getDefault().getPathMatcher("glob:" + path + "/" + fileFormat);
    }

    private void findFiles(String folderPath) throws IOException {
        File folder = new File(folderPath);
        if(folder.exists()) {
            File[] listOfFiles = folder.listFiles();
            for (int i = 0; i < listOfFiles.length; i++) {
                File file = listOfFiles[i];
                if (file.isFile() && pathMatcher.matches(Paths.get(file.toString()))) {
                    String fileAbsolutePath = file.getAbsolutePath();
                    if(!fileProcessors.containsKey(fileAbsolutePath)) {
                        fileProcessors.put(fileAbsolutePath, new RecordReader(file.toString(), processor, offsetManager));
                    }
                } else if (file.isDirectory()) {
                    findFiles(file.getPath());
                }
            }
        }
    }

    public void processPath() throws IOException, InterruptedException {
        if(onePassOnly) {
            process();
        }
        else{
            while(!isStopped){
                process();
                Thread.sleep(1000);
            }
        }

    }

    /**
     * Stop the path reader to keep on watching the files. It doesn't stop immediately if it isn't in the middle of
     * processing the files.
     */
    public void stop(){
        isStopped = true;
    }

    private void process() throws IOException {
        findFiles(removeStarsFromPath(path));
        List<RecordReader> fileProcessorsToRemove = null;
        for (RecordReader fileProcessor : fileProcessors.values()) {
            RecordReader.ExitStatus exitStatus = fileProcessor.processFile();
            if(RecordReader.ExitStatus.FILE_NO_LONGER_EXISTS.equals(exitStatus)){
                if(fileProcessorsToRemove == null){
                    fileProcessorsToRemove = new ArrayList<>();
                }
                fileProcessorsToRemove.add(fileProcessor);
            }
        }
        if(fileProcessorsToRemove != null) {
            fileProcessorsToRemove.stream().forEach(recordReader -> fileProcessors.remove(recordReader.getFilePath()));
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
