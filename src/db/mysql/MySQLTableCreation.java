package db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class MySQLTableCreation {
	
	// automatically reset our tables in our database
	// Run this as Java application to reset db schema.
	public static void main(String[] args) {
		try {
			// Step 1: Connect to MySQL.
			System.out.println("Connecting to " + MySQLDBUtil.URL);
			Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance();
			Connection connection = DriverManager.getConnection(MySQLDBUtil.URL);
			Statement statement = connection.createStatement();
			
			if (connection == null) {
				return;
			}
			
			// Execute a query
			// Step2: drop the former table
			String drop_t_users = "DROP TABLE IF EXISTS users";
			String drop_t_items = "DROP TABLE IF EXISTS items";
			String drop_t_categories = "DROP TABLE IF EXISTS categories";
			String drop_t_histories = "DROP TABLE IF EXISTS histories";
			statement.executeUpdate(drop_t_categories);
			statement.executeUpdate(drop_t_histories);
			statement.executeUpdate(drop_t_users);
			statement.executeUpdate(drop_t_items);
			
			// Step 3: create new tables
			String create_t_users = "CREATE TABLE users ("
					+ "user_id varchar(255) NOT NULL,"
					+ "password varchar(255) NOT NULL,"
					+ "first_name varchar(255),"
					+ "last_name varchar(255),"
					+ "PRIMARY KEY ( user_id ))";
			
			String create_t_items = "CREATE TABLE items ("
					+ "item_id varchar(255) NOT NULL,"
					+ "name varchar(255),"
					+ "rating float,"
					+ "address varchar(255),"
					+ "image_url varchar(255),"
					+ "url varchar(255),"
					+ "distance float,"
					+ "PRIMARY KEY (item_id))";
			
			String create_t_categories = "CREATE TABLE categories ("
					+ "item_id varchar(255) NOT NULL,"
					+ "category varchar(255) NOT NULL,"
					+ "PRIMARY KEY ( item_id, category ),"
					+ "FOREIGN KEY (item_id) REFERENCES items(item_id))";
			
			String create_t_histories = "CREATE TABLE histories ("
					+ "user_id varchar(255) NOT NULL, "
					+ "item_id varchar(255) NOT NULL, "
					+ "last_favor_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
					+ "PRIMARY KEY ( user_id,item_id ),"
					+ "FOREIGN KEY (user_id) REFERENCES users(user_id),"
					+ "FOREIGN KEY (item_id) REFERENCES items(item_id))";
			
			statement.executeUpdate(create_t_users);
			statement.executeUpdate(create_t_items);
			statement.executeUpdate(create_t_categories);
			statement.executeUpdate(create_t_histories);
			
			// Step 4: insert fake user 1111/3229c1097c00d497a0fd282d586be050
			String insert_user = "INSERT INTO users VALUES ('1111', '3229c1097c00d497a0fd282d586be050', 'John', 'Smith')";
			statement.executeUpdate(insert_user);
			
			System.out.println("Import done successfully");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}



















