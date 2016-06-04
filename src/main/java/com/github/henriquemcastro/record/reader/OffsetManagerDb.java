package com.github.henriquemcastro.record.reader;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hcastro on 29/05/16.
 */
public class OffsetManagerDb implements OffsetManager {

    private static final Logger LOG = LoggerFactory.getLogger(OffsetManagerDb.class);

    private static final String OFFSET_MAP_NAME = "offsets";

    private final Map<String, Long> memoryMap;
    private final HTreeMap<String, Long> diskMap;
    private final DB offsetDb;

    public OffsetManagerDb(String dbPath){
        makeDirIfItDoesntExist(dbPath);
        offsetDb = DBMaker.fileDB(dbPath). closeOnJvmShutdown().make();
        diskMap = offsetDb.hashMap(OFFSET_MAP_NAME, Serializer.STRING, Serializer.LONG).createOrOpen();
        memoryMap = new HashMap<>();
        updateInMemoryMapFromInDiskMap();
    }

    private void updateInMemoryMapFromInDiskMap(){
        for(Object key : diskMap.keySet()){
            memoryMap.put(String.valueOf(key), diskMap.get(String.valueOf(key)));
        }
    }

    @Override
    public synchronized void addOffset(String filePath, long offset){
        memoryMap.put(filePath, offset);
    }

    @Override
    public synchronized void commitOffsets() {
        LOG.debug("Committing offsets");
        for(Map.Entry<String, Long> entry : memoryMap.entrySet()){
            diskMap.put(entry.getKey(), entry.getValue());
            if(LOG.isTraceEnabled()){
                LOG.trace("Committing offset {} for file {}", entry.getValue(), entry.getKey());
            }
        }
        offsetDb.commit();
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
        if(diskMap.containsKey(filePath)){
            return diskMap.get(filePath);
        }
        return 0;
    }

    public void close(){
        offsetDb.close();
    }

    private void makeDirIfItDoesntExist(String dbPath){
        Paths.get(dbPath).toAbsolutePath().toFile().getParentFile().mkdirs();
    }
}
