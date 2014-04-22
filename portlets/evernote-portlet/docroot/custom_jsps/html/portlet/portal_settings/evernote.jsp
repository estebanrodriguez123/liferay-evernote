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

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ page import="com.liferay.portal.kernel.util.PrefsPropsUtil" %>
<%@ page import="com.liferay.portal.kernel.util.PropsKeys" %>
<%@ page import="com.liferay.portal.kernel.util.PropsUtil" %>
<%@ page import="com.liferay.portal.kernel.util.GetterUtil" %>
<%@ page import="com.liferay.portal.util.PortletKeys" %>

<liferay-theme:defineObjects />

<% 
final String EVERNOTE_CONSUMER_KEY="evernote.consumnerKey";
final String EVERNOTE_CONSUMER_SECRET="evernote.consumerSecret";

String evernoteConsumerKey = PrefsPropsUtil.getString(company.getCompanyId(), EVERNOTE_CONSUMER_KEY, GetterUtil.getString(PropsUtil.get(EVERNOTE_CONSUMER_KEY)));
String evernoteConsumerSecret = PrefsPropsUtil.getString(company.getCompanyId(), EVERNOTE_CONSUMER_SECRET, GetterUtil.getString(PropsUtil.get(EVERNOTE_CONSUMER_SECRET)));
%>

<h3><liferay-ui:message key="miscellaneous-title-evernote" /></h3>

<aui:fieldset>
	<aui:input autocomplete="off" cssClass="lfr-input-text-container" 
		label="consumer-key" name='<%="settings--"+EVERNOTE_CONSUMER_KEY+"--"%>' 
		type="text" value="<%= evernoteConsumerKey %>" />

	<aui:input autocomplete="off" cssClass="lfr-input-text-container" 
		label="consumer-secret" name='<%="settings--"+EVERNOTE_CONSUMER_SECRET+"--"%>' 
		type="text" value="<%= evernoteConsumerSecret %>" />
</aui:fieldset>