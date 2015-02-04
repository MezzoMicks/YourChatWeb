<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="src" description="" required="true" type="java.lang.String" %>
<c:url value="/resources${src}" var="scriptURL" />
<script type="text/javascript" src="${scriptURL}"></script>