package demo;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

@SuppressWarnings("restriction")
public class HTTPServerDemo {

	final static int DEFAULT_HTTP_SERVER_PORT = 8080;
	final static int HTTP_OK = 200;

	public static void main(String[] args) {
		HttpServer server;
		int port = DEFAULT_HTTP_SERVER_PORT;

		String sport = System.getenv("HTTP_SERVER_PORT");
		if (sport != null && sport.length() > 0) {
			try {
				port = Integer.parseInt(sport);
			} catch (Exception e) {
				System.out.println("Invalid port specified: " + sport);
				System.exit(1);
			}
		}

		try {
			server = HttpServer.create(new InetSocketAddress(port), 0);
			// HttpContext rootContext = server.createContext("/");
			// rootContext.setHandler(HTTPServerDemo::handleRootRequest);
			HttpContext greetContext = server.createContext("/greet");
			greetContext.setHandler(HTTPServerDemo::handleGreetRequest);
			HttpContext timeContext = server.createContext("/gettime");
			timeContext.setHandler(HTTPServerDemo::handleTimeRequest);
			HttpContext convertContext = server.createContext("/convert");
			convertContext.setHandler(HTTPServerDemo::handleConvertRequest);
			server.start();
			System.out.println("Started HTTP server on port " + port);
		} catch (IOException e) {
			System.out.println("Failed to start HTTP server on port " + port);
			System.exit(1);
		}
	}

	static Map<String, String> queryToMap(String query) {
		Map<String, String> result = new HashMap<>();
		for (String param : query.split("&", 0)) {
			String[] entry = param.split("=");
			if (entry.length > 1) {
				result.put(entry[0], entry[1]);
			} else {
				result.put(entry[0], "");
			}
		}
		return result;
	}

	private static void postResponse(int returnCode, String message, HttpExchange exchange) throws IOException {
		exchange.sendResponseHeaders(returnCode, message.getBytes().length);
		OutputStream os = exchange.getResponseBody();
		os.write(message.getBytes());
		os.close();
	}

	private static boolean isNullOrEmpty(String s) {
		return s == null || s.trim().length() == 0;
	}

	// convert?source=inch&target=cm&value=1'
	private static void handleConvertRequest(HttpExchange exchange) throws IOException {
		Map<String, String> params = queryToMap(exchange.getRequestURI().getQuery());
		String source = params.get("source");
		String target = params.get("target");
		String value = params.get("value");
		String message;
		int returnCode = HTTP_OK;
		if (isNullOrEmpty(source) || isNullOrEmpty(target) || isNullOrEmpty(value)) {
			message = "Missing query parameters";
			returnCode = 422;
		} else {
			Double result = 0.0;
			if (source.compareTo("inch") == 0 && target.compareTo("cm") == 0) {
				result = Double.valueOf(value) * 2.54;
				message = Double.toString(result);
			} else {
				message = "Unsupported conversion";
				returnCode = 422;
			}
		}
		postResponse(returnCode, message, exchange);
	}

	private static void handleGreetRequest(HttpExchange exchange) throws IOException {
		String message = "Greetings from the HTTPServerDemo application!\n";
		postResponse(HTTP_OK, message, exchange);
	}

	private static void handleTimeRequest(HttpExchange exchange) throws IOException {
		String hostname = InetAddress.getLocalHost().getHostName();
		if (hostname == null) {
			hostname = InetAddress.getLocalHost().toString();
		}
		String message = "Host: " + hostname + " @ " + Instant.now() + "\n";
		postResponse(HTTP_OK, message, exchange);
	}
}
