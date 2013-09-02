<%@page import="de.deyovi.chat.core.utils.ChatConfiguration"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
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
	<script src="js/vendor/custom.modernizr.js"></script>
	<script src="js/vendor/jquery.js"></script>
	<script src="js/jquery.form.min.js"></script>
	<script src="js/jquery-ui.min.js"></script>
	<script src="js/jquery-ui.offcanvas.js"></script>
	<script src="js/foundation.min.js"></script>
	<script src="js/chat.js"></script>
	<script type="text/javascript">
	var keyRequired = <c:out value="${requestScope.keyRequired}"/>;

	function loginKey(event) {
		if (event.which == 13) {
			login();
		}
	}
	
	function login() {
		if ($('#login').hasClass('active')) {
			var form = $('#loginForm');
			var password = $.trim(form.find('input[name="password"]').val());
			password = sha256_digest(password);
			form.find('input[name="password"]').val('');
			var username = form.find('input[name="username"]').val();
			var sugar = getSugar(username);
			if (sugar != null && sugar != "") {
				form.find('input[name="passwordHash"]').val(sha256_digest(sugar + password));
			} else {
				form.find('input[name="passwordHash"]').val(sha256_digest(password));
			}
			form.submit();
		}
	}
	
	function register() {
		if ($('#registerTab').hasClass('active')) {
			var form = $('#registerForm');
			var username = $.trim(form.find('input[name="username"]').val());
			var password = $.trim(form.find('input[name="password"]').val());
			var password2 = $.trim(form.find('input[name="passwordRepeat"]').val());
			if (password == '') {
				
			} else	if (password != password2) {
				$("#registerLabel").html("Supplied passwords differ!");
			} else {
				var sugar = getSugar(username);
				if (keyRequired) {
					var key = form.find('input[name="key"]').val();
					form.find('input[name="keyHash"]').val(sha256_digest(sugar + key));
				}
				form.find('input[name="passwordHash"]').val(sha256_digest(password));
				form.submit();
			}
		}
	}

	$().ready(function() {
		$(document).foundation();
		if (sha256_self_test() == false) {
			$('#loginForm').html("<strong>Login not possible!</strong>");
			$('#registerForm').html("<strong>Registration not possible!</strong>");
		}
	});
	</script>
</head>

<body>
	  <div class="row">
	    <div class="large-6 columns">
	    <div class="section-container auto" data-section data-options="one_up: true; deep_linking: true">
			<section>
				<p class="title" data-section-title>
					<a href="#login"><i class="icon-group"></i>&nbsp;Login</a>
				</p>
				<div class="content" data-slug="login" data-section-content>
					<c:url value="login" var="loginURL"/>
					<form id="loginForm" class="custom" action="${loginURL}" method="POST">
						<input type="text" name="username" placeholder="Username">
						<br /> 
						<input type="password" name="password" placeholder="Password" onkeydown="loginKey(event)"> 
						<input type="hidden" name="passwordHash" /> 
						<input type="hidden" name="action" value="login" />
						<input type="submit" value="Senden">
					</form>
				</div>
			</section>
			<section>
				<p class="title" data-section-title>
					<a href="#register"><i class="icon-picture"></i>&nbsp;Register</a>
				</p>
				<div class="content" data-slug="register" data-section-content>
					<form id="registerForm" class="custom" action="<c:url value="register"/>" method="POST">
						<input type="text" name="username" placeholder="Username">
						<br /> 
						<input type="password" name="password" placeholder="Password">
						<br /> 
						<input type="password" name="passwordRepeat" placeholder="Password (Again)">
						<input type="hidden" name="passwordHash" /> 
						<br />
						<c:if test="${requestScope.keyRequired}">
							<input type="text" name="key" placeholder="Invite-Key">
							<input type="hidden" name="keyHash" /> 
						</c:if>
						<input type="hidden" name="action" value="register" />
					</form>
					<br />
					<span class="label label-important" id="registerLabel"></span>
				</div>
			</section>
			</div>
	    </div>
	  </div>
</body>
</html>