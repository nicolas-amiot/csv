package main;

import java.io.IOException;
import java.nio.charset.Charset;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet writer for CSV files
 */
public class ServletCSVWriter extends AbstractCVSWriter {

	/**
	 * Create a CSV response
	 * 
	 * @param response http servlet response
	 * @param filename filename without extension
	 * @throws IOException throw a exception if a error in the response is occurred
	 */
	public ServletCSVWriter(HttpServletResponse response, String filename) throws IOException {
		this(response, filename, false, null);
	}

	/**
	 * @param response http servlet response
	 * @param filename filename without extension
	 * @param charset  charset used to write data
	 * @throws IOException throw a exception if a error in the response is occurred
	 */
	public ServletCSVWriter(HttpServletResponse response, String filename, Charset charset) throws IOException {
		this(response, filename, false, charset);
	}

	/**
	 * @param response http servlet response
	 * @param filename filename without extension
	 * @param zipped   indicates if the files will be zipped
	 * @throws IOException throw a exception if a error in the response is occurred
	 */
	public ServletCSVWriter(HttpServletResponse response, String filename, boolean zipped) throws IOException {
		this(response, filename, zipped, null);
	}

	/**
	 * @param response http servlet response
	 * @param filename filename without extension
	 * @param zipped   indicates if the files will be zipped
	 * @param charset  charset used to write data
	 * @throws IOException throw a exception if a error in the response is occurred
	 */
	public ServletCSVWriter(HttpServletResponse response, String filename, boolean zipped, Charset charset)
			throws IOException {
		super(response.getOutputStream(), filename, zipped, charset);
		setHeaders(response);
	}

	/**
	 * @param response http servlet response
	 */
	public void addCookie(HttpServletResponse response) {
		Cookie cookie = new Cookie("fileDownload", "true");
		cookie.setPath("/");
		cookie.setMaxAge(60);
		response.addCookie(cookie);
	}

	/**
	 * @param response http servlet response
	 */
	private void setHeaders(HttpServletResponse response) {
		if (zipped) {
			response.setHeader("Content-Type", "application/zip");
			response.setHeader("Content-Disposition", "attachment;filename=\"" + filename + ZIP + "\"");
		} else {
			response.setHeader("Content-Type", "text/csv");
			response.setHeader("Content-Disposition", "attachment;filename=\"" + filename + CSV + "\"");
		}
	}

}
