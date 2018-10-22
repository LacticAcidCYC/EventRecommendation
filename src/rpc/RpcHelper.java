package rpc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

public class RpcHelper {
	// Writes a JSONArray to http response.
	public static void writeJsonArray(HttpServletResponse response, JSONArray array) throws IOException{
		// set Content Type
		response.setContentType("application/json");
		// front end need
		response.addHeader("Access-Control-Allow-Origin", "*");
		// get writer
		PrintWriter out = response.getWriter();
		out.print(array);
		out.close();
	}

	// Writes a JSONObject to http response.
	public static void writeJsonObject(HttpServletResponse response, JSONObject obj) throws IOException {
		response.setContentType("application/json");
		response.addHeader("Access-Control-Allow-Origin", "*");
		PrintWriter out = response.getWriter();
		out.print(obj);
		out.close();
	}
	
	// Parses a JSONObject from http request.
	public static JSONObject readJsonObject(HttpServletRequest request) {
		StringBuilder builder = new StringBuilder();
		
		try (BufferedReader reader = request.getReader()) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
			return new JSONObject(builder.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new JSONObject();
	}
}








