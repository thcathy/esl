package com.esl.service.io;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esl.dao.practice.IIrregularVerbDAO;
import com.esl.entity.practice.qa.IrregularVerb;

public class IrregularVerbExcelImporter implements IExcelImporter {
	private static Logger logger = LoggerFactory.getLogger("ESL");
	private static final String SELECT_SQL = "select [present], [present participle], [past], [past participle] from [IrregularVerb$]";

	private IIrregularVerbDAO irregularVerbDAO;

	@Override
	public String getSQLQuery() {return SELECT_SQL;}

	@Override
	public int startImport(ResultSet rs) {
		int totalRecords = 0;

		try {
			while (rs.next()) {
				IrregularVerb verb = new IrregularVerb(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4));
				irregularVerbDAO.persist(verb);
				logger.debug("Persisted irregular verb [{}]", verb);
				totalRecords++;
			}
		} catch (SQLException e) {
			logger.error("SQL Exception during reading result set", e);
		}
		return totalRecords;
	}

	// ------------------ Getter / Setter --------------- //

	public IIrregularVerbDAO getIrregularVerbDAO() {return irregularVerbDAO;}
	public void setIrregularVerbDAO(IIrregularVerbDAO irregularVerbDAO) {this.irregularVerbDAO = irregularVerbDAO;}

}
