package com.github.henriquemcastro.record.reader;

import com.github.henriquemcastro.Processor;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.*;
import java.util.Properties;

/**
 * Created by hcastro on 29/05/16.
 */
public class RecordReader {

    public static final String FILE_PATH = "record.reader.file.path";

    private final String filePath;
    private final Processor processor;

    @Inject
    public RecordReader(@Named(FILE_PATH) String filePath, Processor processor){
        this.filePath = filePath;
        this.processor = processor;
    }

    @Inject
    public RecordReader(Properties properties, Processor processor){
        String filePath = properties.getProperty(FILE_PATH);
        if(filePath == null){
            throw new IllegalArgumentException(FILE_PATH + " was not set");
        }
        this.filePath = filePath;
        this.processor = processor;
    }

    public void processFile() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File(filePath)));
        String line;
        while ((line = br.readLine()) != null) {
            processor.process(line);
        }
        br.close();
    }


}
