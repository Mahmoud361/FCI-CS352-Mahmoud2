package com.FCI.SWE.Services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.mvc.Viewable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.FCI.SWE.ServicesModels.PageEntity;
import com.FCI.SWE.ServicesModels.PostEntity;
import com.FCI.SWE.ServicesModels.UserEntity;
import com.google.appengine.api.datastore.Entity;

/**
 * This class contains REST services, also contains action function for web
 * application
 * 
 * @author Mohamed Samir
 * @version 1.0
 * @since 2014-02-12
 *
 */
@Path("/")
@Produces(MediaType.TEXT_PLAIN)
public class UserServices {
	
	
	

		/**
	 * Registration Rest service, this service will be called to make
	 * registration. This function will store user data in data store
	 * 
	 * @param uname
	 *            provided user name
	 * @param email
	 *            provided user email
	 * @param pass
	 *            provided password
	 * @return Status json
	 */
	@POST
	@Path("/RegistrationService")
	public String registrationService(@FormParam("uname") String uname,
			@FormParam("email") String email, @FormParam("password") String pass) {
		UserEntity user = new UserEntity(uname, email, pass , "" , "");
		JSONObject object = new JSONObject();
		if(user.saveUser()){
			object.put("Status", "OK");
		}else{
			object.put("Status", "Faild");
		}
		
		return object.toString();
	}
	@POST
	@Path("/SearchService")
	public String searchUser(@FormParam("uname") String name) {
		JSONArray arr = UserEntity.searchName(name);
		
		return arr.toString();
	}
	@POST
	@Path("/SearchPages")
	public String searchPages(@FormParam("factor") int factor){
		
		return UserEntity.searchPage(factor);
	}
	/**
	 * Login Rest Service, this service will be called to make login process
	 * also will check user data and returns new user from datastore
	 * @param uname provided user name
	 * @param pass provided user password
	 * @return user in json format
	 */
	@POST
	@Path("/LoginService")
	public String loginService(@FormParam("uname") String uname,
			@FormParam("password") String pass) {
		JSONObject object = new JSONObject();
		UserEntity user = UserEntity.getUser(uname, pass);
		if (user == null) {
			object.put("Status", "Failed");

		} else {
			object.put("Status", "OK");
			object.put("name", user.getName());
			object.put("email", user.getEmail());
			object.put("password", user.getPass());
			object.put("id", user.getId());
			object.put("pageName", user.getPageName());
			object.put("page_ID", user.getPageID());
		}
		return object.toString();
	}
	@POST
	@Path("/sendFriendRequest")
	public String sendRequest(@FormParam("sender") String sender,
							  @FormParam("receiver") String receiver) 
	{
		UserEntity.sendFrequest(sender, receiver);
		JSONObject object = new JSONObject();
		object.put("Status", "OK");
		return object.toString();
	}
	@POST
	@Path("/acceptFriendRequest")
	public String acceptRequest(@FormParam("sender") String sender,
							    @FormParam("receiver") String receiver) 
	{
		UserEntity.acceptFrequest(sender, receiver);
		JSONObject object = new JSONObject();
		object.put("Status", "OK");
		return object.toString();
	}
	@POST
	@Path("/createPost")
	public String createPost(@FormParam("reciever")String reciever
			, @FormParam("page")String page , @FormParam("content")String content){
		PostEntity.createPost(reciever, page, content);
		
		JSONObject object = new JSONObject();
		object.put("Status", "OK");
		return object.toString();
	}
	@POST
	@Path("/seePost")
	public String seePost(@FormParam("user")String user ,
			@FormParam("postID")String postID){
		
		PostEntity.seePost(user, postID);
		
		JSONObject object = new JSONObject();
		object.put("Status", "OK");
		return object.toString();
	}
	@POST
	@Path("/sharePost")
	public String sharePost(@FormParam("user")String user ,
			@FormParam("postID")String postID ,
			@FormParam("reciever")String reciever){
		
		PostEntity.sharePost(user, postID, reciever);
		
		JSONObject object = new JSONObject();
		object.put("Status", "OK");
		return object.toString();
	}
	@POST
	@Path("/likePost")
	public String likePost(@FormParam("user")String user ,
			@FormParam("postID")String postID){
		
		PostEntity.likePost(user, postID);
		
		JSONObject object = new JSONObject();
		object.put("Status", "OK");
		return object.toString();
	}
	@POST
	@Path("/commentPost")
	public String commentPost(@FormParam("user")String user ,
			@FormParam("postID")String postID,
			@FormParam("comment")String comment){
		
		PostEntity.commentPost(user, postID , comment);
		
		JSONObject object = new JSONObject();
		object.put("Status", "OK");
		return object.toString();
	}
	@POST
	@Path("/createPage")
	public String createPage(@FormParam("user")String user ,
			@FormParam("pageName")String pageName ,
			@FormParam("type")String type){
		
		JSONObject object = new JSONObject();
		if(PageEntity.createPage(user, pageName, type)){
			object.put("Status", "OK");
		}else{
			object.put("Status", "Faild");
		}
		
		return object.toString();
	}
	@POST
	@Path("/likePage")
	public String likePage(@FormParam("user")String user ,
			@FormParam("pageName")String pageName){
		PageEntity.likePage(user, pageName);
		
		JSONObject object = new JSONObject();
		object.put("Status", "OK");
		return object.toString();
	}
	@POST
	@Path("/pagePost")
	public String pagePost(@FormParam("pageName")String pageName){
		
		PageEntity.pagePost(pageName);
		
		JSONObject object = new JSONObject();
		object.put("Status", "OK");
		return object.toString();
	}
	@POST
	@Path("/hashTagPost")
	public String hashTagPost(@FormParam("tagName")String tagName){
		
		PostEntity.hashTagPost(tagName);
		
		JSONObject object = new JSONObject();
		object.put("Status", "OK");
		return object.toString();
	}
	@POST
	@Path("/loadFriends")
	public String loadFriends(@FormParam("user")String user){
		
		return UserEntity.loadFrinds(user).toJSONString();
	}
	@POST
	@Path("/loadPages")
	public String loadPagess(@FormParam("user")String user){
		
		return PageEntity.loadPages(user);
	}
	@POST
	@Path("/loadPosts")
	public String loadPosts(@FormParam("user")String user){
		
		return PostEntity.loadPosts(user).toJSONString();
	}
	@POST
	@Path("/loadPostByHashTag")
	public String loadPostsByHashTag(@FormParam("tagName")String tagName){
		return PostEntity.loadPostsByHashTag(tagName).toJSONString();
	}
}