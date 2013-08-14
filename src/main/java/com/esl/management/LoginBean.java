package com.esl.management;

public class LoginBean implements LoginMBean {
	private static Integer totalLogin = new Integer(0);

	@Override
	public Integer getTotalLogined() {
		return totalLogin;
	}

	@Override
	public void addTotalLogin() {
		synchronized (totalLogin) {
			totalLogin++;
		}
	}
}
