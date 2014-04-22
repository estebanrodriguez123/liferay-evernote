<%--
/**
 * Copyright (C) 2005-2014 Rivet Logic Corporation.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; version 3 of the License.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 */
--%>

<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>

<liferay-portlet:actionURL var="savePreferencesURL" portletConfiguration="true"/>

<liferay-ui:success key="success" message="your-request-completed-successfully" />

<aui:form method="post" action="${savePreferencesURL}" name="fm">
	<aui:input name="notesLoaded" label="notes-loaded-per-notebook" type="text" value="${notesLoaded}"/>
	<aui:button type="submit" />
</aui:form>