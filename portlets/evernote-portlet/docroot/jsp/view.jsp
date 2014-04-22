<%--
 /*
 * Copyright (C) 2005-2014 Rivet Logic Corporation.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; version 2
 * of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA 02110-1301, USA.
 */
--%>

<%@ include file="/jsp/init.jsp"%>

<liferay-ui:error exception="<%=SystemException.class %>"><liferay-ui:message key="system-exception" /></liferay-ui:error>
<liferay-ui:error exception="<%=EDAMUserException.class %>"><liferay-ui:message key="evernote-error" /></liferay-ui:error>
<liferay-ui:error exception="<%=EDAMSystemException.class %>"><liferay-ui:message key="evernote-error" /></liferay-ui:error>
<liferay-ui:error exception="<%=TException.class %>"><liferay-ui:message key="evernote-error" /></liferay-ui:error>
<liferay-ui:error exception="<%=EDAMNotFoundException.class %>"><liferay-ui:message key="evernote-error" /></liferay-ui:error>
<liferay-ui:error exception="<%=NoNoteException.class %>"><liferay-ui:message key="no-note-selected" /></liferay-ui:error>

<c:if test="<%= themeDisplay.isSignedIn() %>">
	<c:choose>
		<c:when test="${needAuthorize}">	
			<%@ include file="/jsp/include/authorization.jspf" %>
		</c:when>
		<c:otherwise>
			<%@ include file="/jsp/include/evernote.jspf" %> 
		</c:otherwise>
	</c:choose>
</c:if>

