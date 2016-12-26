package com.esl.util;

import org.junit.Test;

/**
 * Created by thcathy on 15/12/2016.
 */
public class WebUtilTest {
    @Test
    public void searchImageBinary() throws Exception {
        String[] l = WebUtil.searchImageBinary("test clipart");
        assert l.length == 10;
    }

}