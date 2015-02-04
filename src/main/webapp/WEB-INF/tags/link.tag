<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="href" description="referenced source" required="true" type="java.lang.String" %>
<%@ attribute name="rel" description="relation type" required="true" type="java.lang.String" %>
<%@ attribute name="type" description="data type of reference" required="true" type="java.lang.String" %>
<c:url value="/resources${href}" var="hrefURL" />
<link rel="${rel}" href="${hrefURL}" type="${type}" />