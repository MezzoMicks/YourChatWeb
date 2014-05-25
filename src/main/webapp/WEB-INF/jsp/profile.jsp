<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<jsp:useBean id="user" type="de.deyovi.chat.core.objects.ChatUser" scope="session" />
<jsp:useBean id="profileUser" type="de.deyovi.chat.core.objects.ChatUser" scope="request" />

<%-- Localization first --%>
<fmt:bundle basename="de.deyovi.chat.web.messages" prefix="front.">
	<fmt:message key="label.login" var="msgLogin" />
	<fmt:message key="label.register" var="msgRegister" />
	<fmt:message key="label.send" var="msgSend" />
	<fmt:message key="label.send" var="msgSend" />
	<fmt:message key="placeholder.username" var="msgUsername" />
	<fmt:message key="placeholder.password" var="msgPassword" />
	<fmt:message key="placeholder.passwordagain" var="msgPasswordAgain" />
	<fmt:message key="placeholder.invitekey" var="msgInviteKey" />
</fmt:bundle>

<%-- Required Configurations --%>
<fmt:bundle basename="yourchat" prefix="de.deyovi.chat.">
	<fmt:message key="invitation.required" var="invitationRequired" />
</fmt:bundle>

<t:master>
    <jsp:attribute name="additionalScripts">
		<t:link rel="stylesheet" href="/css/edit.css" type="text/css" />
        <t:script src="/js/edit.js" />
    </jsp:attribute>
<jsp:body>
	<div class="row">
		<div class="large-12 columns">
            <c:choose>
                <c:when test="${fn:endsWith(profileUser.userName,'s') or fn:endsWith(profileUser.userName,'x')}">
                    <c:set var="suffix" value="'" />
                </c:when>
                <c:otherwise>
                    <c:set var="suffix" value="s" />
                </c:otherwise>
            </c:choose>
            <h1><c:out value="${profileUser.userName}${suffix}" /> ID</h1>
		</div>
	</div>
	<hr />
	<div class="row">
		<div class="large-4 columns">
			<div id="avatar">
                <c:if test="${editMode}">
                    <c:url var="editAction" value="/id-edit/addavatar" />
                </c:if>
                <t:img image="${profileUser.profile.avatar}" editAction="${editAction}" styleClass="avatar-image" />
            </div>
		</div>
        <div class="large-7 large-offset-1 columns">
            <p>${profileUser.profile.additionalInfo}</p>
            <p><t:date date="${profileUser.profile.dateOfBirth}" showAge="true"/></p>
        </div>
	</div>
</jsp:body>
</t:master>