package com.esl.service;

import java.sql.SQLException;

public interface IExcelImportService {
	public void importGrades() throws ClassNotFoundException, SQLException;
	public void importPhoneticQuestions() throws ClassNotFoundException, SQLException;
}
