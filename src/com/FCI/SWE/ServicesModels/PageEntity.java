package com.FCI.SWE.ServicesModels;

import java.util.Date;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;

public class PageEntity {
	public static boolean createPage(String user , String pageName , String type){
		if(UserEntity.isFound("pages", "name", pageName) != null){
			return false;
		}
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Transaction txn = datastore.beginTransaction();
		
		Date date = new Date();
		String pageID = "p"+user.hashCode();
		try {
			Entity employee = new Entity("pages", user.hashCode());
			
			employee.setProperty("user_ID", user);
			employee.setProperty("page_ID", pageID);
			employee.setProperty("name", pageName);
			//employee.setProperty("reciever", "");
			employee.setProperty("like", 0);
			employee.setProperty("post", 0);
			employee.setProperty("type", type);
			employee.setProperty("date", date);
			datastore.put(employee);
			txn.commit();
		}finally{
			if(txn.isActive()){
				txn.rollback();
			}
		}
		Query gaeQuery = new Query("users");
		PreparedQuery pq = datastore.prepare(gaeQuery);
		for(Entity entity : pq.asIterable()){
			String ID = String.format("%s" , entity.getKey().getId());
			if(ID.equals(user)){
				entity.setProperty("pageName", pageName);
				entity.setProperty("page_ID", pageID);
				break;
			}
		}
		
		return true;
	}
	public static void likePage(String user , String page){
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Transaction txn = datastore.beginTransaction();
		
		Query gaeQuery = new Query("pages");
		PreparedQuery pq = datastore.prepare(gaeQuery);
		for (Entity entity : pq.asIterable()){
			if(entity.getProperty("page_ID").toString().equals(page)){
				
				//String users = entity.getProperty("reciever").toString();
				String like = entity.getProperty("like").toString();
				//update
				int numOfLike = Integer.parseInt(like);
				numOfLike++;
				//users += user+"/";
				//set data
				//entity.setProperty("reciever", users);
				entity.setProperty("like", numOfLike);
				datastore.put(entity);
				txn.commit();
				break;
			}
		}
		txn = datastore.beginTransaction();
		gaeQuery = new Query("pageLike");
		pq = datastore.prepare(gaeQuery);
		boolean found = false;
		for (Entity entity : pq.asIterable()){
			if(entity.getProperty("user").toString().equals(user)){
				found = true;
				
				String pages = entity.getProperty("pages").toString();
				if(pages.equals("") || pages.equals(null)){
					pages = "/"+page+"/";
				}else{
					pages += page+"/";
				}
				entity.setProperty("pages", pages);
				datastore.put(entity);
				txn.commit();
				break;
			}
		}
		if(!found){
			txn = datastore.beginTransaction();
			Entity employee = new Entity("pageLike", page.hashCode()+user.hashCode());
			employee.setProperty("user", user);
			employee.setProperty("pages", "/"+page+"/");
			datastore.put(employee);
			txn.commit();
		}
	}
	public static void pagePost(String page){
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Transaction txn = datastore.beginTransaction();
		Query gaeQuery = new Query("pages");
		PreparedQuery pq = datastore.prepare(gaeQuery);
		
		for(Entity entity : pq.asIterable()){
			if(entity.getProperty("page_ID").toString().equals(page)){
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
	}
	public static String loadPages(String user){
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query gaeQuery = new Query("pageLike");
		PreparedQuery pq = datastore.prepare(gaeQuery);
		for(Entity entity : pq.asIterable()){
			String ID = entity.getProperty("user").toString();
			if(ID.equals(user)){
				return entity.getProperty("pages").toString();
			}
		}
		return null;
	}
}
