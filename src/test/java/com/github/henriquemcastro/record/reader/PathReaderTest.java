package com.github.henriquemcastro.record.reader;

import com.github.henriquemcastro.Processor;
import com.github.henriquemcastro.TestingUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by hcastro on 31/05/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class PathReaderTest {

    private static final boolean ONE_PASS_ONLY = true;

    private static final boolean WATCH_FILES = false;

    private final String folderPath = TestingUtils.getResourcePath("example-1");

    private final String fileFormat = "*";

    @Mock
    Processor processor;

    @Mock
    OffsetManager offsetManager;

    private PathReader pathReader;

    @Before
    public void setUp() throws IOException {
        when(processor.process(anyString(), anyString())).thenReturn(Processor.Offset.COMMIT);
        when(offsetManager.getLastInMemoryOffset(anyString())).thenReturn(0L);
        doNothing().when(offsetManager).addOffset(anyString(), anyLong());

        pathReader = new PathReader(folderPath, fileFormat, processor, offsetManager, ONE_PASS_ONLY);
    }

    @Test
    public void testThatAllFilesInFolderAreProcessed() throws IOException, InterruptedException {
        pathReader.processPath();

        verify(processor, times(18)).process(anyString(), anyString());
        verify(processor, times(1)).process("1", "example.txt");
        verify(processor, times(1)).process("2", "example.txt");
        verify(processor, times(1)).process("3", "example.txt");
        verify(processor, times(1)).process("4", "example.txt");
        verify(processor, times(1)).process("5", "example.txt");
        verify(processor, times(1)).process("6", "example-2.txt");
        verify(processor, times(1)).process("7", "example-2.txt");
        verify(processor, times(1)).process("8", "example-2.txt");
        verify(processor, times(1)).process("9", "example-3.txt");
        verify(processor, times(1)).process("20", "example-4");
        verify(processor, times(1)).process("21", "example-4");
        verify(processor, times(1)).process("22", "example-4");
        verify(processor, times(1)).process("23", "example-4");
        verify(processor, times(1)).process("24", "example-4");
        verify(processor, times(1)).process("30", "example-44.txt");
        verify(processor, times(1)).process("31", "example-44.txt");
        verify(processor, times(1)).process("32", "example-44.txt");

    }

    @Test
    public void testThatItCanHandleOffsets() throws IOException, InterruptedException {
        String file1 = TestingUtils.getResourcePath("example-1/example.txt");
        String file2 = TestingUtils.getResourcePath("example-1/example-2.txt");
        String file3 = TestingUtils.getResourcePath("example-1/example-3.txt");
        String file4 = TestingUtils.getResourcePath("example-1/example-4");
        String file5 = TestingUtils.getResourcePath("example-1/example-44.txt");

        when(offsetManager.getLastInMemoryOffset(file1)).thenReturn(2L);
        when(offsetManager.getLastInMemoryOffset(file2)).thenReturn(4L);
        when(offsetManager.getLastInMemoryOffset(file3)).thenReturn(0L);
        when(offsetManager.getLastInMemoryOffset(file4)).thenReturn(1000L);
        when(offsetManager.getLastInMemoryOffset(file5)).thenReturn(1000L);

        pathReader = new PathReader(folderPath, fileFormat, processor, offsetManager, ONE_PASS_ONLY);
        pathReader.processPath();

        verify(processor, times(7)).process(anyString(), anyString());
        verify(processor, times(1)).process("2", "example.txt");
        verify(processor, times(1)).process("3", "example.txt");
        verify(processor, times(1)).process("4", "example.txt");
        verify(processor, times(1)).process("5", "example.txt");
        verify(processor, times(1)).process("8", "example-2.txt");
        verify(processor, times(1)).process("9", "example-3.txt");
        verify(processor, times(1)).process("10", "example-3.txt");

    }

    @Test
    public void testThatFilenameFilterWorks() throws IOException, InterruptedException {
        String txtFileFormat = "*.txt";
        pathReader = new PathReader(folderPath, txtFileFormat, processor, offsetManager, ONE_PASS_ONLY);

        pathReader.processPath();

        verify(processor, times(13)).process(anyString(), anyString());
        verify(processor, times(1)).process("1", "example.txt");
        verify(processor, times(1)).process("2", "example.txt");
        verify(processor, times(1)).process("3", "example.txt");
        verify(processor, times(1)).process("4", "example.txt");
        verify(processor, times(1)).process("5", "example.txt");
        verify(processor, times(1)).process("6", "example-2.txt");
        verify(processor, times(1)).process("7", "example-2.txt");
        verify(processor, times(1)).process("8", "example-2.txt");
        verify(processor, times(1)).process("9", "example-3.txt");
        verify(processor, times(1)).process("10", "example-3.txt");
        verify(processor, times(1)).process("30", "example-44.txt");
        verify(processor, times(1)).process("31", "example-44.txt");
        verify(processor, times(1)).process("2", "example.txt");

        reset(processor);
        when(processor.process(anyString(), anyString())).thenReturn(Processor.Offset.COMMIT);
        String number4FileFormat = "*4*";

        pathReader = new PathReader(folderPath, number4FileFormat, processor, offsetManager, ONE_PASS_ONLY);

        pathReader.processPath();

        verify(processor, times(8)).process(anyString(), anyString());
        verify(processor, times(1)).process("20", "example-4");
        verify(processor, times(1)).process("21", "example-4");
        verify(processor, times(1)).process("22", "example-4");
        verify(processor, times(1)).process("23", "example-4");
        verify(processor, times(1)).process("24", "example-4");
        verify(processor, times(1)).process("30", "example-44.txt");
        verify(processor, times(1)).process("31", "example-44.txt");
        verify(processor, times(1)).process("32", "example-44.txt");
    }

    @Test
    public void testThatSubfoldersAreProcessedCorrectly() throws IOException, InterruptedException {
        String doubleStarPath = TestingUtils.getResourcePath("example-1").replace("/example-1", "**");
        String txtFileFormat = "*.txt";
        pathReader = new PathReader(doubleStarPath, txtFileFormat, processor, offsetManager, ONE_PASS_ONLY);

        pathReader.processPath();

        verify(processor, times(19)).process(anyString(), anyString());
        verify(processor, times(1)).process("1", "example.txt");
        verify(processor, times(1)).process("2", "example.txt");
        verify(processor, times(1)).process("3", "example.txt");
        verify(processor, times(1)).process("4", "example.txt");
        verify(processor, times(1)).process("5", "example.txt");
        verify(processor, times(1)).process("6", "example-2.txt");
        verify(processor, times(1)).process("7", "example-2.txt");
        verify(processor, times(1)).process("8", "example-2.txt");
        verify(processor, times(1)).process("9", "example-3.txt");
        verify(processor, times(1)).process("10", "example-3.txt");
        verify(processor, times(1)).process("30", "example-44.txt");
        verify(processor, times(1)).process("31", "example-44.txt");
        verify(processor, times(1)).process("32", "example-44.txt");
        verify(processor, times(1)).process("100", "example5.txt");
        verify(processor, times(1)).process("101", "example5.txt");
        verify(processor, times(1)).process("102", "example5.txt");
        verify(processor, times(1)).process("103", "example5.txt");
        verify(processor, times(1)).process("104", "example5.txt");
        verify(processor, times(1)).process("105", "example5.txt");
    }

    @Test
    public void testThatDataAddedAfterFirstPassIsProcessed() throws InterruptedException, IOException {
        String doubleStarPath = TestingUtils.getResourcePath("example-1").replace("/example-1", "**");
        String txtFileFormat = "example-4*";
        pathReader = new PathReader(doubleStarPath, txtFileFormat, processor, offsetManager, WATCH_FILES);

        String valueToAppend = "1000";

        new Thread(() -> {
            try {
                pathReader.processPath();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }).start();

        Thread.sleep(100); // sleep to make sure that the files have been processed once
        verify(processor, times(8)).process(anyString(), anyString());
        verify(processor, times(0)).process(valueToAppend, "example-4");

        appendOneLineToFile("example-1", "example-4", valueToAppend);
        Thread.sleep(1500); // sleep for enough time to process again
        removeLastLineFromFile("example-1", "example-4");
        verify(processor, times(9)).process(anyString(), anyString());
        verify(processor, times(1)).process(valueToAppend, "example-4");

        pathReader.stop();
    }

    private void appendOneLineToFile(String path, String filename, String data) throws IOException {
        String file = TestingUtils.getResourcePath(path + "/" + filename);
        Files.write(Paths.get(file), data.getBytes(), StandardOpenOption.APPEND);
    }

    private void removeLastLineFromFile(String path, String filename) throws IOException {
        String file = TestingUtils.getResourcePath(path + "/" + filename);
        RandomAccessFile f = new RandomAccessFile(file, "rw");
        long length = f.length() - 1;
        byte b;
        do {
            length -= 1;
            f.seek(length);
            b = f.readByte();
        } while(b != 10);
        f.setLength(length+1);
        f.close();
    }

}