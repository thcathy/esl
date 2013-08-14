package com.esl.service.io;

import java.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CSVImportService implements IStaticDataImportService {
	private static Logger logger = LoggerFactory.getLogger("ESL");

	private ICSVImporter[] importers;

	@Override
	public void start() {
		try
		{
			// process each importer
			for (ICSVImporter importer : importers) {
				logger.debug("Process [{}] from file [{}]", importer.getClass().toString(), importer.getFilePath());
				FileInputStream fstream = new FileInputStream(importer.getFilePath());
				DataInputStream in = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				int result = importer.startImport(br);
				logger.debug("Total record processed [{}]", result);
				in.close();
			}
		} catch (IOException e) {
			logger.warn("IOException during importing", e);
		}
	}

	// ------------------ Getter / Setter --------------- //

	public void setImporters(ICSVImporter[] importers) {this.importers = importers;}
	public ICSVImporter[] getImporters() {return importers;}

}
