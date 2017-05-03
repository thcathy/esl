package com.esl.service;

import com.esl.util.JSFUtil;
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

    public String redirectToJSF(String link) {
        return JSFUtil.redirectToJSF(link);
    }
}
