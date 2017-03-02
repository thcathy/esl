package com.esl.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.FacesException;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.io.IOException;

public class JSFUtil {
	private static Logger logger = LoggerFactory.getLogger(JSFUtil.class);

	/**
	 * Redirect a request (no matter ajax or not) to new page
	 */
	public static String redirectToJSF(String link) {
        FacesContext ctx = FacesContext.getCurrentInstance();

        ExternalContext extContext = ctx.getExternalContext();
        if (!link.endsWith(".xhtml")) link = link.concat(".xhtml");
        String url = extContext.encodeActionURL(ctx.getApplication().getViewHandler().getActionURL(ctx, link));
        try {
            extContext.redirect(url);
        } catch (IOException ioe) {
            throw new FacesException(ioe);

        }
        return null; 
    }
}
