<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<t:masterPage>
	<jsp:attribute name="title">Login</jsp:attribute>
	
	<jsp:body>
		<form method="POST" action="<c:url value="/login" />">
			Username: <br />
			<input type="text" name="userId" />
			<br />
			Password: <br />
			<input type="password" name="password" />
			<br />
			<input type="submit" name="login" />
		</form>
	</jsp:body>
</t:masterPage>