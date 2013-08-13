package com.esl.service.io;

import java.sql.ResultSet;

public interface IExcelImporter {
	/**
	 * @return the sql string to extract data from excel file
	 */
	public String getSQLQuery();

	/**
	 * import data based on the input result set
	 * @return total record inserted
	 */
	public int startImport(ResultSet rs);
}
