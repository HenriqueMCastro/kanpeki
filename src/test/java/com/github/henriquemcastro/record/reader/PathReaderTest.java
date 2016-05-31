package com.github.henriquemcastro.record.reader;

import com.github.henriquemcastro.Processor;
import com.github.henriquemcastro.TestingUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import static org.junit.Assert.*;
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

        pathReader = new PathReader(folderPath, processor, offsetManager);
    }

    @Test
    public void testThatAllFilesInFolderAreProcessed() throws IOException {
        pathReader.processPath();

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
    }

    @Test
    public void testThatItCanHandleOffsets() throws IOException {
//            pathReader = new PathReader(folderPath, processor, offsetManager);

        String file1 = TestingUtils.getResourcePath("example-1/example.txt");
        String file2 = TestingUtils.getResourcePath("example-1/example-2.txt");
        String file3 = TestingUtils.getResourcePath("example-1/example-3.txt");

        when(offsetManager.getLastOffset(file1)).thenReturn(2L);
        when(offsetManager.getLastOffset(file2)).thenReturn(4L);
        when(offsetManager.getLastOffset(file3)).thenReturn(0L);


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

}