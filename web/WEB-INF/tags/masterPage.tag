<%@tag description="Overall Page Template" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
           
<%@attribute name="title" fragment="true" %>
<%@attribute name="head" fragment="true" %>
<%@attribute name="refresh" type="java.lang.Integer" required="false" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
		<c:if test="${refresh ne null}"><meta http-equiv="refresh" content="${refresh}" /></c:if>
		<link rel="shortcut icon" href="<c:url value='/res/favicon.ico'/>" />
		<title>TBOE - <jsp:invoke fragment="title"/></title>
			
		<script src="<c:url value='/res/Scripts/jquery-1.12.0.js'/>"></script>
		<script src="<c:url value='/res/Scripts/jquery-ui/jquery-ui.js'/>"></script>
		<link rel="stylesheet" href="<c:url value='/res/Scripts/jquery-ui/jquery-ui.css'/>" />
		<script src="<c:url value='/res/Scripts/js.cookie.js'/>"></script>
		
		<script type="text/javascript">
			window.baseSiteUrl = '${pageContext.request.contextPath}';
		</script>
		<script src="<c:url value='/res/Scripts/Global.js'/>"></script>		
		<link rel="stylesheet" href="<c:url value='/res/Styles/Site.css'/>" />		
		<link href='https://fonts.googleapis.com/css?family=Roboto' rel='stylesheet' type='text/css'>
		<link href='https://fonts.googleapis.com/css?family=Roboto+Condensed' rel='stylesheet' type='text/css'>
		
		<jsp:invoke fragment="head"/>
	</head>
  <body>
  	<jsp:include page="../views/include/header.jsp"></jsp:include>
  	
  	<div id="content">
  		<jsp:include page="../views/include/sitemap.jsp"></jsp:include>
	
	    <div id="main">	
	    	<div class="pagetitle">
	    		<span><jsp:invoke fragment="title"/></span>
    		</div>
	    	
	    	<c:if test="${!empty messages}">
	    		<div id="banner">
		    		<c:forEach var="message" items="${messages}">
		    			<p class="message">${message}</p>
		    		</c:forEach>
		    		<br />
				</div>
			</c:if>
			
			<c:if test="${!empty cachedMessages}">
	    		<div id="banner">
		    		<c:forEach var="message" items="${cachedMessages}">
		    			<p class="message">${cachedMessages}</p>
		    		</c:forEach>
		    		<br />
				</div>
			</c:if>
			
			<div id="serverDialog" title="Server Messages"></div>
	    	
	    	<c:catch var="renderError">
	    		<jsp:doBody/>
	    	</c:catch>
	    	<c:if test = "${renderError != null}">
	    		<p>${renderError} <br />
	    		${renderError.message}</p>
	    	</c:if>
	    </div>
	    
	    <jsp:include page="../views/include/sidemenu.jsp" />
  	</div>
    
    <jsp:include page="../views/include/footer.jsp"></jsp:include>
  </body>
</html>