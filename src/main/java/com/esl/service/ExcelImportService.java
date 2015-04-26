package com.esl.service;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.esl.dao.IGradeDAO;
import com.esl.dao.IPhoneticQuestionDAO;
import com.esl.model.Grade;
import com.esl.model.PhoneticQuestion;
import com.esl.util.practice.PhoneticQuestionUtil;

@Service("excelImportService")
@Transactional
public class ExcelImportService implements IExcelImportService {
	public static final String DRIVER_NAME = "sun.jdbc.odbc.JdbcOdbcDriver";
	public static final String SQL_SELECT_GRADES = "select title, Lv, [Long Title] from [Grade$]";
	public static final String SQL_SELECT_PHONETIC_QUESTION = "select word, grades, Pic_File_Name from [PhoneticQuestion$]";
	public static final String SQL_SELECT_PASSAGE = "select grade, unit, title, subtitle, [Listening_File_Link], sentences, [Sentences_in_Chinese], [Is_First_line] as FIRSTLINE from [Passage$]";
	public static final String PIC_FILE_FOLDER_PATH = "c:/workspace/ESL/webcontent/images/graphic/word/";

	@Value("${ExcelImportService.DatabaseURL}") private String databaseURL = "jdbc:odbc:Book1";
	@Resource private IGradeDAO gradeDAO = null;
	@Resource private IPhoneticQuestionDAO phoneticQuestionDAO = null;

	public ExcelImportService() {}

	public void setDatabaseURL(String url) { this.databaseURL = url; }
	public void setGradeDAO(IGradeDAO gradeDAO) { this.gradeDAO = gradeDAO; }
	public void setPhoneticQuestionDAO(IPhoneticQuestionDAO phoneitcQuestionDAO) { this.phoneticQuestionDAO = phoneitcQuestionDAO; }

	public void importGrades() throws ClassNotFoundException, SQLException
	{
		Class.forName(DRIVER_NAME);
		Connection con = null;
		try
		{
			con = DriverManager.getConnection(databaseURL);
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(SQL_SELECT_GRADES);
			while (rs.next()) {
				String title = rs.getString(1);
				int level = rs.getInt(2);
				String longTitle = rs.getString(3);
				Grade grade = gradeDAO.getGradeByTitle(title);
				if (grade == null) {
					grade = new Grade(title, level);
					grade.setLongTitle(longTitle);
				}
				else
				{
					grade.setLevel(level);
					grade.setLongTitle(longTitle);
				}
				gradeDAO.makePersistent(grade);
			}
			rs.close();
			stmt.close();
		}
		finally {
			if (con != null)
				con.close();
		}
	}

	public void importPhoneticQuestions() throws ClassNotFoundException, SQLException {
		Class.forName(DRIVER_NAME);
		Connection con = null;
		try
		{
			con = DriverManager.getConnection(databaseURL);
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(SQL_SELECT_PHONETIC_QUESTION);
			while (rs.next()) {
				String word = rs.getString(1).trim();
				String grades = rs.getString(2);
				String gradesArr[] = grades.split("_");				// Grading are seperated by "_"
				String picFileName = rs.getString(3);

				// Make new phonetic question
				PhoneticQuestion question = phoneticQuestionDAO.getPhoneticQuestionByWord(word);
				PhoneticQuestionUtil pqu = new PhoneticQuestionUtil();

				if (question == null) {
					question = new PhoneticQuestion();
					question.setWord(word);
					pqu.findIPA(question);



					System.out.println("New question: " + question + "]");
					if (question.getIPA() != null && question.getPronouncedLink() != null) {
						phoneticQuestionDAO.makePersistent(question);
					}
				}

				// update pic file link
				//	check pic file exist
				if (picFileName != null && !picFileName.equals("")) {
					File f = new File(PIC_FILE_FOLDER_PATH + picFileName + ".jpg");
					if (f.exists()) question.setPicFileName(picFileName + ".jpg");
					else System.out.println("Pic File not found, path[" + f.getAbsolutePath() + "]");
				}

				// Link to the grading of that question
				for (int i=0; i < gradesArr.length; i++) {
					Grade grade = gradeDAO.getGradeByTitle(gradesArr[i]);
					if (grade != null) {
						if (!question.getGrades().contains(grade)) {
							question.addGrades(grade);
						}
					}
				}
			}
			rs.close();
			stmt.close();
		}
		finally {
			if (con != null) con.close();
		}
	}


	public static void main(String[] args)
	{
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("/com/esl/ESL-context.xml");
		IExcelImportService es = (IExcelImportService) ctx.getBean("excelImportService");
		try
		{
			//es.importGrades();
			//es.importPassage();
			es.importPhoneticQuestions();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
