package com.esl.web.filter;

import javax.servlet.*;
import java.io.IOException;


public class AuthorizationFilter implements Filter {
	public void init (FilterConfig FilterConfig) throws ServletException {}
	
	public void doFilter(ServletRequest request, ServletResponse response,FilterChain chain) throws ServletException, IOException {
		if (request.getAttribute("admin") == null)
			request.getRequestDispatcher("/static/WEB-INF/jsp/restricted.jsp").forward(request, response);
		
		chain.doFilter(request, response);
	}
	
	public void destroy(){}

}
