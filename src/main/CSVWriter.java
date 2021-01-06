package main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Writer for CSV files
 */
public class CSVWriter extends AbstractCVSWriter {

	/**
	 * Create a CSV file at the specified location
	 * 
	 * @param location file location
	 * @param filename filename without extension
	 * @throws IOException throw a exception if the file can't be created
	 */
	public CSVWriter(String location, String filename) throws IOException {
		this(location, filename, false, null);
	}

	/**
	 * Create a CSV file at the specified location with a charset
	 * 
	 * @param location file location
	 * @param filename filename without extension
	 * @param charset  charset used to write data
	 * @throws IOException throw a exception if the file can't be created
	 */
	public CSVWriter(String location, String filename, Charset charset) throws IOException {
		this(location, filename, false, charset);
	}

	/**
	 * Create a CSV file or zipped CSV files at the specified location
	 * 
	 * @param location file location
	 * @param filename filename without extension
	 * @param zipped   indicates if the files will be zipped
	 * @throws IOException throw a exception if the file can't be created
	 */
	public CSVWriter(String location, String filename, boolean zipped) throws IOException {
		this(location, filename, zipped, null);
	}

	/**
	 * Create a CSV file or zipped CSV files at the specified location with a
	 * charset
	 * 
	 * @param location file location
	 * @param filename filename without extension
	 * @param zipped   indicates if the files will be zipped
	 * @param charset  charset used to write data
	 * @throws IOException throw a exception if the file can't be created
	 */
	public CSVWriter(String location, String filename, boolean zipped, Charset charset) throws IOException {
		super(new FileOutputStream(createFile(location, filename, zipped)), filename, zipped, charset);
	}

	/**
	 * Create the file
	 * 
	 * @param location file location
	 * @param filename filename without extension
	 * @param zipped   indicates if the files will be zipped
	 * @return the file
	 */
	private static File createFile(String location, String filename, boolean zipped) {
		location = location.replace('\\', '/');
		if (!location.endsWith("/")) {
			location += "/";
		}
		if (zipped) {
			filename += ZIP;
		} else {
			filename += CSV;
		}
		return new File(location + filename);
	}

}
