<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<t:masterPage>
	<jsp:attribute name="title">Candidate Migrator</jsp:attribute>
	
	<jsp:body>
		<table class="displayTable">
			<tr>
				<th colspan=3>Migrator Status</th>
			</tr>
			<tr>
				<td>${migrator.state}</td>
				<td>${migrator.numLoops}</td>
				<td>
					<c:set var="action" value="${migrator.state eq 'RUNNING' ? 'stop' : 'start'}" />
					<form method="POST" action="<c:url value='/migrator/${action}' />">
						<button class="cardButton">${action}</button>
					</form>
				</td>
			</tr>
			<tr>
				<td colspan=3>
					<pre class="scroll500">
						${migrator.htmlOut}
					</pre>
				</td>
			</tr>
		</table>
	</jsp:body>
</t:masterPage>