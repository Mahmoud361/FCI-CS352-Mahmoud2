package com.FCI.SWE.ServicesModels;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;



//import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Transaction;
import com.FCI.SWE.Models.User;
/**
 * <h1>User Entity class</h1>
 * <p>
 * This class will act as a model for user, it will holds user data
 * </p>
 *
 * @author Mohamed Samir
 * @version 1.0
 * @since 2014-02-12
 */
public class UserEntity {
	private String name;
	private String email;
	private String password;
	private String pageName;
	private String pageID;
	private long id;

	/**
	 * Constructor accepts user data
	 * 
	 * @param name
	 *            user name
	 * @param email
	 *            user email
	 * @param password
	 *            user provided password
	 */
	public UserEntity(String name, String email, String password , String pageName , String pageID){
		this.name = name;
		this.email = email;
		this.password = password;
		this.pageID = pageID;
		this.pageName = pageName;
	}
	
	private void setId(long id){
		this.id = id;
	}
	
	public long getId(){
		return id;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public String getPass() {
		return password;
	}
	public String getPageID() {
		return pageID;
	}
	public String getPageName() {
		return pageName;
	}

	
	/**
	 * 
	 * This static method will form UserEntity class using user name and
	 * password This method will serach for user in datastore
	 * 
	 * @param name
	 *            user name
	 * @param pass
	 *            user password
	 * @return Constructed user entity
	 */

	public static UserEntity getUser(String email, String pass) {
		DatastoreService datastore = DatastoreServiceFactory
									.getDatastoreService();
		
		Query gaeQuery = new Query("users");
		
		PreparedQuery pq = datastore.prepare(gaeQuery);
		
		//datastore.delete(gaeQuery.getAncestor());
		for (Entity entity : pq.asIterable()) {
			
			if (entity.getProperty("email").toString().equals(email)
				&& entity.getProperty("password").toString().equals(pass)) 
			{
				
				UserEntity returnedUser = new UserEntity(entity.getProperty(
											"name").toString(), entity.getProperty("email")
											.toString(), entity.getProperty("password").toString(),
											entity.getProperty("pageName").toString() , 
											entity.getProperty("page_ID").toString());
				returnedUser.setId(entity.getKey().getId());
				return returnedUser;
			}
		}

		return null;
	}

	/**
	 * This method will be used to save user object in datastore
	 * 
	 * @return boolean if user is saved correctly or not
	 */
	public Boolean saveUser() {
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		Transaction txn = datastore.beginTransaction();
		if(isFound("users" , "email" , this.email) != null){
			return false;
		}
		System.out.println("true");
		//List<Entity> list = pq.asList(FetchOptions.Builder.withDefaults());
		//System.out.println("Size = " + list.size());
		
		try {
		Entity employee = new Entity("users", this.hashCode());

		employee.setProperty("name", this.name);
		employee.setProperty("email", this.email);
		employee.setProperty("password", this.password);
		employee.setProperty("pageName", this.pageName);
		employee.setProperty("page_ID", this.pageID);
		datastore.put(employee);
		txn.commit();
		}finally{
			if (txn.isActive()) {
		        txn.rollback();
		    }
		}
		return true;

	}

	public static Entity isFound(String tableName,String key , String value) {
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		Query gaeQuery = new Query(tableName);
		PreparedQuery pq = datastore.prepare(gaeQuery);
		for (Entity entity : pq.asIterable()){
			String e ="";
			if(key == "ID"){
				e = String.format("%s", entity.getKey().getId());
			}else{
				e =  String.format("%s",entity.getProperty(key));
			}
			if(e.equals(value)){
				return entity;
			}
		}
		return null;
	}
	public static JSONArray searchName(String name) {
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		Query gaeQuery = new Query("users");
		PreparedQuery pq = datastore.prepare(gaeQuery);
		
		JSONArray names = new JSONArray();
		//ArrayList<Entity>names = new ArrayList<Entity>();
		for (Entity entity : pq.asIterable()){
			JSONObject json = new JSONObject();
			String e = String.format("%s",entity.getProperty("name"));
			if(e.equals(name)){
				//System.out.println("email = "+name+"  e = "+e);
				json.put("user", entity.toString());
				names.add(json);
			}
		}
		return names;
	}
	public static String searchPage(int factor) {
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		Query gaeQuery = new Query("pages");
		PreparedQuery pq = datastore.prepare(gaeQuery);
		List<Entity> list= pq.asList(FetchOptions.Builder.withDefaults());
		//String pages = list.subList(250*factor, 250*(factor+1)-1).toString();
		return list.toString();
	}
	/**
	 * 
	 * @param sender
	 * @param receiver
	 * @return true if send successed
	 */
	public static boolean sendFrequest(String sender , String receiver){
		DatastoreService datastore = DatastoreServiceFactory
									.getDatastoreService();
		
		Transaction txn = datastore.beginTransaction();
		
		try {
		Entity employee = new Entity("friends", sender.hashCode());
		employee.setProperty("sender", sender);
		employee.setProperty("receiver", receiver);
		employee.setProperty("accepted", 0);
		datastore.put(employee);
		txn.commit();
		}finally{
			if (txn.isActive()) {
		        txn.rollback();
		    }
		}
		return true;
	}
	/**
	 * 
	 * @param sender
	 * @param receiver
	 */
	public static void acceptFrequest(String sender, String receiver) {
		DatastoreService datastore = DatastoreServiceFactory
									.getDatastoreService();
		Transaction txn = datastore.beginTransaction();
		Query gaeQuery = new Query("friends");
		PreparedQuery pq = datastore.prepare(gaeQuery);
		for (Entity entity : pq.asIterable()) 
		{
			Object src = entity.getProperty("sender");
			String srcEmail = String.format("%s", src);
			Object ds = entity.getProperty("receiver");
			String dsEmail = String.format("%s", ds);
			System.out.println("src = "+srcEmail+"  ds = "+dsEmail);
			if(sender.equalsIgnoreCase(srcEmail) && receiver.equalsIgnoreCase(dsEmail)){
				System.out.println("if cond true");
				entity.setProperty("accepted", 1);
				datastore.put(entity);
				txn.commit();
			}
		}
	}
	public static User search(){
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Transaction txn = datastore.beginTransaction();
		Query query = new Query("users");
		PreparedQuery pq = datastore.prepare(query);
		User user;
		for (Entity entity : pq.asIterable()){
			//user = new User();
		}
		return null;
	}
	public static JSONArray loadFrinds(String sender){
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query gaeQuery = new Query("friends");
		PreparedQuery pq = datastore.prepare(gaeQuery);
		JSONArray array = new JSONArray();
		for(Entity entity : pq.asIterable()){
			String senderID = entity.getProperty("sender").toString();
			String recieverID = entity.getProperty("reciever").toString();
			String accepted = entity.getProperty("accepted").toString();
				
			if(senderID.equals(sender)){
				//this is sending of friend request
				JSONObject json = new JSONObject(); 
				json.put("accepted" , accepted);
				json.put("recieverID", recieverID);
				Entity user = isFound("users", "ID", recieverID);
				json.put("name", user.getProperty("name").toString());
				array.add(json);
			}else if(recieverID.equals(sender)){
				//this is recieving of friend rquest
				JSONObject json = new JSONObject(); 
				json.put("accepted" , accepted);
				json.put("recieverID", senderID);
				Entity user = isFound("users", "ID", recieverID);
				json.put("name", user.getProperty("name").toString());
				array.add(json);
			}
		}
		return array;
	}
}
