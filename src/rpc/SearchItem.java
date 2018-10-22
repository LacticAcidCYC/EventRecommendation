package rpc;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;
import external.TicketMasterAPI;

/**
 * Servlet implementation class SearchItem
 */
@WebServlet("/search")
public class SearchItem extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchItem() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// allow access only if session exists
		HttpSession session = request.getSession(false);
		if (session == null) {
			response.setStatus(403);
			return;
		}
		
		// optional
		String userId = session.getAttribute("user_id").toString();
		
		double lat = Double.parseDouble(request.getParameter("lat"));
		double lon = Double.parseDouble(request.getParameter("lon"));
		//double lat = 37.38;
		//double lon = -122.08;
		
		// term can be empty
		String keyword = request.getParameter("term");
		//String userId = request.getParameter("user_id");
		
		DBConnection connection = DBConnectionFactory.getConnection();
		try {
			JSONArray array = new JSONArray();
			List<Item> items = connection.searchItems(lat, lon, keyword);
			Set<String> favoriteItems = connection.getFavoriteItemIds(userId);
			
			for (Item item : items) {
				JSONObject object = item.toJSONObject();
				object.put("favorite", favoriteItems.contains(item.getItemId()));
				array.put(object);
			}
			RpcHelper.writeJsonArray(response, array);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			connection.close();
		}
		
//		TicketMasterAPI tmAPI = new TicketMasterAPI();
//		List<Item> items = tmAPI.search(lat, lon, keyword);
//		
//		JSONArray array = new JSONArray();
//		try {
//			for (Item item : items) {
//				JSONObject object = item.toJSONObject();
//				array.put(object);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		RpcHelper.writeJsonArray(response, array);
		
		
		//String[] c = new String[]{"Vegetarian", "Music", "Sports"};
//		try {
			//array.put(new JSONObject().put("username", "Eric"));
			//array.put(new JSONObject().put("username", "Joey"));
//			array.put(new JSONObject().put("item_id", "1")
//					.put("favorite", 1)
//					.put("image_url", "https://s3-media3.fl.yelpcdn.com/bphoto/EmBj4qlyQaGd9Q4oXEhEeQ/ms.jpg")
//					.put("url", "https://concerts1.livenation.com/event/090054D9F1BA6F43")
//					.put("name", "Item")
//					.put("categories", c)
//					.put("address", "699 Calderon Ave,Mountain View, CA"));
//			array.put(new JSONObject().put("item_id", "1")
//					.put("favorite", 0)
//					.put("image_url", "https://s1.ticketm.net/dam/a/b7e/46369d46-9b99-4f4a-a973-9558b6d20b7e_781341_RECOMENDATION_16_9.jpg")
//					.put("url", "https://concerts1.livenation.com/event/090054D9F1BA6F43")
//					.put("name", "Item")
//					.put("categories", c)
//					.put("address", "699 Calderon Ave,Mountain View, CA"));
//			array.put(new JSONObject().put("item_id", "1")
//					.put("favorite", 0)
//					.put("image_url", "https://s1.ticketm.net/dam/a/bde/286f0dc3-d068-40f3-85f1-36432ac62bde_708211_ARTIST_PAGE_3_2.jpg")
//					.put("url", "https://concerts1.livenation.com/event/090054D9F1BA6F43")
//					.put("name", "Item")
//					.put("categories", c)
//					.put("address", "699 Calderon Ave,Mountain View, CA"));
//			array.put(new JSONObject().put("item_id", "1")
//					.put("favorite", 1)
//					.put("image_url", "https://s1.ticketm.net/dam/a/54c/427e9e54-aec1-4463-a34e-bb84707fb54c_91341_EVENT_DETAIL_PAGE_16_9.jpg")
//					.put("url", "https://concerts1.livenation.com/event/090054D9F1BA6F43")
//					.put("name", "Item")
//					.put("categories", c)
//					.put("address", "699 Calderon Ave,Mountain View, CA"));
//			array.put(new JSONObject().put("item_id", "1")
//					.put("favorite", 0)
//					.put("image_url", "https://s1.ticketm.net/dam/a/619/8a75854c-f9c0-4353-b6bf-5d0404da0619_748691_RETINA_PORTRAIT_16_9.jpg")
//					.put("url", "https://concerts1.livenation.com/event/090054D9F1BA6F43")
//					.put("name", "Item")
//					.put("categories", c)
//					.put("address", "699 Calderon Ave,Mountain View, CA"));
//			array.put(new JSONObject().put("item_id", "1")
//					.put("favorite", 1)
//					.put("image_url", "https://s3-media3.fl.yelpcdn.com/bphoto/EmBj4qlyQaGd9Q4oXEhEeQ/ms.jpg")
//					.put("url", "https://concerts1.livenation.com/event/090054D9F1BA6F43")
//					.put("name", "Item")
//					.put("categories", c)
//					.put("address", "699 Calderon Ave,Mountain View, CA"));
//			array.put(new JSONObject().put("item_id", "1")
//					.put("favorite", 1)
//					.put("image_url", "https://s3-media3.fl.yelpcdn.com/bphoto/EmBj4qlyQaGd9Q4oXEhEeQ/ms.jpg")
//					.put("url", "https://concerts1.livenation.com/event/090054D9F1BA6F43")
//					.put("name", "Item")
//					.put("categories", c)
//					.put("address", "699 Calderon Ave,Mountain View, CA"));
//			array.put(new JSONObject().put("item_id", "1")
//					.put("favorite", 1)
//					.put("image_url", "https://s3-media3.fl.yelpcdn.com/bphoto/EmBj4qlyQaGd9Q4oXEhEeQ/ms.jpg")
//					.put("url", "https://concerts1.livenation.com/event/090054D9F1BA6F43")
//					.put("name", "Item")
//					.put("categories", c)
//					.put("address", "699 Calderon Ave,Mountain View, CA"));
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		RpcHelper.writeJsonArray(response, array);
		
		/*
		if (request.getParameter("username") != null) {
			String username = request.getParameter("username");
			JSONObject obj = new JSONObject();
			try {
				obj.put("username", username);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			out.print(obj);
		}*/	
			/*
			out.println("<!DOCTYPE html>");
			out.println("<html><head><title>Servlet Test</title></head>");
			out.println("<body>");
			out.println("<h1>This is a HTML page.</h1>");
			out.println("<h2>Hello, " + username + "</h2>");
			out.println("</body>");
			out.println("</html>");*/
		//out.append("This is a HTML page.\n").append("Hello, " + request.getQueryString());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
