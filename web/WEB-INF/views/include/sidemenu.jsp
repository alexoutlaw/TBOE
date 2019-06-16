<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div id="sidemenu" class="mobile">
	<ul>
		<c:if test="${user eq null}">
			<li>
				<div class="sitelink">
					<a href="<c:url value="/login" />">
						<span>Login</span>
					</a>
				</div>
			</li>
		</c:if>
		<c:if test="${user ne null}">
			<li>
				<div class="sitelink">
					<a href="<c:url value="/logout" />">
						<span>Logout</span>
					</a>
				</div>
			</li>
		</c:if>
	</ul>
</div>