package com.FCI.SWE.Controller;
import com.google.appengine.api.images.Image;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.spi.ImageInputStreamSpi;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageInputStreamImpl;
import javax.jws.WebService;
import javax.ws.rs.Consumes;
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

import com.FCI.SWE.Models.User;
import com.FCI.SWE.ServicesModels.UserEntity;
import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.images.ImagesService;

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
//@Produces("text/html")
//@Consumes("application/json")
public class UserController {
	/**
	 * Action function to render Signup page, this function will be executed
	 * using url like this /rest/signup
	 * 
	 * @return sign up page
	 */
	@POST
	@Path("/doSearch")
	public String usersList(@FormParam("uname") String name){
		//System.out.println(name);
		String serviceUrl = "http://1-dot-mahmoud20120366.appspot.com/rest/SearchService";
		String urlParameters = "uname=" + name;
		String retJson = Connection.connect(serviceUrl, urlParameters, "POST",
				"application/x-www-form-urlencoded;charset=UTF-8");
		
		return retJson;
	}
	/**
	 * Action function to render Signup page, this function will be executed
	 * using url like this /rest/signup
	 * 
	 * @return sign up page
	 */
	@GET
	@Path("/signup")
	public Response signUp() {
		return Response.ok(new Viewable("/jsp/register")).build();
	}
	/**
	 * Action function to render search page, this function will be executed
	 * using url like this /rest/search
	 * 
	 * @return search page
	 */
	@GET
	@Path("/search")
	public Response search(){
		return Response.ok(new Viewable("/jsp/search")).build();
	}
	/**
	 * Action function to render home page of application, home page contains
	 * only signup and login buttons
	 * 
	 * @return enty point page (Home page of this application)
	 */
	@GET
	@Path("/")
	public Response index() {
		return Response.ok(new Viewable("/jsp/entryPoint")).build();
	}

	/**
	 * Action function to render login page this function will be executed using
	 * url like this /social/login
	 * 
	 * @return login page
	 *
	 */
	@GET
	@Path("/login")
	public Response login() {
		return Response.ok(new Viewable("/jsp/login")).build();
	}

	/**
	 * Action function to response to signup request, This function will act as
	 * a controller part and it will calls RegistrationService to make
	 * registration
	 * 
	 * @param uname
	 *            provided user name
	 * @param email
	 *            provided user email
	 * @param pass
	 *            provided user password
	 * @return Status string
	 */
	@POST
	@Path("/response")
	@Produces({MediaType.TEXT_PLAIN , MediaType.TEXT_HTML})
	public String response(@FormParam("uname") String uname,
			@FormParam("email") String email, @FormParam("password") String pass) {

		String serviceUrl = "http://1-dot-mahmoud20120366.appspot.com/rest/RegistrationService";
		String urlParameters = "uname=" + uname + "&email=" + email
				+ "&password=" + pass;
		String retJson = Connection.connect(serviceUrl, urlParameters, "POST",
				"application/x-www-form-urlencoded;charset=UTF-8");
		JSONParser parser = new JSONParser();
		Object obj;
		try {
			// System.out.println(retJson);
			obj = parser.parse(retJson);
			JSONObject object = (JSONObject) obj;
			if (object.get("Status").equals("OK"))
				return "Registered Successfully";

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			return "error";
			//e.printStackTrace();
		}

		/*
		 * UserEntity user = new UserEntity(uname, email, pass);
		 * user.saveUser(); return uname;
		 */
		return "Failed";
	}
	/**
	 * Action function to response to login request. This function will act as a
	 * controller part, it will calls login service to check user data and get
	 * user from datastore
	 * 
	 * @param uname
	 *            provided user name
	 * @param pass
	 *            provided user password
	 * @return Home page view
	 */
	@POST
	@Path("/home")
	@Produces({MediaType.TEXT_HTML,MediaType.APPLICATION_JSON})
	//@Consumes("application/json")
	public Response home(@FormParam("uname") String uname,
						 @FormParam("password") String pass) 
	{
		String urlParameters = "uname=" + uname + "&password=" + pass;
		String retJson = Connection.connect(
				"http://1-dot-mahmoud20120366.appspot.com/rest/LoginService", urlParameters,
				"POST", "application/x-www-form-urlencoded;charset=UTF-8");

		JSONParser parser = new JSONParser();
		Object obj;
		try {
			
			obj = parser.parse(retJson);
			JSONObject object = (JSONObject) obj;
			if (object.get("Status").equals("Failed"))
				return null;
			Map<String, String> map = new HashMap<String, String>();
			User user = User.getUser(object.toJSONString());
			map.put("name", user.getName());
			map.put("email", user.getEmail());
			map.put("password", user.getPass());
			String ID = String.format("%s", user.getId());
			map.put("ID", ID);
			return Response.ok(new Viewable("/jsp/home", map)).build();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		 * UserEntity user = new UserEntity(uname, email, pass);
		 * user.saveUser(); return uname;
		 */
		return null;

	}
	/**
	 * 
	 * @param sender
	 * @param receiver
	 * Action function to render sendRequest page, this function will be executed
	 * using url like this /social/sendRequest
	 * 
	 * @return sendRequest page
	 */
	@POST
	@Path("/sendrequest")
	@Produces("text/html")
	public Response sendRequest(@FormParam("sender") String sender,
			 					@FormParam("receiver") String receiver) 
	{
		String urlParameters = "sender=" + sender + "&receiver=" + receiver;
		
		String retJson = Connection.connect(
				"http://1-dot-mahmoud20120366.appspot.com/rest/sendFriendRequest", urlParameters,
				"POST", "application/x-www-form-urlencoded;charset=UTF-8");
		
		JSONParser parser = new JSONParser();
		Object obj;
		try {
			obj = parser.parse(retJson);
			JSONObject object = (JSONObject) obj;
			if (object.get("Status").equals("Failed"))
				return null;
			System.out.println("should be done");
			Map<String, String> map = new HashMap<String, String>();
//			User user = User.getUser(object.toJSONString());
			map.put("name", sender);
			map.put("receiver",receiver);
			return Response.ok(new Viewable("/jsp/home", map)).build();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 
	 * @param sender
	 * @param receiver
	 * 
	 * Action function to render acceptRequest page, this function will be executed
	 * using url like this /social/acceptRequest
	 * 
	 * @return acceptRequest page
	 */
	@POST
	@Path("/acceptrequest")
	@Produces("text/html")
	public Response acceptRequest(@FormParam("sender") String sender,
			 					  @FormParam("receiver") String receiver) 
	{
String urlParameters = "sender=" + sender + "&receiver=" + receiver;
		
		String retJson = Connection.connect(
				"http://1-dot-mahmoud20120366.appspot.com/rest/acceptFriendRequest", urlParameters,
				"POST", "application/x-www-form-urlencoded;charset=UTF-8");
		
		JSONParser parser = new JSONParser();
		Object obj;
		try {
			obj = parser.parse(retJson);
			JSONObject object = (JSONObject) obj;
			if (object.get("Status").equals("Failed"))
				return null;
			System.out.println("should be done");
			Map<String, String> map = new HashMap<String, String>();
//			User user = User.getUser(object.toJSONString());
			map.put("name", receiver);
			map.put("receiver",sender);
			return Response.ok(new Viewable("/jsp/home", map)).build();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}
	@POST
	@Path("/createPost")
	public String createPost(@FormParam("reciever")String reciever
			, @FormParam("page")String page , @FormParam("content")String content){
		//System.out.println(name);
		//image.toString();
		String serviceUrl = "http://1-dot-mahmoud20120366.appspot.com/rest/createPost";
		String urlParameters = "reciever="+reciever+"&page="
				+page+"&content="+content;
		String retJson = Connection.connect(serviceUrl, urlParameters, "POST",
				"application/x-www-form-urlencoded;charset=UTF-8");
		
		return retJson;
	}
	@POST
	@Path("/seePost")
	public String seePost(@FormParam("reciever")String reciever
			, @FormParam("postID")String postID){
		//System.out.println(name);
		//image.toString();
		String serviceUrl = "http://1-dot-mahmoud20120366.appspot.com/rest/seePost";
		String urlParameters = "user="+reciever+"&postID="
				+postID;
		String retJson = Connection.connect(serviceUrl, urlParameters, "POST",
				"application/x-www-form-urlencoded;charset=UTF-8");
		
		return retJson;
	}
	@POST
	@Path("/sharePost")
	public String sharePost(@FormParam("user")String user ,
			@FormParam("postID")String postID ,
			@FormParam("reciever")String reciever){
		//System.out.println(name);
		//image.toString();
		String serviceUrl = "http://1-dot-mahmoud20120366.appspot.com/rest/sharePost";
		String urlParameters = "user="+user+"&postID="
				+postID+"&reciever="+reciever;
		String retJson = Connection.connect(serviceUrl, urlParameters, "POST",
				"application/x-www-form-urlencoded;charset=UTF-8");
		
		return retJson;
	}
	@POST
	@Path("/likePost")
	public String likePost(@FormParam("user")String user ,
			@FormParam("postID")String postID){
		//System.out.println(name);
		//image.toString();
		String serviceUrl = "http://1-dot-mahmoud20120366.appspot.com/rest/likePost";
		String urlParameters = "user="+user+"&postID="
				+postID;
		String retJson = Connection.connect(serviceUrl, urlParameters, "POST",
				"application/x-www-form-urlencoded;charset=UTF-8");
		
		return retJson;
	}
	@POST
	@Path("/commentPost")
	public String commentPost(@FormParam("user")String user ,
			@FormParam("postID")String postID,
			@FormParam("comment")String comment){
		//System.out.println(name);
		//image.toString();
		String serviceUrl = "http://1-dot-mahmoud20120366.appspot.com/rest/commentPost";
		String urlParameters = "user="+user+"&postID="
				+postID+"&comment="+comment;
		String retJson = Connection.connect(serviceUrl, urlParameters, "POST",
				"application/x-www-form-urlencoded;charset=UTF-8");
		
		return retJson;
	}
	@POST
	@Path("/createPage")
	public String createPage(@FormParam("user")String user ,
			@FormParam("pageName")String pageName ,
			@FormParam("type")String type){
		//System.out.println(name);
		//image.toString();
		String serviceUrl = "http://1-dot-mahmoud20120366.appspot.com/rest/createPage";
		String urlParameters = "user="+user+"&pageName="
				+pageName+"&type="+type;
		String retJson = Connection.connect(serviceUrl, urlParameters, "POST",
				"application/x-www-form-urlencoded;charset=UTF-8");
		
		return retJson;
	}
	@POST
	@Path("/likePage")
	public String likePage(@FormParam("user")String user ,
			@FormParam("pageName")String pageName){
		//System.out.println(name);
		//image.toString();
		String serviceUrl = "http://1-dot-mahmoud20120366.appspot.com/rest/likePage";
		String urlParameters = "user="+user+"&pageName="
				+pageName;
		String retJson = Connection.connect(serviceUrl, urlParameters, "POST",
				"application/x-www-form-urlencoded;charset=UTF-8");
		
		return retJson;
	}
	@POST
	@Path("/pagePost")
	public String pagePost(@FormParam("pageName")String pageName){
		//System.out.println(name);
		//image.toString();
		String serviceUrl = "http://1-dot-mahmoud20120366.appspot.com/rest/pagePost";
		String urlParameters = "pageName="
				+pageName;
		String retJson = Connection.connect(serviceUrl, urlParameters, "POST",
				"application/x-www-form-urlencoded;charset=UTF-8");
		
		return retJson;
	}
	@POST
	@Path("/hashTagPost")
	public String hashTagPost(@FormParam("tagName")String tagName){
		//System.out.println(name);
		//image.toString();
		String serviceUrl = "http://1-dot-mahmoud20120366.appspot.com/rest/hashTagPost";
		String urlParameters = "tagName="
				+tagName;
		String retJson = Connection.connect(serviceUrl, urlParameters, "POST",
				"application/x-www-form-urlencoded;charset=UTF-8");
		
		return retJson;
	}
	/**
	 * Action function to render logout hyper link, this function will be executed
	 * 
	 * @return login page
	 */
	@GET
	@Path("/logout")
	@Produces("text/html")
	public Response logout() {
		return Response.ok(new Viewable("/jsp/login")).build();
	}
	
	
}