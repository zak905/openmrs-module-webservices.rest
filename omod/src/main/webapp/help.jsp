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
	table.resourceData, table.resourceData td, table.resourceData th
	{
        border-collapse: collapse;
        border: 1px solid black;
	}
	
	
	table.resourceData tr.d0 td {
	    background-color: #FCF6CF;
		
	}
	
	table.resourceData tr.d1 td {
		background-color: #FEFEF2;
		/*border: 1px solid black;*/
	}

		table.innerTable, table.innerTable td, table.innerTable tr
	{
	    width:100%;
		border: 0px !important;
		 border-collapse: collapse;
	}
	
	
	
	
	
</style>

<div style="height:20px"></div>
<table id="resourceTable" class="resourceData">
  <tr>
   <th>Resource</th>
   <th>Url</th>
   <th>Representations</th>
  </tr>
  <jsp:include page="resources.jsp" />
</table>
<h2> Search Handlers </h2>

  <jsp:include page="searchResources.jsp" />
</table>

<%@ include file="/WEB-INF/template/footer.jsp"%>