package external;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import entity.Item;
import entity.Item.ItemBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TicketMasterAPI {
	private static final String URL = "https://app.ticketmaster.com/discovery/v2/events.json";
	private static final String DEFAULT_KEYWORD = "";
	private static final String API_KEY = "c4EhkuWI8HdG7wCtZY0fLQ6Hb9czo4qU";
	
	private static final String EMBEDDED = "_embedded";
	private static final String EVENTS = "events";
	private static final String NAME = "name";
	private static final String ID = "id";
	private static final String URL_STR = "url";
	private static final String RATING = "rating";
	private static final String DISTANCE = "distance";
	private static final String VENUES = "venues";
	private static final String ADDRESS = "address";
	private static final String LINE1 = "line1";
	private static final String LINE2 = "line2";
	private static final String LINE3 = "line3";
	private static final String CITY = "city";
	private static final String IMAGES = "images";
	private static final String CLASSIFICATIONS = "classifications";
	private static final String SEGMENT = "segment";
	
	/**
	 * Helper Methods
	 */
	//  {
	//    "name": "laioffer",
              //    "id": "12345",
              //    "url": "www.laioffer.com",
	//    ...
	//    "_embedded": {
	//	    "venues": [
	//	        {
	//		        "address": {
	//		           "line1": "101 First St,",
	//		           "line2": "Suite 101",
	//		           "line3": "...",
	//		        },
	//		        "city": {
	//		        	"name": "San Francisco"
	//		        }
	//		        ...
	//	        },
	//	        ...
	//	    ]
	//    }
	//    ...
	//  }
	private String getAddress(JSONObject event) throws JSONException {
		if (!event.isNull(EMBEDDED)) {
			JSONObject embedded = event.getJSONObject(EMBEDDED);
			
			if (!embedded.isNull(VENUES)) {
				JSONArray venues = embedded.getJSONArray(VENUES);
				
				for (int i=0; i<venues.length(); ++i) {
					JSONObject venue = venues.getJSONObject(i);
					StringBuilder sb = new StringBuilder();
					
					if (!venue.isNull(ADDRESS)) {
						JSONObject address = venue.getJSONObject(ADDRESS);
						
						if (!address.isNull(LINE1)) {
							sb.append(address.getString(LINE1));
						}
						if (!address.isNull(LINE2)) {
							sb.append(",");
							sb.append(address.getString(LINE2));
						}
						if (!address.isNull(LINE3)) {
							sb.append(",");
							sb.append(address.getString(LINE3));
						}
					}
					
					if (!venue.isNull(CITY)) {
						JSONObject city = venue.getJSONObject(CITY);
						
						if (!city.isNull(NAME)) {
							sb.append(",");
							sb.append(city.getString(NAME));
						}
					}
					
					String addr = sb.toString();
					if (!addr.equals("")) {
						return addr;
					}
				}
			}
		}
		return "";
	}
	
	// {"images": [{"url": "www.example.com/my_image.jpg"}, ...]}
	private String getImageUrl(JSONObject event) throws JSONException {
		if (!event.isNull(IMAGES)) {
			JSONArray array = event.getJSONArray(IMAGES);
			for (int i=0; i<array.length(); i++) {
				JSONObject image = array.getJSONObject(i);
				if (!image.isNull(URL_STR)) {
					return image.getString(URL_STR);
				}
			}
		}
		return "";
	}
	
	// {"classifications" : [{"segment": {"name": "music"}}, ...]}
	private Set<String> getCategories(JSONObject event) throws JSONException {
		Set<String> categories = new HashSet<>();
		if (!event.isNull(CLASSIFICATIONS)) {
			JSONArray classifications = event.getJSONArray(CLASSIFICATIONS);
			for (int i=0; i<classifications.length(); ++i) {
				JSONObject classification = classifications.getJSONObject(i);
				if (!classification.isNull(SEGMENT)) {
					JSONObject segment = classification.getJSONObject(SEGMENT);
					if (!segment.isNull(NAME)) {
						categories.add(segment.getString(NAME));
					}
				}
			}
		}
		return categories;
	}
	
	// Convert JSONArray to a list of item objects
	private List<Item> getItemList(JSONArray events) throws JSONException {
		List<Item> itemList = new ArrayList<>();
		for (int i=0; i<events.length(); ++i) {
			JSONObject event = events.getJSONObject(i);
			ItemBuilder builder = new ItemBuilder();
			
			if (!event.isNull(NAME)) {
				builder.setName(event.getString(NAME));
			}
			if (!event.isNull(ID)) {
				builder.setItemId(event.getString(ID));
			}
			if (!event.isNull(URL_STR)) {
				builder.setUrl(event.getString(URL_STR));
			}
			if (!event.isNull(RATING)) {
				builder.setRating(event.getDouble(RATING));
			}
			if (!event.isNull(DISTANCE)) {
				builder.setDistance(event.getDouble(DISTANCE));
			}
			
			builder.setAddress(getAddress(event));
			builder.setCategories(getCategories(event));
			builder.setImageUrl(getImageUrl(event));
			
			itemList.add(builder.build());
		}
		
		return itemList;
	}
	
	/**
	 * search for events
	 * @param lat
	 * @param lon
	 * @param keyword
	 * @return JSONArray including event information
	 */
	public List<Item> search(double lat, double lon, String keyword) {
		// Encode keyword in URL since it may contain special characters(e.g. ' ')
		keyword = encodeKeyword(keyword);
		
		// Convert lat/lon to GeoHash format
		String geoHash = GeoHash.encodeGeohash(lat, lon, 8);
		
		// Create URL "apikey=12345&geoPoint=abcd&keyword=music&radius=50"
		Map<String, String> queryMap = createQueryMap(geoHash, keyword);
		String query = generateQuery(queryMap);
		
		try {
			// open a HTTP connection between your Java application and TicketMastre based URL
			//System.out.println(URL + "?" + query);
			HttpURLConnection connection = (HttpURLConnection) new URL(URL + "?" + query).openConnection();
			// tell what http method to use
			connection.setRequestMethod("GET");
			// get the status code from an HTTP response message
			// To execute the request we can use the getResponseCode(), connect(), getInputStream() or getOutputStream()
			int responseCode = connection.getResponseCode();
			String responseMessage = connection.getResponseMessage();
			System.out.println("\nSending 'GET' request to URL: " + URL + "?" + query);
			System.out.println("Response code: " + responseCode + " " + responseMessage);
			
			StringBuilder response = new StringBuilder();
			// Create a BufferedReader to help read text from a character-input stream
			try(BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
				String inputLine;
				// Append response data to response StringBuilder instance line by line
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				// Close the BufferedReader after reading the inputstream/response data.
				// in.close();
			};

			// Extract events array only
			// Create a JSONObject out of the response string
			JSONObject responseJSON = new JSONObject(response.toString());
			if (responseJSON.isNull("_embedded")) {
				return new ArrayList<>();
			}
			JSONObject embedded = (JSONObject) responseJSON.get("_embedded");
			/*
			if (embedded.isNull("events")) {
				return new JSONArray();
			}*/
			// if finding an embedded obj, then there must be an events
			JSONArray events = (JSONArray) embedded.get("events");
			return getItemList(events);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}
	
	private Map<String, String> createQueryMap(String geoPoint, String keyword) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("apikey", API_KEY);
		map.put("geoPoint", geoPoint);
		map.put("keyword", keyword);
		map.put("radius", "50");
		return map;
	}

	private void queryAPI(double lat, double lon) {
		//JSONArray events = search(lat, lon, null);
		List<Item> itemList = search(lat, lon, null);
		try {
			for (Item item : itemList) {
				JSONObject jsonObject = item.toJSONObject();
				System.out.println(jsonObject.toString(2));
			}
			/*
			for (int i = 0; i < events.length(); ++i) {
				JSONObject event = events.getJSONObject(i);
				System.out.println(event);
			}*/
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String encodeKeyword(String keyword) {
		if (keyword == null) {
			keyword = DEFAULT_KEYWORD;
		}
		try {
			// encode function
			keyword = java.net.URLEncoder.encode(keyword, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return keyword;
	}
	
	private String generateQuery(Map<String, String> map) {
		StringBuilder query = new StringBuilder();
		int cnt = 0;
		for (Map.Entry<String, String> entry : map.entrySet()) {
			if (cnt++ != 0) {
				query.append("&");
			}
			query.append(entry.getKey() + "=" + entry.getValue());
		}
		return query.toString();
	}
	
	/**
	 * Main entry for sample TicketMaster API requests.
	 * @param args
	 */
	public static void main(String[] args) {
		TicketMasterAPI ticketMasterAPI = new TicketMasterAPI();
		// Mountain View, CA
		// ticketMasterAPI.queryAPI(37.38, -122.08);
		// London, UK
		// ticketMasterAPI.queryAPI(51.503364, -0.12);
		// Houston, TX
		ticketMasterAPI.queryAPI(29.682684, -95.295410);
	}
}
