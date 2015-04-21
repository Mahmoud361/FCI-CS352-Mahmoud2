package com.FCI.SWE.ServicesModels;

import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.Query.SortDirection;

public class PostEntity {
	public static void createPost(String reciever , String page , String content){
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Transaction txn = datastore.beginTransaction();
//		Query gaeQuery = new Query("posts");
//		gaeQuery.addSort("date", SortDirection.ASCENDING);
//		PreparedQuery pq = datastore.prepare(gaeQuery);
		int postID = content.hashCode();
		Date date = new Date();
		try {
			Entity employee = new Entity("posts", postID);
			employee.setProperty("page_ID", page);
			employee.setProperty("post_ID", postID);
			employee.setProperty("reciever" , reciever);
			employee.setProperty("content", content);
			employee.setProperty("seen", 0);
//			employee.setProperty("seenBy", "");
//			employee.setProperty("share", "");
//			employee.setProperty("like", "");
			employee.setProperty("numOfComment", 0);
			employee.setProperty("numOfShare", 0);
			employee.setProperty("numOfLike", 0);
			employee.setProperty("date", date);
			datastore.put(employee);
			txn.commit();
		}catch(Exception e){
			System.out.println("false");
		}finally{
			if(txn.isActive()){
				txn.rollback();
			}
		}
		String ID = String.format("%s", postID);
		sharePost(page, ID, reciever);
	}
	public static void seePost(String reciever , String postID){
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Transaction txn = datastore.beginTransaction();
		
		Query gaeQuery = new Query("posts");
		PreparedQuery pq = datastore.prepare(gaeQuery);
		for (Entity entity : pq.asIterable()){
			if(entity.getProperty("post_ID").toString().equals(postID)){
				//String seenBy = entity.getProperty("seenBy").toString();
				String seen = entity.getProperty("seen").toString();
				//update
				int numOfSeen= Integer.parseInt(seen);
				numOfSeen++;
				//seenBy += reciever+"/";
				//set data
				entity.setProperty("seen", numOfSeen);
				//entity.setProperty("seenBy", seenBy);
				datastore.put(entity);
				txn.commit();
				break;
			}
		}
		txn = datastore.beginTransaction();
		gaeQuery = new Query("interaction");
		pq = datastore.prepare(gaeQuery);
		Date date = new Date();
		boolean found = false;
		for (Entity entity : pq.asIterable()){
			if(entity.getProperty("post_ID").toString().equals(postID)
					&& entity.getProperty("user").equals(reciever)){
				found = true;
				entity.setProperty("seenBy", 1);
				entity.setProperty("date", date);
				datastore.put(entity);
				txn.commit();
				break;
			}
		}
		if(!found){
			txn = datastore.beginTransaction();
			Entity employee = new Entity("interaction", postID.hashCode()+reciever.hashCode());
			employee.setProperty("user", reciever);
			employee.setProperty("post_ID", postID);
			employee.setProperty("seenBy", 1);
			employee.setProperty("date", date);
			datastore.put(employee);
			txn.commit();
		}
	}
	public static void sharePost(String user , String postID , String reciever){
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Transaction txn = datastore.beginTransaction();
		
		Query gaeQuery = new Query("posts");
		PreparedQuery pq = datastore.prepare(gaeQuery);
		for (Entity entity : pq.asIterable()){
			if(entity.getProperty("post_ID").toString().equals(postID)){
				
				//String users = entity.getProperty("share").toString();
				//String r = entity.getProperty("reciever").toString();
				String num = entity.getProperty("numOfShare").toString();
				int numOfShare = Integer.parseInt(num);
				//update
				//r += reciever+"/";
				//users += user+"/";
				numOfShare++;
				//set data
				//entity.setProperty("reciever", r);
				//entity.setProperty("share", users);
				entity.setProperty("numOfShare", numOfShare);
				datastore.put(entity);
				txn.commit();
				break;
			}
		}
		txn = datastore.beginTransaction();
		gaeQuery = new Query("interaction");
		pq = datastore.prepare(gaeQuery);
		Date date = new Date();
		boolean found = false;
		for (Entity entity : pq.asIterable()){
			//System.out.println("this = "+entity.getProperty("user"));
			if(entity.getProperty("post_ID").toString().equals(postID)
					&& entity.getProperty("user").equals(user)){
				found = true;
				entity.setProperty("share", 1);
				entity.setProperty("reciever", reciever);
				entity.setProperty("date", date);
				datastore.put(entity);
				txn.commit();
				break;
			}
		}
		if(!found){
			txn = datastore.beginTransaction();
			Entity employee = new Entity("interaction", postID.hashCode()+reciever.hashCode());
			employee.setProperty("user", user);
			employee.setProperty("post_ID", postID);
			employee.setProperty("share", 1);
			employee.setProperty("reciever", reciever);
			employee.setProperty("date", date);
			
			datastore.put(employee);
			txn.commit();
		}
	}
	public static void likePost(String user , String postID){
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Transaction txn = datastore.beginTransaction();
		
		Query gaeQuery = new Query("posts");
		PreparedQuery pq = datastore.prepare(gaeQuery);
		for (Entity entity : pq.asIterable()){
			if(entity.getProperty("post_ID").toString().equals(postID)){
				
				//String users = entity.getProperty("like").toString();
				String num = entity.getProperty("numOfLike").toString();
				int numOfLike = Integer.parseInt(num);
				//update
				//users += user+"/";
				numOfLike++;
				//set data
				//entity.setProperty("like", users);
				entity.setProperty("numOfLike", numOfLike);
				datastore.put(entity);
				txn.commit();
				break;
			}
		}
		txn = datastore.beginTransaction();
		gaeQuery = new Query("interaction");
		pq = datastore.prepare(gaeQuery);
		Date date = new Date();
		boolean found = false;
		for (Entity entity : pq.asIterable()){
			//System.out.println("this = "+entity.getProperty("user"));
			if(entity.getProperty("post_ID").toString().equals(postID)
					&& entity.getProperty("user").equals(user)){
				found = true;
				entity.setProperty("like", 1);
				entity.setProperty("date", date);
				datastore.put(entity);
				txn.commit();
				break;
			}
		}
		if(!found){
			txn = datastore.beginTransaction();
			Entity employee = new Entity("interaction", postID.hashCode()+user.hashCode());
			employee.setProperty("user", user);
			employee.setProperty("post_ID", postID);
			employee.setProperty("like", 1);
			employee.setProperty("date", date);
			datastore.put(employee);
			txn.commit();
		}
	}
	public static void commentPost(String user , String postID , String comment){
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Transaction txn = datastore.beginTransaction();
		
		Query gaeQuery = new Query("posts");
		PreparedQuery pq = datastore.prepare(gaeQuery);
		for (Entity entity : pq.asIterable()){
			if(entity.getProperty("post_ID").toString().equals(postID)){
				
				String num = entity.getProperty("numOfComment").toString();
				int numOfComment = Integer.parseInt(num);
				//update
				//users += user+"/";
				numOfComment++;
				//set data
				entity.setProperty("numOfComment", numOfComment);
				datastore.put(entity);
				txn.commit();
				break;
			}
		}
		txn = datastore.beginTransaction();
		gaeQuery = new Query("interaction");
		pq = datastore.prepare(gaeQuery);
		Date date = new Date();
		boolean found = false;
		for (Entity entity : pq.asIterable()){
			//System.out.println("this = "+entity.getProperty("user"));
			if(entity.getProperty("post_ID").toString().equals(postID)
					&& entity.getProperty("user").equals(user)){
				found = true;
				entity.setProperty("comment", comment);
				entity.setProperty("date", date);
				datastore.put(entity);
				txn.commit();
				break;
			}
		}
		if(!found){
			txn = datastore.beginTransaction();
			Entity employee = new Entity("interaction", postID.hashCode()+user.hashCode());
			employee.setProperty("user", user);
			employee.setProperty("post_ID", postID);
			employee.setProperty("comment", comment);
			employee.setProperty("date", date);
			datastore.put(employee);
			txn.commit();
		}
	}
	public static void hashTagPost(String tagName){		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Transaction txn = datastore.beginTransaction();
		Query gaeQuery = new Query("hasTag");
		gaeQuery.addSort("date", SortDirection.DESCENDING);
		PreparedQuery pq = datastore.prepare(gaeQuery);
		//isFound("hashTag" , "name", tagName)
		if(UserEntity.isFound("hashTag" , "name", tagName) != null){
			for(Entity entity : pq.asIterable()){
				if(entity.getProperty("name").toString().equals(tagName)){
					String post = entity.getProperty("post").toString();
					//update
					int numOfPost = Integer.parseInt(post);
					numOfPost++;
					entity.setProperty("post", numOfPost);
					datastore.put(entity);
					txn.commit();
					break;
				}
			}
		}else{
			
			Date date = new Date();
			try {
				Entity employee = new Entity("hashTag", tagName.hashCode());
				
				employee.setProperty("post", 1);
				employee.setProperty("name", tagName);
				employee.setProperty("date", date);
				datastore.put(employee);
				txn.commit();
			}finally{
				if(txn.isActive()){
					txn.rollback();
				}
			}
		}
	}
 	public static JSONArray loadPosts(String user){
 		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query gaeQuery = new Query("interaction").addSort("date", SortDirection.ASCENDING);
		PreparedQuery pq = datastore.prepare(gaeQuery);
		JSONArray array = new JSONArray();
		
		JSONArray friends = UserEntity.loadFrinds(user);//load friend of current active user
		String pages = PageEntity.loadPages(user);//load pages that current active user like 
		
		for(Entity entity : pq.asIterable()){
			Entity ff = new Entity("");
			//load data post
			String ID = entity.getProperty("post_ID").toString();
			String sharer = entity.getProperty("user").toString();
			String share = entity.getProperty("share").toString();
			String reciever = entity.getProperty("reciever").toString();
			Entity sharerEntity = UserEntity.isFound("users", "ID", sharer);
			String sharerName =	sharerEntity.getProperty("name").toString();
			//make json to search for
			JSONObject friend = new JSONObject();
			friend.put("accepted", "1");
			friend.put("recieverID", sharer);
			friend.put("name", sharerName);
			//
			JSONObject friend1 = new JSONObject();
			friend1.put("accepted", "1");
			friend1.put("senderID", sharer);
			friend1.put("name", sharerName);
			
			JSONObject json = new JSONObject();
			boolean canSee = false;
			if(share == "1"){
				if(reciever.equals("fiends") || reciever.equals("public") ||reciever.equals("fllowers")){
					
					if(friends.contains(friend) || friends.contains(friend1) || pages.contains("/"+sharer+"/")){
						canSee = true;
					}
				}else if(reciever.contains("/"+user+"/")){
					 canSee = true;
				}
				
				if(canSee){
					Entity post =  UserEntity.isFound("posts", "post_ID", ID);
					String creater = post.getProperty("page_ID").toString();
					Entity createrEntity;
					
					if(creater.charAt(0) == 'p'){
						createrEntity = UserEntity.isFound("pages", "page_ID", creater);
					}else{
						createrEntity = UserEntity.isFound("users", "ID", creater);
					}
					String createrName = createrEntity.getProperty("name").toString();
					String privacy = post.getProperty("reciever").toString();
					sharerEntity.removeProperty("password");
					createrEntity.removeProperty("password");
					json.put("creater", createrEntity.toString());
					json.put("sharer", sharerEntity.toString());
					json.put("post", post.toString());
					array.add(json);
				}
			}
		}
		return array;
	}
 	public static JSONArray loadPostsByHashTag(String tagName){
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query gaeQuery = new Query("posts");
		PreparedQuery pq = datastore.prepare(gaeQuery);
		JSONArray posts = new JSONArray();
		for(Entity entity : pq.asIterable()){
			JSONObject json = new JSONObject();
			String content = entity.getProperty("content").toString();
			if(content.contains("#"+tagName)){
				json.put("post", entity.toString());
				posts.add(json);
			}
		}
		return posts;
 	}
}
