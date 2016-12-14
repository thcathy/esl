package com.esl.service.rest;

import com.esl.entity.rest.WebItem;
import com.google.common.base.Strings;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

@Service
public class WebParserRestService {
    private final String host;

    public WebParserRestService(@Value("${APISERVER_HOST}") String apiHost) {
        if (Strings.isNullOrEmpty(apiHost)) throw new IllegalArgumentException("Cannot create WebParserRestService without API server's host");

        if (!apiHost.contains("http://")) apiHost = "http://" + apiHost;
        if (!apiHost.endsWith("/")) apiHost = apiHost + "/";
        this.host = apiHost;
    }

    static {
        UnirestSetup.setupAll();
    }

    public Future<HttpResponse<WebItem[]>> searchGoogleImage(String query) {
        return Unirest.get(host + "rest/search/image/{query}")
                .routeParam("query", query)
                .asObjectAsync(WebItem[].class);
    }

}
