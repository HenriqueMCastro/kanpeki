package com.github.henriquemcastro;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by hcastro on 29/05/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class RecordReaderTest {

    @Mock
    Properties properties;

    @Mock
    Processor processor;

    private RecordReader recordReader;

    @Before
    public void setUp(){
        doNothing().when(processor).process(any(String.class));
        when(properties.getProperty(RecordReader.FILE_PATH)).thenReturn(getResourcePath("example-1/example.txt"));

        recordReader = new RecordReader(properties, processor);
    }

    @Test
    public void testThatProcessorIsCalled() throws IOException {
        recordReader.processFile();

        verify(processor, times(5)).process(anyString());
    }

    @Test
    public void testThatProcessorIsCalledWithTheRightRecords() throws IOException {
        recordReader.processFile();

        verify(processor, times(1)).process("1");
        verify(processor, times(1)).process("2");
        verify(processor, times(1)).process("3");
        verify(processor, times(1)).process("4");
        verify(processor, times(1)).process("5");
    }

    @Test
    public void testRecordsArePassedToTheProcessorInOrder() throws IOException {
        recordReader.processFile();

        InOrder inOrder = inOrder(processor, processor, processor, processor, processor);
        inOrder.verify(processor).process("1");
        inOrder.verify(processor).process("2");
        inOrder.verify(processor).process("3");
        inOrder.verify(processor).process("4");
        inOrder.verify(processor).process("5");
    }

    private String getResourcePath(String resource){
        URL url = Thread.currentThread().getContextClassLoader().getResource(resource);
        return url.getPath();
    }
}