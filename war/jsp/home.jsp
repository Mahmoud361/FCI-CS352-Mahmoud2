<%@ page language="java" contentType="text/html; charset=windows-1256"
    pageEncoding="windows-1256"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=windows-1256">
<title>Insert title here</title>
</head>

<SCRIPT type="text/javascript">
//window.history.forward();
function noBack() { window.history.forward(); }
</SCRIPT>

<body >
<p> Welcome b2a ya ${it.name} </p>
<p>your ID is ${it.ID}</p>
<p> This is should be user home page </p>
<p> Current implemented services "http://fci-swe-apps.appspot.com/rest/RegistrationService --- {requires: uname, email, password}" </p>
<p> and "http://fci-swe-apps.appspot.com/rest/LoginService --- {requires: uname,  password}" </p>
<p> you should implement sendFriendRequest service and addFriend service

<form action="/social/sendrequest" method="post">
	Send Friend Request:-
	    	   <input type="hidden" name="sender" value=${it.name}  readonly /> <br>
  	Receiver:  <input type="text" name="receiver" />
  			   <input type="submit" value="Send"> <br><br>
  
</form>

<form action="/social/acceptrequest" method="post">
	Accept Friend Request:-
	    	   <input type="hidden" name="receiver" value=${it.name}  readonly /> <br>
  	Request Sender :  <input type="text" name="sender" />
  			   		  <input type="submit" value="Accept"> <br><br>
 
</form>
<form action="/social/createPost" method="post">
	<input type="hidden" name="page" value=${it.ID}  readonly /> <br>
  	who see this post :  
  	<input type="text" name="reciever" />
  	<br>
  	<pr>say something____<pr>
  	<input type="text" name="content"/>
  	<input type="submit" value="sumit"> <br><br>
 
</form>
<form onload="noBack();"
onpageshow="if (event.persisted) noBack();" onunload="">

<a href='http://1-dot-mahmoud20120366.appspot.com/social/login/' >logout</a>
</form>

</body>
</html>