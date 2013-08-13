package com.esl.util;

import org.springframework.context.*;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class SpringUtil {
	private static ApplicationContext context=null;

	public SpringUtil() {}
	
	public static void initSpring() {
		System.out.println("<<<<<<<<<SpringUtil static init>>>>>>>>>>>");
		try {
			String[] configFiles=new String[2];
			configFiles[0] = "WebContent/WEB-INF/ESL-data_local.xml";
			configFiles[1] = "WebContent/WEB-INF/ESL-service_local.xml";
			//configFiles[2] = "WebContent/WEB-INF/ESL-servlet.xml";
			context = new FileSystemXmlApplicationContext(configFiles);			
		}
		catch (Throwable ex) {
			throw new ExceptionInInitializerError(ex);
		}	
	}
	
	public static ApplicationContext getContext() { return context; }
}
