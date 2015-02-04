<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<header id="header">
	<nav class="top-bar">
		<ul class="title-area">
			<li>
				<a class="general offcanvas-button" style="font-size: 18px; color: #111; float: left; padding: 4px;" href="#sidebar"><i class="icon-tasks"></i><span></span></a>
			</li>
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
                <c:url value="/id-edit/" var="urlIdEdit"/>
				<li><a href="${urlIdEdit}" target="chat_profil"><i
						class="icon-edit"></i>&nbsp;Profile</a></li>
				<li><a href="#" data-reveal-id="inviteDialog"><i class="icon-ticket"></i>&nbsp;Invite</a></li>
				<li><a href="#" data-reveal-id="mailInboxDialog"><i class="icon-envelope"></i>&nbsp;Mail</a></li>
				<li><a href="#" id="awayAction"><i class="icon-minus-sign"></i>&nbsp;Away</a></li>
				<li><a href="#" data-reveal-id="settingsDialog"><i class="icon-wrench"></i>&nbsp;Settings</a></li>
				<c:url value="/logout" var="logoutUrl">
					<c:param name="action" value="logout"/>
					<c:param name="key" value="${sessionScope.logoutKey}"/>
				</c:url>
				<li><a href="${logoutUrl}"><i class="icon-power-off"></i>&nbsp;Logout</a></li>
			</ul>
		</section>
	</nav>
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
</header>
