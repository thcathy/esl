package com.esl.service.io;

import java.io.BufferedReader;

public interface ICSVImporter {
	public final static String SEPARATOR = ",";

	/**
	 * @return the file path contains data
	 */
	public String getFilePath();

	/**
	 * import data based on the input reader
	 * @return total record inserted
	 */
	public int startImport(BufferedReader reader);
}
