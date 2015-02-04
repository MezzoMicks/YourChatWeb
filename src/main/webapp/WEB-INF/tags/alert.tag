<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setBundle basename="de.deyovi.chat.de.deyovi.chat.web.messages"/>

<section id="alerts"></section>
<c:choose>
	<c:when test="${users ne null}">
		<c:set value="${user.alerts}" var="userAlerts" />
	</c:when>
	<c:otherwise>
		<c:set value="${alerts}" var="userAlerts" />
	</c:otherwise>
</c:choose>
<c:set var="alertObjects">
	<c:forEach items="${alerts}" var="alert" varStatus="i">
		<c:if test="${i.index gt 0}">
			,
		</c:if>
		<%-- TODO translate --%>
		<fmt:message key="${alert.messageCode}" var="message" />
		{ lifespan : "${alert.lifespan.name}", level : "${alert.level.name}" , message : "${message}" }
	</c:forEach>
</c:set>
<script type="text/javascript">
	var userAlerts = [${alertObjects}];
	showAlerts(userAlerts);
</script>