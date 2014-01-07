<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ attribute name="additionalScripts" description="Scripts to be included in head" required="false" type="java.lang.String" %>
<fmt:bundle basename="de.deyovi.chat.web.messages" />
<!DOCTYPE html>
<!--[if IE 8]>
<html class="no-js lt-ie9" lang="en" >
<![endif]-->
<!--[if gt IE 8]><!-->
<html class="no-js" lang="en">
<!--<![endif]-->
	<head>
		<meta charset="utf-8" />
		<meta name="viewport" content="width=device-width" />
		<title>YourChat</title>
		<t:link rel="icon" href="/img/favicon.gif" type="image/gif" />
		<t:link rel="stylesheet" href="/css/app.css" type="text/css" />
		<!--[if lt IE 8]><t:link rel="stylesheet" href="/css/font-awesome-ie7.min.css" type="text/css" /><![endif] -->
		<!--[if gt IE 8]><! -->
		<t:link rel="stylesheet" href="/css/font-awesome.min.css" type="text/css" />
		<t:link rel="stylesheet" href="/css/fixes.css" type="text/css" />
		<!--<![endif] -->
		<script>
			listenID = "<c:out value="${sessionScope.user.listenId}"/>";
		</script>
		<t:script src="/js/vendor/custom.modernizr.js" />
		<t:script src="/js/vendor/jquery.js"/>
		<t:script src="/js/jquery-ui.min.js"/>
		<t:script src="/js/jquery.form.min.js"/>
		<t:script src="/js/jquery-ui.offcanvas.js"/>
		<t:script src="/js/foundation/foundation.js"/>
		<t:script src="/js/foundation/foundation.alerts.js"/>
		<t:script src="/js/foundation/foundation.forms.js"/>
		<t:script src="/js/foundation/foundation.section.js"/>
		<t:script src="/js/foundation/foundation.topbar.js"/>
		<t:script src="/js/foundation/foundation.tooltips.js"/>
		<t:script src="/js/chat.js"/>
		<c:url value="/" var="urlPrefix"></c:url>
		<script type="text/javascript">
			urlPrefix = "${urlPrefix}";
		</script>
		<%--include additional scripts, if present --%>
		<c:if test="${not empty additionalScripts}">
			${additionalScripts}
		</c:if>
	</head>
	<body>
		<section id="alerts">
			
		</section>
		<jsp:doBody />
	</body>
</html>