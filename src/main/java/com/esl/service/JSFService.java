package com.esl.service;

import org.springframework.stereotype.Service;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.io.IOException;

@Service
public class JSFService {

    public String redirectTo(String url) throws IOException {
        ExternalContext extContext = FacesContext.getCurrentInstance().getExternalContext();
        extContext.redirect(url);
        return null;
    }
}
