<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="image" required="true" type="de.deyovi.chat.core.objects.Image" %>
<%@ attribute name="editAction" required="false" type="java.lang.String" %>
<%@ attribute name="styleClass" required="false" type="java.lang.String" %>

<c:set var="editable" value="${not empty editAction}" />
<c:choose>
    <c:when test="${image == null}">
        <c:url value="/resources0/img/noimage.png" var="previewURL" />
        <c:url value="/resources/img/noimage.png" var="fullURL" />
    </c:when>
    <c:otherwise>
        <c:url value="/d/db_${image.id}.preview.jpg" var="previewURL" />
        <c:url value="/d/db_${image.id}.jpg" var="fullURL" />
    </c:otherwise>
</c:choose>
<c:set var="alt">
	<c:out value="${image.title}" />
</c:set>
<c:choose>
    <c:when test="${editable}">
        <img id="image${image.id}" class="${styleClass}" src="${previewURL}" alt="${alt}" width="${previewWidth}" height="${previewHeight}" data-editor='{ "type" :"img-upload", "action" : "${editAction}","uploadLabel":"","uploadClass":"icon-upload", "resetLabel" :"", "resetClass" :"icon-remove","submitLabel" :"", "submitClass" :"icon-check"}' />
    </c:when>
    <c:otherwise>
        <img id="image${image.id}" class="${styleClass}" src="${previewURL}" alt="${alt}" width="${previewWidth}" height="${previewHeight}" />
    </c:otherwise>
</c:choose>
