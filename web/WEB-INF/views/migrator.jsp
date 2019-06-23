<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<t:masterPage refresh="5">
	<jsp:attribute name="title">Candidate Migrator</jsp:attribute>
	
	<jsp:body>
		<table class="displayTable">
			<tr>
				<th colspan=4>Migrator Status</th>
			</tr>
			<tr>
				<th>State</th>
				<th>Checks</th>
				<th>Migated</th>
				<th></th>
			</tr>
			<c:set var="action" value="${migrator.state eq 'RUNNING' ? 'STOP' : 'START'}" />
			<c:set var="actionClass" value="${migrator.state eq 'RUNNING' ? 'negative' : 'positive'}" />
			<c:set var="stateClass" value="${migrator.state eq 'RUNNING' ? 'positive' : 'negative'}" />
			<tr>
				<td>
					<span class="${stateClass}">${migrator.state}</span>
				</td>
				<td>${migrator.numLoops}</td>
				<td>${migrator.numMigrated}</td>
				<td>
					<form method="POST" action="<c:url value='/migrator/${action}' />">
						<button class="cardButton ${actionClass}">${action}</button>
					</form>
				</td>
			</tr>
			<tr>
				<th colspan=4>
					Migator Console
				</th>
			</tr>
			<tr>
				<td colspan=4><pre class="scroll500">${migrator.htmlOut}</pre></td>
			</tr>
		</table>
	</jsp:body>
</t:masterPage>