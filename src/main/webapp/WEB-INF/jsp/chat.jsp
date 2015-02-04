<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%-- Localization first --%>
<fmt:bundle basename="de.deyovi.chat.de.deyovi.chat.web.messages" prefix="front.">
	<fmt:message key="label.media" var="msgMedia"/>
	<fmt:message key="label.users" var="msgUsers"/>
	<fmt:message key="label.background" var="msgBackground"/>
	<fmt:message key="label.autoscroll" var="msgAutoscroll"/>
</fmt:bundle>
<t:master>
	<jsp:attribute name="additionalScripts">
		<script src="js/frontend.js"></script>
	</jsp:attribute>
	<jsp:body>
		<t:header />
		<div class="row">
		<div class="large-9 small-12 columns">
			<div id="chat" class="full-height">
				<div id="chat-screen">
				</div>
			</div>
			<div id="chat-input" class="large-12 columns">
				<c:url value="/talk" var="talkURL"/>
				<form id="talkForm" class="custom" action="${talkURL}" method="post">
					<div class="row">
						<div class="small-12 large-9 columns left">
							<input id="talkText" type="text" name="message">
							<input id="talkFile" type="file" name="talkfile" class="file hidden"/>
						</div>
					</div>
					<div class="row">
						<div class="small-5 large-3 columns left">
							<label for="autoscroll">
							    <input type="checkbox" id="autoscroll" checked="checked"/>&nbsp;${msgAutoscroll}
							</label>
						</div>
						<div class="small-5 large-3 columns left">
							<label for="showBackground">
							    <input type="checkbox" id="showBackground" checked="checked"/>&nbsp;${msgBackground}
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
				<div class="section-container auto" data-section data-section-resized data-options="one_up: false; deep_linking: true">
				<section>
					<p class="title" data-section-title>
						<a href="#users"><i class="icon-group"></i>&nbsp;${msgUsers}</a>
					</p>
					<div id="userList" class="content" data-slug="users" data-section-content>
						<ul id="roomMateList"></ul>
						<ul id="neighbourList"></ul>
					</div>
				</section>
				<section>
					<p class="title" data-section-title>
						<a href="#media"><i class="icon-picture"></i>&nbsp;${msgMedia}</a>
					</p>
					<div class="content" data-slug="media" data-section-content>
						<ul id="mediaList"></ul>
					</div>
				</section>
				</div>
			</div>
		</div>
	</jsp:body>
</t:master>