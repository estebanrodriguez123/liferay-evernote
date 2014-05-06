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

<liferay-ui:error exception="<%=SystemException.class %>" message="system-error" />
<liferay-ui:error exception="<%=EDAMUserException.class %>" message="evernote-error" />
<liferay-ui:error exception="<%=EDAMSystemException.class %>" message="evernote-error" />
<liferay-ui:error exception="<%=TException.class %>" message="evernote-error" />
<liferay-ui:error exception="<%=EDAMNotFoundException.class %>" message="evernote-error" />
<liferay-ui:error exception="<%=NoNoteException.class %>" message="no-note-selected" />
<liferay-ui:error exception="<%=InvalidApiKeyException.class %>" message="invalid-api-key-error" />

<c:if test="<%= themeDisplay.isSignedIn() && !SessionErrors.contains(renderRequest, InvalidApiKeyException.class) %>">
	<c:choose>
		<c:when test="${needAuthorize}">	
			<%@ include file="/jsp/include/authorization.jspf" %>
		</c:when>
		<c:otherwise>
			<%@ include file="/jsp/include/evernote.jspf" %> 
		</c:otherwise>
	</c:choose>
</c:if>
