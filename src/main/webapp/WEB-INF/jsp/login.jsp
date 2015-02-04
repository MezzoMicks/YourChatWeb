<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%-- Localization first --%>
<fmt:bundle basename="de.deyovi.chat.web.messages" prefix="front.">
	<fmt:message key="label.login" var="msgLogin"/>
	<fmt:message key="label.register" var="msgRegister"/>
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
		<script type="text/javascript">
			var keyRequired = ${invitationRequired};
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
            <c:url value="j_spring_security_check" var="loginURL"/>
            <form id="loginForm" class="custom" action="${loginURL}" method="POST">
                <input type="text" name="username" placeholder="${msgUsername}">
                <br />
                <input type="password" name="password" placeholder="${msgPassword}">
                <br />
                <input type="submit" class="button" value="${msgSend}"/>
            </form>
        </div>
    </div>
  <div class="row">
      <div class="large-centered large-4 columns panel">
          <h2>${msgRegister}</h2>
        <c:url value="/register" var="registerURL"/>
         <form:form modelAttribute="registerData" action="${registerURL}" method="POST" class="custom">
            <form:input type="text" path="username" placeholder="${msgUsername}" />
            <br />
            <form:input type="password" path="password" placeholder="${msgPassword}" />
            <br />
            <form:input type="password" path="passwordRepeat" placeholder="${msgPasswordAgain}" />
            <c:if test="${requestScope.keyRequired}">
                <br />
                <form:input type="text" path="invitationKey" placeholder="${msgInviteKey}" />
            </c:if>
            <form:button class="button">${msgSend}</form:button>
        </form:form>
        <br />
    </div>
  </div>
  </jsp:body>
</t:master>