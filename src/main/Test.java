package main;
// http://localhost:8080/Test

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Test
 */
@WebServlet("/Test")
public class Test extends HttpServlet {

	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Test() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		List<List<String>> lines = createLines();

		ServletCSVWriter writer = new ServletCSVWriter(response, "test", true);
		writer.addCookie(response);
		writer.setFlushLine(2);
		writer.setMaxLine(1);

		for (List<String> line : lines) {
			sleep(1);
			writer.writeLine(line);
		}
		writer.close();
	}

	private List<List<String>> createLines() {
		List<List<String>> lines = new ArrayList<>();
		lines.add(Arrays.asList("City", "Number", "Date"));
		lines.add(Arrays.asList("Buenos Aires", "2,5", "01/01/2020"));
		lines.add(Arrays.asList("Córdoba\" Aires", "00", "20/04/2020"));
		return lines;
	}

	private void sleep(int second) {
		try {
			Thread.sleep(second * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
