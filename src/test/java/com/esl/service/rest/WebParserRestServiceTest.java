package com.esl.service.rest;

import com.esl.ESLApplication;
import com.esl.entity.rest.WebItem;
import org.assertj.core.util.Strings;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertFalse;

@SpringBootTest
@ContextConfiguration(classes=ESLApplication.class)
@RunWith(SpringRunner.class)
public class WebParserRestServiceTest {
    private Logger log = LoggerFactory.getLogger(WebParserRestServiceTest.class);

    @Autowired WebParserRestService service;

    @Test(expected = IllegalArgumentException.class)
    public void createServiceWithoutHost_ShouldThrowException() {
        new WebParserRestService(null);
    }

    @Test
    public void searchGoogleImage_shouldReturn10WebItem() throws ExecutionException, InterruptedException {
        CompletableFuture<WebItem[]> result = service.searchGoogleImage("testing");
        WebItem[] items = result.join();

        assert items.length == 10;
        for (WebItem item : items) {
            assertFalse(Strings.isNullOrEmpty(item.url));
        }
    }
}
