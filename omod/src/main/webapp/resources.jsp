<%@ include file="/WEB-INF/template/include.jsp"%>

<c:forEach var="resource" items="${data}" varStatus="status">
	<tr
		class="d0" 
		<c:if test="${!empty resource.subResources}">id="${resource.name}"</c:if>
		 >

		<td>
            <c:if test="${resource.subResource}">Subresource:</c:if>
            <c:if test="${!empty resource.subResources}"><div class="parentResource expand"></div></c:if>
		    ${resource.name}
            <c:if test="${!empty resource.subtypeHandlerForResourceName}">extends ${resource.subtypeHandlerForResourceName}</c:if>
        </td>
		<td>${resource.url}</td>
	<!-- <td>
		   <c:forEach var="ver" items="${resource.supportedOpenMRSVersion}">
		     ${ver} 
		   </c:forEach>
		</td>-->

		<td>
			<table class="innerTable">
				<c:forEach var="representation" items="${resource.representations}">
					<tr>
						<td>${representation.name}: ${representation.properties}</td>
					</tr>
				</c:forEach>
			</table>
		</td>

	</tr>
	<c:if test="${!empty resource.subResources}">
		<c:set var="data" value="${resource.subResources}" scope="request"/>
		<jsp:include page="resources.jsp"/>
	</c:if>
    <c:if test="${!empty resource.subtypeHandlers}">
        <c:set var="data" value="${resource.subtypeHandlers}" scope="request"/>
        <jsp:include page="resources.jsp"/>
    </c:if>
</c:forEach>