package com.esl.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class SpringUtil {
	private static ApplicationContext context=null;

	public SpringUtil() {}
	
	public static void initSpring() {
		System.out.println("<<<<<<<<<SpringUtil static init>>>>>>>>>>>");
		try {
			String[] configFiles=new String[2];
			configFiles[0] = "com/esl/ESL-context.xml";
			context = new FileSystemXmlApplicationContext(configFiles);			
		}
		catch (Throwable ex) {
			throw new ExceptionInInitializerError(ex);
		}	
	}
	
	public static ApplicationContext getContext() { return context; }
}
