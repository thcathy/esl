//package com.esl.web.lifecycle;
//
//import com.sun.faces.lifecycle.LifecycleImpl;
//import javax.faces.FacesException;
//import javax.faces.context.FacesContext;
//import javax.servlet.http.HttpServletResponse;
//
//import org.apache.log4j.Logger;
//
//public class ESLLifeCycleImpl extends LifecycleImpl{
//	private static final String viewExceptionURL = "/error/viewexpirederror.jsf";
//	private static final String systemExceptionURL = "/error/systemerror.jsf";
//
//    public ESLLifeCycleImpl() {
//        super();
//    }
//
//    public void execute(FacesContext context) throws FacesException{
//        try{
//            super.execute(context);
//        }catch(javax.faces.application.ViewExpiredException e){
//            Logger.getLogger("ESL").info("Catch ViewExpiredException");
//            try{
//                context.responseComplete();
//                context.renderResponse();
//                HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
//                String url = context.getExternalContext().getRequestContextPath() + viewExceptionURL;
//                response.sendRedirect(url);
//            }catch(Exception e1){
//            	Logger.getLogger("ESL").info("ViewExpiredException: url redirect wrong ");
//            }
//        }catch(Exception ex){
//            Logger.getLogger("ESL").error("System Error:", ex);
//            try{
//                context.responseComplete();
//                context.renderResponse();
//                HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
//                String url = context.getExternalContext().getRequestContextPath() + systemExceptionURL;
//                response.sendRedirect(url);
//            }catch(Exception e1){
//            	Logger.getLogger("ESL").info("SystemException: url redirect wrong ");
//            }
//        }
//
//    }
//
//}