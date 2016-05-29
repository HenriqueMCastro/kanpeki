package com.github.henriquemcastro;

import java.net.URL;

/**
 * Created by hcastro on 29/05/16.
 */
public class TestingUtils {

    public static String getResourcePath(String resource){
        URL url = Thread.currentThread().getContextClassLoader().getResource(resource);
        return url.getPath();
    }
}
