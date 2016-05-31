package com.github.henriquemcastro.record.reader;

import com.github.henriquemcastro.Processor;
import com.github.henriquemcastro.TestingUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

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

    private final String folderPath = TestingUtils.getResourcePath("example-1");

    private final String fileFormat = "*";

    @Mock
    Processor processor;

    @Mock
    OffsetManager offsetManager;

    private PathReader pathReader;

    @Before
    public void setUp(){
        when(processor.process(anyString())).thenReturn(true);
        when(offsetManager.getLastOffset(anyString())).thenReturn(0L);
        doNothing().when(offsetManager).commitOffset(anyString(), anyLong());

        pathReader = new PathReader(folderPath, fileFormat, processor, offsetManager);
    }

    @Test
    public void testThatAllFilesInFolderAreProcessed() throws IOException {
        pathReader.processPath();

        verify(processor, times(18)).process(anyString());
        verify(processor, times(1)).process("1");
        verify(processor, times(1)).process("2");
        verify(processor, times(1)).process("3");
        verify(processor, times(1)).process("4");
        verify(processor, times(1)).process("5");
        verify(processor, times(1)).process("6");
        verify(processor, times(1)).process("7");
        verify(processor, times(1)).process("8");
        verify(processor, times(1)).process("9");
        verify(processor, times(1)).process("20");
        verify(processor, times(1)).process("21");
        verify(processor, times(1)).process("22");
        verify(processor, times(1)).process("23");
        verify(processor, times(1)).process("24");
        verify(processor, times(1)).process("30");
        verify(processor, times(1)).process("31");
        verify(processor, times(1)).process("32");

    }

    @Test
    public void testThatItCanHandleOffsets() throws IOException {
        String file1 = TestingUtils.getResourcePath("example-1/example.txt");
        String file2 = TestingUtils.getResourcePath("example-1/example-2.txt");
        String file3 = TestingUtils.getResourcePath("example-1/example-3.txt");
        String file4 = TestingUtils.getResourcePath("example-1/example-4");
        String file5 = TestingUtils.getResourcePath("example-1/example-44.txt");

        when(offsetManager.getLastOffset(file1)).thenReturn(2L);
        when(offsetManager.getLastOffset(file2)).thenReturn(4L);
        when(offsetManager.getLastOffset(file3)).thenReturn(0L);
        when(offsetManager.getLastOffset(file4)).thenReturn(1000L);
        when(offsetManager.getLastOffset(file5)).thenReturn(1000L);


        pathReader.processPath();

        verify(processor, times(7)).process(anyString());
        verify(processor, times(1)).process("2");
        verify(processor, times(1)).process("3");
        verify(processor, times(1)).process("4");
        verify(processor, times(1)).process("5");
        verify(processor, times(1)).process("8");
        verify(processor, times(1)).process("9");
        verify(processor, times(1)).process("10");

    }

    @Test
    public void testThatFilenameFilterWorks() throws IOException {
        String txtFileFormat = "*.txt";
        pathReader = new PathReader(folderPath, txtFileFormat, processor, offsetManager);

        pathReader.processPath();

        verify(processor, times(13)).process(anyString());
        verify(processor, times(1)).process("1");
        verify(processor, times(1)).process("2");
        verify(processor, times(1)).process("3");
        verify(processor, times(1)).process("4");
        verify(processor, times(1)).process("5");
        verify(processor, times(1)).process("6");
        verify(processor, times(1)).process("7");
        verify(processor, times(1)).process("8");
        verify(processor, times(1)).process("9");
        verify(processor, times(1)).process("10");
        verify(processor, times(1)).process("30");
        verify(processor, times(1)).process("31");
        verify(processor, times(1)).process("2");

        reset(processor);
        String number4FileFormat = "*4*";

        pathReader = new PathReader(folderPath, number4FileFormat, processor, offsetManager);

        pathReader.processPath();

        verify(processor, times(8)).process(anyString());
        verify(processor, times(1)).process("20");
        verify(processor, times(1)).process("21");
        verify(processor, times(1)).process("22");
        verify(processor, times(1)).process("23");
        verify(processor, times(1)).process("24");
        verify(processor, times(1)).process("30");
        verify(processor, times(1)).process("31");
        verify(processor, times(1)).process("32");
    }

    @Test
    public void testThatSubfoldersAreProcessedCorrectly() throws IOException {
        String doubleStarPath = TestingUtils.getResourcePath("example-1").replace("/example-1", "**");
        String txtFileFormat = "*.txt";
        pathReader = new PathReader(doubleStarPath, txtFileFormat, processor, offsetManager);

        pathReader.processPath();

        verify(processor, times(19)).process(anyString());
        verify(processor, times(1)).process("1");
        verify(processor, times(1)).process("2");
        verify(processor, times(1)).process("3");
        verify(processor, times(1)).process("4");
        verify(processor, times(1)).process("5");
        verify(processor, times(1)).process("6");
        verify(processor, times(1)).process("7");
        verify(processor, times(1)).process("8");
        verify(processor, times(1)).process("9");
        verify(processor, times(1)).process("10");
        verify(processor, times(1)).process("30");
        verify(processor, times(1)).process("31");
        verify(processor, times(1)).process("32");
        verify(processor, times(1)).process("100");
        verify(processor, times(1)).process("101");
        verify(processor, times(1)).process("102");
        verify(processor, times(1)).process("103");
        verify(processor, times(1)).process("104");
        verify(processor, times(1)).process("105");
    }



}