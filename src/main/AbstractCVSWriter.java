package main;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Abstract writer for CSV files
 */
public abstract class AbstractCVSWriter {

	/** constant CSV */
	protected static final String CSV = ".csv";

	/** constant ZIP */
	protected static final String ZIP = ".zip";

	/** constant DEFAULT_TRUNCATE */
	protected static final String TRUNCATE = "The file has been truncated";

	/** constant EMPTY */
	private static final char TEXT_SEPARATOR = '"';

	/** constant COLUMN_SEPARATOR */
	private static final char COLUMN_SEPARATOR = ',';

	/** constant LINE_SEPARATOR */
	private static final char LINE_SEPARATOR = '\n';

	/** constant UNDERSCORE */
	private static final char UNDERSCORE = '_';

	/** stream stream */
	protected OutputStream stream;

	/** filename attribute */
	protected String filename;

	/** maxLine attribute */
	protected int maxLine;

	/** flushLine attribute */
	protected int flushLine;

	/** zipped attribute */
	protected boolean zipped;

	/** charset attribute */
	protected Charset charset;

	/** numberLine attribute */
	private int numberLine;

	/** numberFile attribute */
	private int numberFile;

	/** numberFile attribute */
	private List<String> entetes;

	/** truncate attribute */
	private String truncateMessage;

	/**
	 * Create a CSV or zipped CSV with specific charset
	 * 
	 * @param stream   outpout stream
	 * @param filename filename without extension
	 * @param zipped   indicates if the files will be zipped
	 * @param charset  charset used to write data
	 */
	public AbstractCVSWriter(OutputStream stream, String filename, boolean zipped, Charset charset) {
		if (charset == null) {
			charset = StandardCharsets.UTF_8;
		}
		if (zipped) {
			stream = new ZipOutputStream(stream, charset);
		}
		this.stream = stream;
		this.filename = filename;
		this.zipped = zipped;
		this.charset = charset;
		this.truncateMessage = TRUNCATE;
	}

	/**
	 * Set the maximum number of lines in a CSV. By default no limit.
	 * <p>
	 * If the maximum has been reached then :
	 * </p>
	 * <ul>
	 * <li>Truncate the CSV file if not zipped</li>
	 * <li>Create a new CSV entry in the ZIP if zipped</li>
	 * </ul>
	 * 
	 * @param maxLine the maximum number of lines whitout the header in a CSV file
	 */
	public void setMaxLine(int maxLine) {
		if (maxLine < 0) {
			maxLine = 0;
		}
		this.maxLine = maxLine;
	}

	/**
	 * The flush line parameter is used when writing a line
	 * 
	 * @param flushLine The number of lines to write before flush the response
	 */
	public void setFlushLine(int flushLine) {
		if (flushLine < 0) {
			flushLine = 0;
		}
		this.flushLine = flushLine;
	}

	/**
	 * Change the default truncate message : {@value #TRUNCATE}
	 * 
	 * @param truncateMessage the new message to display when the file is truncated
	 */
	public void setTruncateMessage(String truncateMessage) {
		if (truncateMessage != null) {
			this.truncateMessage = truncateMessage;
		}
	}

	/**
	 * Write a line in the CSV with the default column and text separator
	 * <p>
	 * The first line to have been written is considered like the header and will be
	 * used in the case of a zip with several csv
	 * <p>
	 * 
	 * @param columns The list of data to write
	 * @throws IOException throw a exception if the line could not be written
	 */
	public void writeLine(List<String> columns) throws IOException {
		writeLine(columns, COLUMN_SEPARATOR, TEXT_SEPARATOR);
	}

	/**
	 * Write a line in the CSV with a specific column and text separator
	 * <p>
	 * The first line to have been written is considered like the header and will be
	 * used in the case of a zip with several csv
	 * <p>
	 * 
	 * @param columns   The list of data to write
	 * @param separator the column separator
	 * @param quote     the text separator
	 * @throws IOException throw a exception if the line could not be written
	 */
	public void writeLine(List<String> columns, char separator, char quote) throws IOException {
		if (columns != null) {
			// First line is a header line and create the fisrt entry for zip
			if (numberLine == 0 && numberFile == 0) {
				entetes = columns;
				numberFile++;
				if (zipped) {
					ZipOutputStream zip = (ZipOutputStream) stream;
					addEntry(zip);
				}
			}
			if (maxLine == 0 || numberLine <= maxLine) {
				// Write line to the CSV file
				addLine(columns, separator, quote);
			} else if (zipped) {
				// Create a new entry and add the header line
				ZipOutputStream zip = (ZipOutputStream) stream;
				numberLine = 0;
				numberFile++;
				zip.closeEntry();
				if (flushLine != 0) {
					stream.flush();
				}
				addEntry(zip);
				addLine(entetes, separator, quote);
				addLine(columns, separator, quote);
			} else {
				// Write a break line and the truncate message
				StringBuilder sb = new StringBuilder();
				sb.append(LINE_SEPARATOR);
				sb.append(quote);
				sb.append(truncateMessage);
				sb.append(quote);
				stream.write(sb.toString().getBytes(charset));
			}
		}
	}

	/**
	 * Allows to properly close the stream
	 * 
	 * @throws IOException throw a exception if the stream can't be close
	 */
	public void close() throws IOException {
		if (numberLine != 0) {
			stream.flush();
			if (zipped) {
				ZipOutputStream zip = (ZipOutputStream) stream;
				zip.closeEntry();
				zip.finish();
			}
		}
		stream.close();
	}

	/**
	 * Add a CSV entry in the ZIP
	 * 
	 * @param zip zip output stream
	 * @throws IOException
	 */
	private void addEntry(ZipOutputStream zip) throws IOException {
		ZipEntry entry = new ZipEntry(filename + UNDERSCORE + numberFile + CSV);
		zip.putNextEntry(entry);
	}

	/**
	 * Construct the CSV line and write it in the response
	 * 
	 * @param columns   The list of data to write
	 * @param separator the column separator
	 * @param quote     the text separator
	 * @throws IOException throw a exception if the line could not be written
	 */
	private void addLine(List<String> columns, char separator, char quote) throws IOException {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < columns.size(); i++) {
			String column = columns.get(i);
			String text = String.valueOf(quote);
			if (column.contains(text)) {
				column = column.replace(text, text + text);
			}
			if (i != 0) {
				sb.append(separator);
			}
			sb.append(quote);
			sb.append(column);
			sb.append(quote);
		}
		sb.append(LINE_SEPARATOR);
		stream.write(sb.toString().getBytes(charset));
		numberLine++;
		if (flushLine != 0 && numberLine % flushLine == 0) {
			stream.flush();
		}
	}

}
