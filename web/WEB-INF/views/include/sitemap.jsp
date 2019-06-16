<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div id="siteMap" class="non-mobile">
	<ul>
		<li>
			<div class="sitelink">
				<a href="<c:url value="/" />">
					<span>Home</span>
				</a>
			</div>
		</li>
		<li>
			<span>Listing</span>
			<ul>
				<li>
					<div class="sitelink">
						<a href="<c:url value="/listing" />">
							<span>Available</span>
						</a>
					</div>
				</li>
				<c:if test="${user ne null}">
					<li>
						<div class="sitelink">
							<a href="<c:url value="/saved" />">
								<span>Saved</span>
							</a>
						</div>
					</li>
				</c:if>
			</ul>
		</li>
		<c:if test="${user ne null && user.role eq 'admin'}">
			<li>
				<span>Admin</span>
				<ul>
					<li>
						<div class="sitelink">
							<a href="<c:url value="/migrator" />">
								<span>Migrator</span>
							</a>
						</div>
					</li>
				</ul>
			</li>
		</c:if>
		<c:if test="${user eq null}">
			<li>
				<div class="sitelink">
					<a href="<c:url value="/register" />">
						<span>Register</span>
					</a>
				</div>
			</li>
		</c:if>
		<c:if test="${user eq null}">
			<li>
				<br />
				<div class="non-mobile">
					<form method="POST" action="<c:url value="/login" />">
						Username: <br />
						<input type="text" name="userId" />
						<br />
						Password: <br />
						<input type="password" name="password" />
						<br />
						<input type="submit" name="login" />
					</form>
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
