<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%-- Localization first --%>
<fmt:bundle basename="de.deyovi.chat.web.messages" prefix="front.">
	<fmt:message key="label.login" var="msgLogin"/>
	<fmt:message key="label.register" var="msgRegister"/>
	<fmt:message key="label.send" var="msgSend"/>
	<fmt:message key="label.send" var="msgSend"/>
	<fmt:message key="placeholder.username" var="msgUsername"/>
	<fmt:message key="placeholder.password" var="msgPassword"/>
	<fmt:message key="placeholder.passwordagain" var="msgPasswordAgain"/>
	<fmt:message key="placeholder.invitekey" var="msgInviteKey"/>
</fmt:bundle>
<%-- Required Configurations --%>
<fmt:bundle basename="yourchat" prefix="de.deyovi.chat.">
	<fmt:message key="invitation.required" var="invitationRequired"/>
</fmt:bundle>

<t:master>
	<jsp:attribute name="additionalScripts">
		<t:script src="/js/sha256.js" />
		<script type="text/javascript">
			var keyRequired = ${invitationRequired};
			
		
			
			function login() {
				var $form = $('#loginForm');
				var password = $.trim($form.find('input[name="password"]').val());
				password = sha256_digest(password);
				$form.find('input[name="password"]').val('');
				var username = $form.find('input[name="username"]').val();
				var sugar = getSugar(username);
				console.log('retrieved sugar : ' + sugar);
				if (sugar != null && sugar != "") {
					$form.find('input[name="passwordHash"]').val(sha256_digest(sugar + password));
				} else {
					$form.find('input[name="passwordHash"]').val(sha256_digest(password));
				}
				return true;
			}
			
			function register() {
				var $form = $('#registerForm');
				var username = $.trim($form.find('input[name="username"]').val());
				var password = $.trim($form.find('input[name="password"]').val());
				$form.find('input[name="password"]').val('');
				var password2 = $.trim($form.find('input[name="passwordRepeat"]').val());
				$form.find('input[name="passwordRepeat"]').val('');
				if (password == '') {
					return false;
				} else	if (password != password2) {
					$("#registerLabel").html("Supplied passwords differ!");
					return false;
				} else {
					var sugar = getSugar(username);
					console.log("got sugar:" + sugar);
					if (keyRequired) {
						var key = $form.find('input[name="key"]').val();
						$form.find('input[name="keyHash"]').val(sha256_digest(sugar + key));
					}
					$form.find('input[name="passwordHash"]').val(sha256_digest(password));
					return true;
				}
			}
			
			$().ready(function() {
				$(document).foundation();
				if (sha256_self_test() == false) {
					$('#loginForm').html("<strong>Login not possible!</strong>");
					$('#registerForm').html("<strong>Registration not possible!</strong>");
				}
				$('#loginForm').submit(login);
				$('#registerForm').submit(register);
			});
		</script>
	</jsp:attribute>
	<jsp:body>
	  <div class="row">
	    <div class="large-centered large-6 columns text-center">
	    	<t:brand/>
	    </div>
	  </div>
	  <hr />
	  <div class="row">
	    <div class="large-centered large-4 columns">
	        <h2>${msgLogin}</h2>
            <c:url value="/login" var="loginURL"/>
            <form id="loginForm" class="custom" action="${loginURL}" method="POST">
                <input type="text" name="username" placeholder="${msgUsername}">
                <br />
                <input type="password" name="password" placeholder="${msgPassword}">
                <input type="hidden" name="passwordHash" />
                <input type="hidden" name="action" value="login" />
                <input type="submit" class="button" value="${msgSend}"/>
            </form>
        </div>
    </div>
  <div class="row">
      <div class="large-centered large-4 columns panel">
          <h2>${msgRegister}</h2>
        <c:url value="/register" var="registerURL"/>
        <form id="registerForm" class="custom" action="${registerURL}" method="POST">
            <input type="text" name="username" placeholder="${msgUsername}">
            <br />
            <input type="password" name="password" placeholder="${msgPassword}">
            <br />
            <input type="password" name="passwordRepeat" placeholder="${msgPasswordAgain}">
            <input type="hidden" name="passwordHash" />
            <c:if test="${requestScope.keyRequired}">
                <br />
                <input type="text" name="key" placeholder="${msgInviteKey}">
                <input type="hidden" name="keyHash" />
            </c:if>
            <input type="hidden" name="action" value="register" />
            <input type="submit" class="button" value="${msgSend}"/>
        </form>
        <br />
    </div>
  </div>
  </jsp:body>
</t:master>