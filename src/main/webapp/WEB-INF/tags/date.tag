<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="date" required="true" type="java.util.Date" %>
<%@ attribute name="showAge" required="false" type="java.lang.Boolean" %>

<fmt:bundle basename="de.deyovi.chat.de.deyovi.chat.web.messages" prefix="date.">
    <fmt:message key="label.agePrefix" var="msgAgePrefix" />
    <fmt:message key="label.ageSuffix" var="msgAgeSuffix" />
    <fmt:message key="pattern" var="pattern" />
</fmt:bundle>
<p data-editor="{">
<c:choose>
    <c:when test="showAge">
        <jsp:useBean id="now" class="java.util.Date" />
        <c:set value="${now.year - profileDate.year}" var="age"/>
        <c:if test="${now.day < profileDate.day}">
            <c:set value="${age - 1}" var="age" />
        </c:if>
        <c:out value="${msgAgePrefix + age + msgAgeSuffix}" />
    </c:when>
    <c:otherwise>
        <fmt:formatDate value="${date}" pattern="${pattern}" />
    </c:otherwise>
</c:choose>
</p>