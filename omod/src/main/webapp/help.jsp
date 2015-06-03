<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>

<%@ include file="localHeader.jsp"%>

<openmrs:require anyPrivilege="View RESTWS, Manage RESTWS" otherwise="/login.htm" redirect="/module/webservices/rest/help.form" />

<h2><spring:message code="webservices.rest.help.title" /></h2>

Main documentation page for the module is on the wiki: 
<a href="https://wiki.openmrs.org/x/xoAaAQ">https://wiki.openmrs.org/display/projects/Webservices.rest+Module</a>

<br/><br/>
 <openmrs:htmlInclude file="/moduleResources/webservices/rest/js/mytestscript.js" />




<style type="text/css">
	table.resourceData, table.resourceData td
	{
        border-collapse: collapse;
	}
	
	table.resourceData th{
	    /* border: 1px solid black; */
	}
	
	table.resourceData tr.d0 td {
	    background-color: #FCF6CF;
		/*border: 1px solid black;*/
		
	}
	
	table.resourceData tr.d1 td {
		background-color: #FEFEF2;
		/*border: 1px solid black;*/
	}
	
	.versionsFieldSet{
	width:30%;
	}
		table.innerTable, table.innerTable td, table.innerTable tr
	{
	    width:100%;
		border: 0px !important;
		 border-collapse: collapse;
	}
	.formVersion{
	  width: 50%;
	}
	.formField{
	  float:left;
	  overflow:hidden;
	}
	
	.collapse{
		background-image: url("<openmrs:contextPath/>/moduleResources/webservices/rest/images/collapse.png");
        height: 30px;
        margin:0px;
        background-repeat: no-repeat;
        overflow:hidden;
        cursor:pointer;
	}
	
	.expand{
       background-image: url("<openmrs:contextPath/>/moduleResources/webservices/rest/images/expand.png");
       height: 30px;
       margin:0px;
       background-repeat: no-repeat;
       overflow:hidden;
       cursor:pointer;
	}
	
	.collapsed{
	display:none;
	}
	
	.expanded{
	  display:table-row;
	}
	
	.subResourceCell{
	   padding-left:60px;
	}
	
	.subResourceRepresention{
	   position:relative;
	   left: 25px;
	}
	
	.resourceText{
	  position: relative;
	  left: 40px;
	  top: -30px;
	}
	
	
</style>

<form:form method="post" modelAttribute="documentationConfiguration">
<fieldset class="versionsFieldSet">
<legend> OpenMRS Versions </legend>
 <!--  <c:forEach var="openMRSVersion" items="${distinctVersions}">
          <span> <input type="checkbox" name="${openMRSVersion}" value="${openMRSVersion}"> ${openMRSVersion} <span>
    </c:forEach>-->
    <form:checkboxes path="selectedVersions" items="${distinctVersions}" />
    <input type="submit" value="Display"/> 
</fieldset>
</form:form>
<div style="height:20px"></div>
<table id="resourceTable" class="resourceData">
  <tr>
   <th>Resource</th>
   <th>Url</th>
  <!--   <th>Version</th>-->
   <th>Representations</th>
  </tr>
  <jsp:include page="resources.jsp" />
</table>
<h2> Search Handlers </h2>

  <jsp:include page="searchResources.jsp" />
</table>

<%@ include file="/WEB-INF/template/footer.jsp"%>