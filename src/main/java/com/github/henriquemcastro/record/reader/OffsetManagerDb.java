package com.github.henriquemcastro.record.reader;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;

import java.io.File;
import java.nio.file.Paths;

/**
 * Created by hcastro on 29/05/16.
 */
public class OffsetManagerDb implements OffsetManager {

    private static final String OFFSET_MAP_NAME = "offsets";

    private final HTreeMap<String, Long> offsetMap;
    private final DB offsetDb;

    public OffsetManagerDb(String dbPath){
        makeDirIfItDoesntExist(dbPath);
        offsetDb = DBMaker.fileDB(dbPath).closeOnJvmShutdown().make();
        offsetMap = offsetDb.hashMap(OFFSET_MAP_NAME, Serializer.STRING, Serializer.LONG).createOrOpen();
    }

    @Override
    public synchronized void commitOffset(String filePath, long offset) {
        offsetMap.put(filePath, offset);
        offsetDb.commit();
    }

    @Override
    public synchronized long getLastOffset(String filePath) {
        if(offsetMap.containsKey(filePath)){
            return offsetMap.get(filePath);
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
