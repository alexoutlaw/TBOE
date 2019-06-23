<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<t:masterPage>
	<jsp:attribute name="title">Available Listing</jsp:attribute>
	
	<jsp:body>
		<table class="displayTable">
			<tr>
				<th colspan=6>
					Candidates
				</th>
			</tr>
			<tr>
				<th>Name</th>
				<th>Body ID</th>
				<th>System ID</th>
				<th>Criteria</th>
				<th></th>
			</tr>
			<c:forEach var="model" items="${models}">
				<tr>
					<td>${model.displayName}</td>
					<td>${model.bodyId}</td>
					<td>${model.systemId}</td>
					<td>${model.criteria}</td>
					<td>
						<c:if test="${user ne null}">
							<form method="POST" action="<c:url value="/listing/checkout/${model.systemId}/${model.bodyId}" />">
								<button class="cardButton" type="submit">Checkout</button>
							</form>
						</c:if>
					</td>
				</tr>
			</c:forEach>
		</table>
	</jsp:body>
</t:masterPage>