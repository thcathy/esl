package com.esl.management;

import com.esl.service.IMembershipService;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

@Aspect
public class LoginCounterAspect {
	private static Logger logger = LoggerFactory.getLogger(LoginCounterAspect.class);

	@Resource
	private LoginMBean loginBean;

	public void setLoginBean(LoginMBean bean) {
		loginBean = bean;
	}

	@AfterReturning(pointcut="execution(public * com.esl.service.IMembershipService.login(..))", returning="retVal")
	public void doAfterLogin(Object retVal) {
		logger.debug("doAfterLogin: START");
		if (IMembershipService.LOGIN_SUCCEED.equals(retVal)) loginBean.addTotalLogin();
	}
}
