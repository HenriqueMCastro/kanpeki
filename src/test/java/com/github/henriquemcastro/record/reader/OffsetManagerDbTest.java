package com.github.henriquemcastro.record.reader;

import com.github.henriquemcastro.TestingUtils;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Created by hcastro on 29/05/16.
 */
public class OffsetManagerDbTest {

    private static final String DB_NAME = "offsets-test";

    private OffsetManagerDb offsetManagerDb;

    @Test
    public void testThatOffsetsAreWrittenToDisk(){
        String filePath = "example-1/example.txt";
        String resourcePath = TestingUtils.getResourcePath(filePath);
        long offset = 6;
        try{
            checkThatDbDoesNotExist();
            offsetManagerDb = new OffsetManagerDb((DB_NAME));
            offsetManagerDb.addOffset(resourcePath, offset);
            offsetManagerDb.close();

            offsetManagerDb = new OffsetManagerDb(DB_NAME);
            long lastOffset = offsetManagerDb.getLastInMemoryOffset(resourcePath);
            assertEquals(0, lastOffset);
            offsetManagerDb.addOffset(resourcePath, offset);
            offsetManagerDb.commitOffsets();
            offsetManagerDb.close();

            offsetManagerDb = new OffsetManagerDb(DB_NAME);
            lastOffset = offsetManagerDb.getLastInMemoryOffset(resourcePath);
            assertEquals(offset, lastOffset);

        }
        finally {
            deleteDb();
        }
    }

    @Test
    public void testThatOffsetsCanBeUpdated(){
        String filePath1 = "example-1/example.txt";
        String filePath2 = "example-1/example-2.txt";
        String resourcePath1 = TestingUtils.getResourcePath(filePath1);
        String resourcePath2 = TestingUtils.getResourcePath(filePath2);
        long offset1 = 6;
        long offset2 = 12;
        long offset3 = 20;
        try{
            checkThatDbDoesNotExist();
            offsetManagerDb = new OffsetManagerDb((DB_NAME));
            offsetManagerDb.addOffset(resourcePath1, offset1);
            assertEquals(offsetManagerDb.getLastInMemoryOffset(resourcePath1), offset1);
            assertEquals(offsetManagerDb.getLastCommittedOffset(resourcePath1), 0);
            offsetManagerDb.addOffset(resourcePath1, offset2);
            assertEquals(offsetManagerDb.getLastInMemoryOffset(resourcePath1), offset2);
            assertEquals(offsetManagerDb.getLastCommittedOffset(resourcePath1), 0);

            offsetManagerDb.addOffset(resourcePath2, offset3);
            assertEquals(offsetManagerDb.getLastInMemoryOffset(resourcePath2), offset3);
            assertEquals(offsetManagerDb.getLastCommittedOffset(resourcePath2), 0);

            offsetManagerDb.commitOffsets();
            assertEquals(offsetManagerDb.getLastCommittedOffset(resourcePath1), offset2);
            assertEquals(offsetManagerDb.getLastCommittedOffset(resourcePath2), offset3);
        }
        finally {
            deleteDb();
        }
    }

    @Test
    public void testThatDbIsCreated(){
        try {
            checkThatDbDoesNotExist();
            offsetManagerDb = new OffsetManagerDb((DB_NAME));
            checkThatDbExists();
        }
        finally {
            deleteDb();
        }
    }

    private void checkThatDbDoesNotExist(){
        assertFalse("Db file should not exist in order to run this test", Paths.get(DB_NAME).toFile().exists());
    }

    private void checkThatDbExists(){
        assertTrue("Expected db file to have been created", Paths.get(DB_NAME).toFile().exists());
    }

    private void deleteDb(){
        Paths.get(DB_NAME).toFile().delete();
    }

}