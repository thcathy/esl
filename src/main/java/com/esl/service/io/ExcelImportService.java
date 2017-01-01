package com.esl.service.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class ExcelImportService implements IStaticDataImportService {
	private static Logger logger = LoggerFactory.getLogger(ExcelImportService.class);
	private static final String DRIVER_NAME = "sun.jdbc.odbc.JdbcOdbcDriver";
	private static final String CONNECTION_STRING = "jdbc:odbc:Driver={Microsoft Excel Driver (*.xls)};DriverID=22;READONLY=true;DBQ=";
	private String databasePath = "D:/Upload/esl_data.xls";

	private IExcelImporter[] importers;


	@Override
	public void start() {
		logger.debug("Start import data from excel [{}]", databasePath);

		try {
			Class.forName(DRIVER_NAME);
		} catch (ClassNotFoundException e) {
			logger.error("Cannot find driver class", e);
			return;
		}

		Connection con = null;
		ResultSet rs = null;
		Statement stmt = null;
		try
		{
			con = DriverManager.getConnection(CONNECTION_STRING + databasePath,"","");

			// process each importer
			for (IExcelImporter importer : importers) {
				logger.debug("Process SQL [{}]", importer.getSQLQuery());
				stmt = con.createStatement();
				rs = stmt.executeQuery(importer.getSQLQuery());
				int result = importer.startImport(rs);
				logger.debug("Total record process [{}]", result);
				rs.close();
				stmt.close();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (con != null) con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	// ------------------ Getter / Setter --------------- //

	public IExcelImporter[] getImporters() {return importers;}
	public void setImporters(IExcelImporter[] importers) {this.importers = importers;}

	public String getDatabasePath() {return databasePath;}
	public void setDatabasePath(String databasePath) {this.databasePath = databasePath;}

}
