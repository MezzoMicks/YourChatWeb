<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%-- Localization first --%>
<fmt:bundle basename="de.deyovi.chat.web.messages" prefix="front.">
	<fmt:message key="label.initialization" var="msgLabelInitialization"/>
	<fmt:message key="label.initialize" var="msgLabelInitialize"/>
	<fmt:message key="tooltip.initialize" var="msgTooltipInitialize"/>
	<fmt:message key="label.adminuser" var="msgLabelAdminuser"/>
	<fmt:message key="info.adminhint" var="msgInfoAdminthint"/>
</fmt:bundle>
<t:master>
	<div class="row">
	    <div class="large-centered large-6 columns text-center">
			<t:brand/>
		</div>
	</div>
	<hr />
	<div class="row">
		<div class="large-centered large-6 columns panel">
			<h2>${msgLabelInitialization}</h2>
			<c:url value="/setup/init"  var="urlInitialize" />
			<form action="${urlInitialize}">
				<div class="row">
					<div class="large-12 columns">
						<input type="text" name="username" placeholder="${msgLabelAdminuser}" />
						<p>${msgInfoAdminthint}</p>
					</div>
				</div>
				<hr/>
				<input type="hidden" name="action" value="initialize" />
				<input type="hidden" name="token" value="${token}" />
				<input type="submit" class="button small" value="${msgLabelInitialize}" title="${msgTooltipInitialize}">
			</form>
		</div>
	</div>
</t:master>