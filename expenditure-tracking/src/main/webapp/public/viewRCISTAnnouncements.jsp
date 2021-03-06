<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/collection-pager" prefix="cp"%>

<%@page import="pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestChecksumFilter"%>
<%@page import="pt.ist.fenixWebFramework.servlets.filters.contentRewrite.GenericChecksumRewriter"%>

<style>
table.rcistTable {
margin: 0;
margin-bottom: 20px;
}
table.rcistTable th {
width: 115px;
}
table.rcistTable th b {
font-weight: normal;
}
table.rcistTable th, table.rcistTable td {
background: #f5f5f5 !important;
padding: 7px 10px;
}

table.rcistTable tr.firstrow th, table.rcistTable tr.firstrow td {
border-top: 2px solid #ccc;
}
</style>


<h2><bean:message key="title.rcist.announcements" bundle="ACQUISITION_RESOURCES"/></h2>

<p>
	<bean:message key="description.rcist.announcements" bundle="ACQUISITION_RESOURCES"/>
</p>

<logic:empty name="announcements">
	<p><em><bean:message key="process.messages.info.noAvailableAnnouncements" bundle="EXPENDITURE_RESOURCES"/>.</em></p>
</logic:empty>

<p class="aright mbottom1">
		<cp:collectionPages url="/viewRCISTAnnouncements.do?method=viewRCIST" 
			pageNumberAttributeName="pageNumber" numberOfPagesAttributeName="numberOfPages" numberOfVisualizedPages="10"/>
</p>

<logic:iterate id="announcement" name="announcements" indexId="Id">
	<div>
	<fr:view name="announcement" schema="viewRCISTAnnouncement">
		<fr:layout name="tabular">
			<fr:property name="classes" value="tstyle2 thnowrap thlight thleft thtop width100pc rcistTable"/>
			<fr:property name="rowClasses" value="firstrow,,"/>
		</fr:layout>
	</fr:view>
	</div>
</logic:iterate>

<p class="aright mtop0">
		<cp:collectionPages url="/viewRCISTAnnouncements.do?method=viewRCIST"
			pageNumberAttributeName="pageNumber" numberOfPagesAttributeName="numberOfPages" numberOfVisualizedPages="10"/>
	</p>
