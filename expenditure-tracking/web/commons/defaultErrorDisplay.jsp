<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/messages.tld" prefix="messages" %>

<messages:hasMessages type="WARN">
	<div class="infoop4">
		<messages:showMessages type="WARN"/>
	</div>
</messages:hasMessages>
<messages:hasMessages type="ERROR">
	<div class="error1">
		<messages:showMessages type="ERROR"/>
	</div>
</messages:hasMessages>