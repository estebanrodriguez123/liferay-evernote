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

<liferay-ui:error exception="<%=EDAMUserException.class %>"><liferay-ui:message key="evernote-error" /></liferay-ui:error>
<liferay-ui:error exception="<%=EDAMSystemException.class %>"><liferay-ui:message key="evernote-error" /></liferay-ui:error>
<liferay-ui:error exception="<%=TException.class %>"><liferay-ui:message key="evernote-error" /></liferay-ui:error>
<liferay-ui:error exception="<%=EDAMNotFoundException.class %>"><liferay-ui:message key="evernote-error" /></liferay-ui:error>

<portlet:renderURL var="backURL">
	<portlet:param name="mvcPath" value="/jsp/view.jsp" />
</portlet:renderURL>

<portlet:resourceURL var="loadNotesURL">
	<portlet:param name="cmd" value="loadNotes"/>
</portlet:resourceURL>	

<a href="${backURL}"><button class="btn btn-primary" type="button"> &lt;&lt;&nbsp;<liferay-ui:message key="back"/></button></a>

<div id="${pns}create-note">
	<div class="row-fluid">
	    <div class="span6">
	        <div id="note-create" class="evernote">
	            <portlet:actionURL var="createNoteURL" name="createNote"/>
	            <h3><i class="icon-file"></i><liferay-ui:message key="create-note"/></h3>
	            <aui:form name="createNoteForm" action="${createNoteURL}" method="post"> 
	                <div class="notebook-selector-wrapper">
	                	<div class="notebook-selector"><aui:select name="newNoteNotebook"></aui:select></div>
	                	<div class="notebook-selector-loader"><img id="evernote-small-loader" src="${pageContext.request.contextPath}/images/ajax-loader.gif" /></div>
	                </div>
	                <div id="note-wrapper">
	                	<aui:input name="newNoteTitle" placeholder="title" label="" type="text"/>
	                	<aui:input name="newNoteContent" placeholder="note-content" label="" type="textarea" cssClass="evernote-textarea"/>
	                </div>
	                <button class="btn btn-primary" type="submit"><liferay-ui:message key="create"/></button>
	            </aui:form>
	        </div>
	    </div>
	
	    <div class="span6">
	        <div id="notebook-create" class="evernote">
	            <portlet:actionURL var="createNotebookURL" name="createNotebook"/>
	            <h3><i class="icon-book"></i><liferay-ui:message key="create-notebook"/></h3>
	            <aui:form name="createNotebookForm" action="${createNotebookURL}" method="post"> 
	                <aui:input type="text" name="newNotebookName" label="" placeholder="new-notebook-name"/>
	                <button class="btn btn-primary" type="submit"><liferay-ui:message key="create"/></button>
	            </aui:form>
	        </div>
	    </div>
	</div>
</div>
<script id="notebook-options" type="text/x-html-template">
	{{#each notes}}
		<option value="{{guid}}">{{name}}</option>
	{{/each}}
</script>

<aui:script use="aui-io-request,handlebars">
	A.io.request('${loadNotesURL}', {
		method: 'GET',
		data: {
		},
		dataType: 'json',
		on: {
			success: function(d) {
				updateNotebooksList({notes: d});
			}
		}
	});	

	/** render notebooks on select of Create Note */
	function updateNotebooksList(data) {
	    var evalData = eval(data.notes.details[1].response),
	    	container = A.one("#${pns}create-note"),
	    	source = A.one("#notebook-options").getHTML(),
	    	template = A.Handlebars.compile(source);
	    
	    var notes = [];	
	    for (var i = 0; i < evalData.length; ++i) {
	    	notes.push({name: evalData[i].name, guid: evalData[i].guid});
	    }

	    var html = template({notes: notes});
	   
	    container.one("#evernote-small-loader").remove();
	    container.one("#${pns}newNoteNotebook").get('childNodes').remove();
	    container.one("#${pns}newNoteNotebook").append(html);
	}
</aui:script>
