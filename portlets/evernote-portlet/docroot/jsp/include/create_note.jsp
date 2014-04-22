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

<a href="${backURL }"><button class="btn btn-primary" type="button"> &lt;&lt; Back</button></a>

<div class="row-fluid">
    <div class="span6">
        <div id="note-create" class="evernote">
            <portlet:actionURL var="createNoteURL" name="createNote"/>
            <h3><i class="icon-file"></i>Create Note</h3>
            <aui:form name="createNoteForm" action="${createNoteURL}" method="post"> 
                <div class="notebook-selector-wrapper">
                	<div class="notebook-selector"><aui:select name="newNoteNotebook"></aui:select></div>
                	<div class="notebook-selector-loader"><img id="evernote-small-loader" src="${pageContext.request.contextPath}/images/ajax-loader.gif" /></div>
                </div>
                <div id="note-wrapper">
                    <input type="text" name="${pns}newNoteTitle" placeholder="Title"/>
                    <textarea name="${pns}newNoteContent" class="evernote-textarea" placeholder="Note content"></textarea>
                </div>
                <button class="btn btn-primary" type="submit">Create</button>
            </aui:form>
        </div>
    </div>

    <div class="span6">
        <div id="notebook-create" class="evernote">
            <portlet:actionURL var="createNotebookURL" name="createNotebook"/>
            <h3><i class="icon-book"></i>Create Notebook</h3>
            <aui:form name="createNotebookForm" action="${createNotebookURL}" method="post"> 
                <aui:input type="text" name="newNotebookName" />
                <button class="btn btn-primary" type="submit">Create</button>
            </aui:form>
        </div>
    </div>
</div>

<script id="notebook-options" type="text/x-html-template">
	{{#each notes}}
		<option value="{{guid}}">{{name}}</option>
	{{/each}}
</script>

<script>
AUI().use('aui-io-request', 'handlebars', function(Y) {
	Y.io.request('${loadNotesURL}', {
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

	function updateNotebooksList(data) {
	    // render notebooks on select of Create Note
	    var evalData = eval(data.notes.details[1].response);
	    var notebooksCount = evalData.length;

	    var html = "";
	    for (var i=0;i<notebooksCount;i++) {
	    	html += "<option value='" + evalData[i].guid + "'>" + evalData[i].name + "</option>";
	    }
	    
	    Y.one("#evernote-small-loader").remove();
	    Y.one("#${pns}newNoteNotebook").get('childNodes').remove();
	    Y.one("#${pns}newNoteNotebook").append(html);
	}
}); 

</script>
