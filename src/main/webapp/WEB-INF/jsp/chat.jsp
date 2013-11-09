<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<!--[if IE 8]> 				 <html class="no-js lt-ie9" lang="en" > <![endif]-->
<!--[if gt IE 8]><!-->
<html class="no-js" lang="en">
<!--<![endif]-->

<head>
<meta charset="utf-8" />
<meta name="viewport" content="width=device-width" />
<link id="favicon" rel="icon" href="favicon.ico" type="image/x-icon">
<title>YourChat</title>
<link rel="stylesheet" href="css/app.css" />
<!--[if lt IE 8]><link rel="stylesheet" href="css/font-awesome-ie7.min.css" /><![endif] -->
<!--[if gt IE 8]><! -->
<link rel="stylesheet" href="css/font-awesome.min.css" />
<link rel="stylesheet" href="css/fixes.css" />
<!--<![endif] -->
<script>
	urlPrefix = "<c:out value="${requestScope.urlPrefix}"/>";
	listenID = "<c:out value="${sessionScope.user.listenId}"/>";
</script>
<script src="js/vendor/custom.modernizr.js"></script>
<script src="js/vendor/jquery.js"></script>
<script src="js/jquery-ui.min.js"></script>
<script src="js/jquery.form.min.js"></script>
<script src="js/jquery-ui.offcanvas.js"></script>
<script src="js/foundation/foundation.js"></script>
<script src="js/foundation/foundation.forms.js"></script>
<script src="js/foundation/foundation.section.js"></script>
<script src="js/foundation/foundation.topbar.js"></script>
<script src="js/foundation/foundation.tooltips.js"></script>
<script src="js/chat.js"></script>
<script src="js/frontend.js"></script>
</head>
<body>
	<header id="header">
		<nav class="top-bar">
			<ul class="title-area">
				<li>
					<a class="general offcanvas-button" style="font-size: 18px; color: white; float: left; padding: 4px;" href="#sidebar"><i class="icon-tasks"></i><span></span></a></li>
				<!-- Title Area -->
				<li class="name">
					<h1>
						<a href="#" id="signalLink"><img src="" id="signal" title="Status"/></a>
						<a href="#">&nbsp;</a>
					</h1>
				</li>
				<!-- Remove the class "menu-icon" to get rid of menu icon. Take out "Menu" to just have icon alone -->
				<li class="toggle-topbar menu-icon"><a href="#"><span></span></a></li>
			</ul>
			<section class="top-bar-section">
				<!-- Right Nav Section -->
				<ul class="right">
					<li><a href="help.html" target="_blank"><i
							class="icon-question"></i>&nbsp;Help</a></li>
					<li class="divider"></li>
					<li><a href="profile.jsp?edit=true" target="chat_profil"><i
							class="icon-edit"></i>&nbsp;Profile</a></li>
					<li><a href="#" data-reveal-id="inviteDialog"><i class="icon-ticket"></i>&nbsp;Invite</a></li>
					<li><a href="#" data-reveal-id="mailInboxDialog"><i class="icon-envelope"></i>&nbsp;Mail</a></li>
					<li><a href="#" id="awayAction"><i class="icon-minus-sign"></i>&nbsp;Away</a></li>
					<li><a href="#" data-reveal-id="settingsDialog"><i class="icon-wrench"></i>&nbsp;Settings</a></li>
					<li><a href="<c:out value="${requestScope.urlPrefix}logout"/>?action=logout&key=<c:out value="${sessionScope.logoutKey}"/>"><i class="icon-power-off"></i>&nbsp;Logout</a></li>
				</ul>
			</section>
		</nav>
	</header>
	<div class="row">
	<div class="large-9 small-12 columns">
		<div id="chat" class="full-height">
			<div id="chat-screen">
			</div>
		</div>
		<div id="chat-input" class="large-12 columns">
			<form id="talkForm" class="custom" action="app/talk" method="post">
				<div class="row">
					<div class="small-12 large-9 columns left">
						<input id="talkText" type="text" name="message">
						<input id="talkFile" type="file" name="talkfile" class="file hidden"/>
					</div>
				</div>
				<div class="row">
					<div class="small-5 large-3 columns left">
						<label for="autoscroll">
						    <input type="checkbox" id="autoscroll"/>&nbsp;Autoscroll
						</label>
					</div>
					<div class="small-5 large-3 columns left">
						<label for="showBackground">
						    <input type="checkbox" id="showBackground"/>&nbsp;Background
						</label>
					</div>
					<div class="small-2 large-3 columns right">
						<div class="right">
							<a id="talkUpload" onclick="javascript:upload()"><i class="icon-upload-alt"></i></a>&nbsp;<i
								class="icon-circle"></i>&nbsp;
						</div>
					</div>
				</div>
			</form>
		</div>
	</div>	
		<div id="sidebar" class="offcanvas left small-12 large-3 columns">
			<div class="section-container auto" data-section data-options="one_up: false; deep_linking: true">
			<section>
				<p class="title" data-section-title>
					<a href="#users"><i class="icon-group"></i>&nbsp;Users</a>
				</p>
				<div id="userList" class="content" data-slug="users" data-section-content>
					<ul id="roomMateList"></ul>
					<ul id="neighbourList"></ul>
				</div>
			</section>
			<section>
				<p class="title" data-section-title>
					<a href="#media"><i class="icon-picture"></i>&nbsp;Media</a>
				</p>
				<div class="content" data-slug="media" data-section-content>
					<ul id="mediaList"></ul>
				</div>
			</section>
			</div>
		</div>
		
	</div>
	
	<div id="settingsDialog" class="reveal-modal">
		<h5>settings</h5>
		<form id="settingsForm" class="custom">
			<div class="row">
				<div class="large-4 columns">
					<label>Nickcolor</label>
					<input id="userColor" value="<c:out value="${sessionScope.user.settings.color}"/>" onchange="userColorChange()"/>
				</div>	    	
			</div>
			<div class="row">
				<div class="large-4 columns">
					<label for="fontSelector">Chatfont</label>
					<select id="fontSelector" onchange="fontChange()" contenteditable="false"></select>
				</div>
			</div>
		</form>
		<a href="#" class="small button" onclick="createKey()">Invite</a> <a
			class="close-reveal-modal">&#215;</a>
	</div>
	
	<div id="inviteDialog" class="reveal-modal">
		<h5>Invitation</h5>
		<form id="inviteForm" class="custom">
			<p>
				<label for="trial1"> <input name="trial" value="true"
					type="radio" id="trial1" style="display: none;" CHECKED> <span
					class="custom radio checked"></span> 24 Hour-Key
				</label> <span class="help-block">The invitee may enter the chat
					within the next 24 hours</span>
			</p>
			<p>
				<label for="trial2"> <input name="trial" value="false"
					type="radio" id="trial2" style="display: none;"> <span
					class="custom radio"></span> Registration
				</label> <span class="help-block">The invitee may register
					permanently</span>
			</p>
			<div style="visibility: hidden">
				<p>Give this key to your invitee</p>
				<span id="keyResult" style="font-weight: bold"></span>
			</div>
		</form>
		<a href="#" class="small button" onclick="createKey()">Invite</a> <a
			class="close-reveal-modal">&#215;</a>
	</div>
	
	<div id="mailInboxDialog" class="reveal-modal">
		<form class="custom">
			<h5>Inbox</h5>
			<table class="mail">
				<thead>
					<tr>
						<td>Subject</td>
						<td>Sender</td>
						<td class="hide-for-small date">Date</td>
						<td class="delete"></td>
					</tr>
				</thead>
				<tbody>
					<tr class="unread">
						<td>Hallo Du</td>
						<td>Horst</td>
						<td class="hide-for-small date">10:00 26.02.2013</td>
						<td class="delete"><input type="checkbox"/></td>
					</tr>
					<tr>
						<td>Doofnuss</td>
						<td>Werner</td>
						<td class="hide-for-small date">10:00 26.02.2013</td>
						<td class="delete"><input type="checkbox"/></td>
					</tr>
					<tr>
						<td>wat geht?!</td>
						<td>Horst</td>
						<td class="hide-for-small date">10:00 26.02.2013</td>
						<td class="delete"><input type="checkbox"/></td>
					</tr>
				</tbody>
				<tfoot>
					<tr>
						<td></td>
						<td></td>
						<td class="hide-for-small"></td>
						<td><i class="icon-trash"></i></td>
					</tr>
				</tfoot>
			</table>
		</form>
		<ul class="button-group even-3">
		  <li><a href="#" class="small button">Inbox</a></li>
		  <li><a href="#" data-reveal-id="mailOutboxDialog" class="small button">Outbox</a></li>
		  <li><a href="#" data-reveal-id="mailSendDialog" class="small button">Send</a></li>
		</ul>
		<a class="close-reveal-modal">&#215;</a>
	</div>
	
	<div id="mailOutboxDialog" class="reveal-modal">
		<form class="custom">
			<h5>Outbox</h5>
			<table class="mail">
				<thead>
					<tr>
						<td>Outbox</td>
						<td>Recipient</td>
						<td class="hide-for-small date">Date</td>
						<td class="delete"></td>
					</tr>
				</thead>
				<tbody>
					<tr class="unread">
						<td>Hallo Du non  sec dolor</td>
						<td>Horst</td>
						<td class="hide-for-small date">10:00 26.02.2013</td>
						<td class="delete"><input type="checkbox"/></td>
					</tr>
					<tr>
						<td>Doofnuss</td>
						<td>Werner</td>
						<td class="hide-for-small date">10:00 26.02.2013</td>
						<td class="delete"><input type="checkbox"/></td>
					</tr>
					<tr>
						<td>wat geht?! sit amet massa</td>
						<td>Horst</td>
						<td class="hide-for-small date">10:00 26.02.2013</td>
						<td class="delete"><input type="checkbox"/></td>
					</tr>
				</tbody>
				<tfoot>
					<tr>
						<td></td>
						<td></td>
						<td class="hide-for-small"></td>
						<td><i class="icon-trash"></i></td>
					</tr>
				</tfoot>
			</table>
		</form>
		<ul class="button-group even-3">
		  <li><a href="#" data-reveal-id="mailInboxDialog" class="small button">Inbox</a></li>
		  <li><a href="#" class="small button">Outbox</a></li>
		  <li><a href="#" data-reveal-id="mailSendDialog" class="small button">Send</a></li>
		</ul>
		<a class="close-reveal-modal">&#215;</a>
	</div>
	
	<div id="mailSendDialog" class="reveal-modal">
		<form class="custom">
			<h5>Send</h5>
			<div class="row">
		      <div class="large-12 columns">
		        <input type="text" placeholder="Recipient">
		      </div>
		    </div>
			<div class="row">
		      <div class="large-12 columns">
		        <input type="text" placeholder="Subject">
		      </div>
		    </div>
			<div class="row">
		      <div class="large-12 columns">
		        <textarea placeholder="Body"></textarea>
		      </div>
		    </div>
		</form>
		<ul class="button-group even-3">
		  <li><a href="#" data-reveal-id="mailInboxDialog" class="small button">Inbox</a></li>
		  <li><a href="#" data-reveal-id="mailOutboxDialog" class="small button">Outbox</a></li>
		  <li><a href="#" class="small button">Send</a></li>
		</ul>
		<a class="close-reveal-modal">&#215;</a>
	</div>
</body>
</html>