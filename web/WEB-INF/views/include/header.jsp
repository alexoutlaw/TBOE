<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div id="header">
	<div class="titlewrapper">
		<div class="sitetitle">
			<span>ED - TBOE</span>
		</div>
		<div>
			<img src="<c:url value="/res/Images/expand.png" />" class="imglink mobile" onclick="toggleMobileMenu()" />
		</div>
	</div>

	<div>
		<img src="<c:url value="/res/Images/search.png" />" class="imglink mobile" />
	</div>
	
	<div>
		<img src="<c:url value="/res/Images/bugs.png" />" class="imglink mobile" />
	</div>
	
	<div>
		<img src="<c:url value="/res/Images/more.png" />" class="imglink mobile" onclick="toggleSideMenu()" />
	</div>
</div>
	