package com.github.henriquemcastro.record.reader;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hcastro on 29/05/16.
 */
public class OffsetManagerTextFile implements OffsetManager {

    private static final Logger LOG = LoggerFactory.getLogger(OffsetManagerTextFile.class);

    private static final String KEY_VALUE_SEPARATOR = "===";

    private static final String OFFSET_MAP_NAME = "offsets";

    private final Map<String, Long> memoryMap;
    private Map<String, Long> committedMap;
    private String dbPath;

    public OffsetManagerTextFile(String dbPath){
        this.dbPath = dbPath;
        memoryMap = new HashMap<>();
        committedMap = new HashMap<>();
        makeDirIfItDoesntExist();
        loadOffsets();
    }

    private void loadOffsets(){
        Path offsetsDb = Paths.get(dbPath);
        if(offsetsDb.toFile().exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(offsetsDb.toFile()))) {
                String line = null;
                while ((line = br.readLine()) != null) {
                    String[] keyValue = line.split(KEY_VALUE_SEPARATOR);
                    if(keyValue.length==2){
                        memoryMap.put(keyValue[0], Long.valueOf(keyValue[1]));
                        committedMap.put(keyValue[0], Long.valueOf(keyValue[1]));
                    }
                }
            } catch (IOException e) {
                LOG.error("Couldn't read line: " + e.getMessage(), e);
            }
        }
    }

    private void writeOffsetsToDisk() {
        try (FileWriter fw = new FileWriter(dbPath)) {
            for (Map.Entry<String, Long> entry : memoryMap.entrySet()) {
                fw.write(entry.getKey() + KEY_VALUE_SEPARATOR + entry.getValue() + System.lineSeparator());
            }
        } catch (IOException e) {
            LOG.error("Couldn't write offsets to disk: " + e.getMessage(), e);
        }
    }

    @Override
    public synchronized void addOffset(String filePath, long offset){
        memoryMap.put(filePath, offset);
    }

    @Override
    public synchronized void commitOffsets() {
        LOG.debug("Committing offsets");
        writeOffsetsToDisk();
        committedMap = new HashMap<>(memoryMap);
    }

    @Override
    public synchronized long getLastInMemoryOffset(String filePath) {
        if(memoryMap.containsKey(filePath)){
            return memoryMap.get(filePath);
        }
        return 0;
    }

    @Override
    public long getLastCommittedOffset(String filePath) {
        if(committedMap.containsKey(filePath)){
            return committedMap.get(filePath);
        }
        return 0;
    }

    @Override
    public void close(){
        // nothing to do
    }

    private void makeDirIfItDoesntExist(){
        Paths.get(dbPath).toAbsolutePath().toFile().getParentFile().mkdirs();
    }
}
