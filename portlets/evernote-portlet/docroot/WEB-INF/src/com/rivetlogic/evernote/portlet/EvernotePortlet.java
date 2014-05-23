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
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

package com.rivetlogic.evernote.portlet;

import static com.rivetlogic.evernote.util.EvernoteConstants.ACCESS_TOKEN;
import static com.rivetlogic.evernote.util.EvernoteConstants.MVC_PATH;
import static com.rivetlogic.evernote.util.EvernoteConstants.CREATE_NOTE_JSP;
import static com.rivetlogic.evernote.util.EvernoteConstants.NEED_AUTHORIZE;
import static com.rivetlogic.evernote.util.EvernoteConstants.LOAD_NOTES_ACTION;
import static com.rivetlogic.evernote.util.EvernoteConstants.SELECT_NOTE_ACTION;
import static com.rivetlogic.evernote.util.EvernoteConstants.LOAD_MORE_NOTES_ACTION;
import static com.rivetlogic.evernote.util.EvernoteConstants.NOTES_LOADED;
import static com.rivetlogic.evernote.util.EvernoteConstants.NOTES_LOADED_DEFAULT_VALUE;
import static com.rivetlogic.evernote.util.EvernoteConstants.EVERNOTE_SERVICE;
import static com.rivetlogic.evernote.util.EvernoteConstants.NOTE_NAME;
import static com.rivetlogic.evernote.util.EvernoteConstants.GUID;
import static com.rivetlogic.evernote.util.EvernoteConstants.NOTE_TITLE;
import static com.rivetlogic.evernote.util.EvernoteConstants.NOTE_LIST;
import static com.rivetlogic.evernote.util.EvernoteConstants.LOAD_MORE;
import static com.rivetlogic.evernote.util.EvernoteConstants.NOTEBOOK_GUID;
import static com.rivetlogic.evernote.util.EvernoteConstants.COUNT_LIST;
import static com.rivetlogic.evernote.util.EvernoteConstants.NOTE_GUID;
import static com.rivetlogic.evernote.util.EvernoteConstants.EVERNOTE_SERVICE_NOTEBOOK_URL;
import static com.rivetlogic.evernote.util.EvernoteConstants.NOTE_CONTENT;
import static com.rivetlogic.evernote.util.EvernoteConstants.EDIT_NOTE_URL;
import static com.rivetlogic.evernote.util.EvernoteConstants.NEW_NOTE_TITLE;
import static com.rivetlogic.evernote.util.EvernoteConstants.UNTITLED_NOTE_KEY;
import static com.rivetlogic.evernote.util.EvernoteConstants.GUID_SUCCESSFULL_CREATED_MESSAGE;
import static com.rivetlogic.evernote.util.EvernoteConstants.NEW_NOTE_NOTEBOOK;
import static com.rivetlogic.evernote.util.EvernoteConstants.NEW_NOTEBOOK_NAME;
import static com.rivetlogic.evernote.util.EvernoteConstants.NOTE_GUID_DELETE;
import static com.rivetlogic.evernote.util.EvernoteConstants.GUID_SUCCESSFULL_DELETED_MESSAGE;
import static com.rivetlogic.evernote.util.EvernoteConstants.JSON_RETURNING_ERROR;
import static com.rivetlogic.evernote.util.EvernoteConstants.EVERNOTE_AUTHENTICATION_ERROR;
import static com.rivetlogic.evernote.util.EvernoteConstants.NOTEBOOK_EMPTY_ERROR;

import java.io.IOException;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletRequest;

import com.evernote.auth.EvernoteAuth;
import com.evernote.clients.ClientFactory;
import com.evernote.clients.NoteStoreClient;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.NoteSortOrder;
import com.evernote.edam.type.Notebook;
import com.evernote.thrift.TException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.oauth.OAuthException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.rivetlogic.evernote.exception.NoNoteException;
import com.rivetlogic.evernote.util.EvernoteUtil;

public class EvernotePortlet extends MVCPortlet {

	private static final Log LOG = LogFactoryUtil.getLog(EvernotePortlet.class);

	@Override
	public void doView(RenderRequest request,RenderResponse response) 
		throws PortletException, IOException {
		
		PortletSession portletSession = request.getPortletSession();

		ThemeDisplay themeDisplay = (ThemeDisplay) request
				.getAttribute(WebKeys.THEME_DISPLAY);
		
		if (themeDisplay.isSignedIn()) {
			if (((String) portletSession.getAttribute(ACCESS_TOKEN)) != null) {
				request.setAttribute(ACCESS_TOKEN,
						(String) portletSession.getAttribute(ACCESS_TOKEN));
				request.setAttribute(NEED_AUTHORIZE, Boolean.FALSE);
			} else {
				request.setAttribute(NEED_AUTHORIZE, Boolean.TRUE);
				try {
					EvernoteUtil.authenticateEvernote(request,
							portletSession, themeDisplay);
				} catch (OAuthException e) {
					
					if (LOG.isDebugEnabled()) {
						LOG.error(EVERNOTE_AUTHENTICATION_ERROR, e);
					} 
					
					if (SessionErrors.isEmpty(request)) {
						SessionMessages.add(request, 
							request.getAttribute(WebKeys.PORTLET_ID) + 
							SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_ERROR_MESSAGE);
					}
					
					SessionErrors.add(request, OAuthException.class);
				}
			}
		}
		
		super.doView(request, response);
	}

	@Override
	public void serveResource(ResourceRequest request, ResourceResponse response) {

		String cmd = ParamUtil.getString(request, Constants.CMD);
		
		try {
			if (LOAD_NOTES_ACTION.equals(cmd)) {
				JSONArray resultJsonArray = JSONFactoryUtil.createJSONArray();
				resultJsonArray = loadNotes(request);
				writeJSON(request, response, resultJsonArray);
			} else {
				
				JSONObject resultJsonObject = JSONFactoryUtil.createJSONObject();
				if (LOAD_MORE_NOTES_ACTION.equals(cmd)) {
					resultJsonObject = loadMoreNotes(request);
				} else if (SELECT_NOTE_ACTION.equals(cmd)) {
					resultJsonObject = selectNote(request);
				}
				writeJSON(request, response, resultJsonObject);
			}
				
		} catch (IOException e) {
			LOG.error(JSON_RETURNING_ERROR, e);
		}

	}

	public JSONArray loadNotes(ResourceRequest request) {

		PortletSession portletSession = request.getPortletSession();
		PortletPreferences prefs = request.getPreferences();
		String accessToken = (String) portletSession.getAttribute(ACCESS_TOKEN);
		int notesLoaded = GetterUtil.getInteger(prefs.getValue(NOTES_LOADED,
				NOTES_LOADED_DEFAULT_VALUE));

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		if (accessToken != null && !accessToken.isEmpty()) {
			EvernoteAuth evernoteAuth = new EvernoteAuth(EVERNOTE_SERVICE,
					accessToken);
			NoteStoreClient noteStoreClient;
			try {
				noteStoreClient = new ClientFactory(evernoteAuth)
						.createNoteStoreClient();
				for (Notebook notebook : noteStoreClient.listNotebooks()) {
					JSONObject notebooks = JSONFactoryUtil.createJSONObject();
					notebooks.put(NOTE_NAME, notebook.getName());
					notebooks.put(GUID, notebook.getGuid());

					NoteFilter filter = new NoteFilter();
					filter.setNotebookGuid(notebook.getGuid());
					filter.setOrder(NoteSortOrder.CREATED.getValue());

					List<Note> notes = noteStoreClient.findNotes(filter, 0,
							notesLoaded).getNotes();
					JSONArray noteList = JSONFactoryUtil.createJSONArray();
					for (Note note : notes) {
						JSONObject jsonNote = JSONFactoryUtil
								.createJSONObject();
						jsonNote.put(NOTE_TITLE, note.getTitle());
						jsonNote.put(GUID, note.getGuid());
						noteList.put(jsonNote);
					}
					notebooks.put(NOTE_LIST, noteList);
					notebooks
							.put(LOAD_MORE,
									noteStoreClient.findNotes(filter, 0,
											notesLoaded + 1).getNotesSize() > notesLoaded);

					jsonArray.put(notebooks);
				}
			} catch (EDAMUserException e) {
				LOG.error(e);
				SessionErrors.add(request, EDAMUserException.class);
			} catch (EDAMSystemException e) {
				LOG.error(e);
				SessionErrors.add(request, EDAMSystemException.class);
			} catch (TException e) {
				LOG.error(e);
				SessionErrors.add(request, TException.class);
			} catch (EDAMNotFoundException e) {
				LOG.error(e);
				SessionErrors.add(request, EDAMNotFoundException.class);
			}

		}

		return jsonArray;
	}

	public JSONObject loadMoreNotes(ResourceRequest request) {

		PortletSession portletSession = request.getPortletSession();
		PortletPreferences prefs = request.getPreferences();
		String accessToken = (String) portletSession.getAttribute(ACCESS_TOKEN);
		int notesLoaded = GetterUtil.getInteger(prefs.getValue(NOTES_LOADED,
				NOTES_LOADED_DEFAULT_VALUE));

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		String notebookGuid = ParamUtil.getString(request,
			NOTEBOOK_GUID);
		Integer currentNotes = ParamUtil.getInteger(request, COUNT_LIST) - 1;

		EvernoteAuth evernoteAuth = new EvernoteAuth(EVERNOTE_SERVICE,
				accessToken);
		NoteStoreClient noteStoreClient;
		try {
			noteStoreClient = new ClientFactory(evernoteAuth)
					.createNoteStoreClient();
			NoteFilter filter = new NoteFilter();
			filter.setNotebookGuid(notebookGuid);
			filter.setOrder(NoteSortOrder.CREATED.getValue());

			int notesToLoad = currentNotes + notesLoaded;
			List<Note> notes = noteStoreClient
					.findNotes(filter, 0, notesToLoad).getNotes();

			JSONArray noteList = JSONFactoryUtil.createJSONArray();
			while (notes.size() > currentNotes) {
				JSONObject jsonNote = JSONFactoryUtil.createJSONObject();

				Note note = notes.get(currentNotes);
				jsonNote.put(NOTE_TITLE, note.getTitle());
				jsonNote.put(GUID, note.getGuid());

				noteList.put(jsonNote);
				currentNotes++;
			}

			jsonObject.put(NOTE_LIST, noteList);
			jsonObject.put(LOAD_MORE,
					noteStoreClient.findNotes(filter, 0, notesToLoad + 1)
							.getNotesSize() > notesToLoad);
		} catch (EDAMUserException e) {
			LOG.error(e);
			SessionErrors.add(request, EDAMNotFoundException.class);
		} catch (EDAMSystemException e) {
			LOG.error(e);
			SessionErrors.add(request, EDAMNotFoundException.class);
		} catch (TException e) {
			LOG.error(e);
			SessionErrors.add(request, EDAMNotFoundException.class);
		} catch (EDAMNotFoundException e) {
			LOG.error(e);
			SessionErrors.add(request, EDAMNotFoundException.class);
		}

		return jsonObject;
	}

	public JSONObject selectNote(ResourceRequest resourceRequest) {
		HttpServletRequest request = PortalUtil
				.getHttpServletRequest(resourceRequest);

		PortletSession portletSession = resourceRequest.getPortletSession();
		String accessToken = (String) portletSession.getAttribute(ACCESS_TOKEN);

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		String noteGuid = ParamUtil.getString(resourceRequest, NOTE_GUID);

		EvernoteAuth evernoteAuth = new EvernoteAuth(EVERNOTE_SERVICE,
				accessToken);
		NoteStoreClient noteStoreClient;
		
		try {
			noteStoreClient = new ClientFactory(evernoteAuth)
					.createNoteStoreClient();
			Note note = noteStoreClient.getNote(noteGuid, true, true, false,
					false);

			String editNoteURL = EVERNOTE_SERVICE.getHost()
					+ EVERNOTE_SERVICE_NOTEBOOK_URL + noteGuid;

			jsonObject.put(NOTE_CONTENT, note.getContent());
			jsonObject.put(GUID, noteGuid);
			jsonObject.put(EDIT_NOTE_URL, editNoteURL);
			
		} catch (EDAMUserException e) {
			LOG.error(e);
			SessionErrors.add(request, EDAMUserException.class);
		} catch (EDAMSystemException e) {
			LOG.error(e);
			SessionErrors.add(request, EDAMSystemException.class);
		} catch (TException e) {
			LOG.error(e);
			SessionErrors.add(request, TException.class);
		} catch (EDAMNotFoundException e) {
			LOG.error(e);
			SessionErrors.add(request, EDAMNotFoundException.class);
		}

		return jsonObject;
	}

	public void createNote(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException, PortletException {

		PortletSession portletSession = actionRequest.getPortletSession();
		String accessToken = (String) portletSession.getAttribute(ACCESS_TOKEN);
		ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

		EvernoteAuth evernoteAuth = new EvernoteAuth(EVERNOTE_SERVICE,
				accessToken);
		NoteStoreClient noteStoreClient;
		try {
			noteStoreClient = new ClientFactory(evernoteAuth)
					.createNoteStoreClient();

			// To create a new note, simply create a new Note object and fill in
			// attributes such as the note's title.
			Note note = new Note();

			String title = ParamUtil.getString(actionRequest, NEW_NOTE_TITLE);
			
			if (Validator.isNull(title)) {
				title = LanguageUtil.get(themeDisplay.getLocale(), UNTITLED_NOTE_KEY);
			}

			// The content of an Evernote note is represented using Evernote
			// Markup Language
			// (ENML). The full ENML specification can be found in the Evernote
			// API
			// Overview at
			// http://dev.evernote.com/documentation/cloud/chapters/ENML.php
			StringBundler sb = new StringBundler();
			sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
			  .append("<!DOCTYPE en-note SYSTEM \"http://xml.evernote.com/pub/enml2.dtd\">")
			  .append("<en-note>")
			  .append(ParamUtil.getString(actionRequest, "newNoteContent"))
			  .append("</en-note>");

			String notebookGuid = ParamUtil.getString(actionRequest,
				NEW_NOTE_NOTEBOOK);

			note.setTitle(title);
			note.setContent(sb.toString());
			if (!notebookGuid.isEmpty())
				note.setNotebookGuid(notebookGuid);

			// Finally, send the new note to Evernote using the createNote
			// method
			// The new Note object that is returned will contain
			// server-generated
			// attributes such as the new note's unique GUID.
			Note createdNote = noteStoreClient.createNote(note);
			String newNoteGuid = createdNote.getGuid();
			
			if (LOG.isDebugEnabled()) {
				LOG.debug(GUID_SUCCESSFULL_CREATED_MESSAGE + newNoteGuid);
			}

		} catch (EDAMUserException e) {
			LOG.error(e);
			SessionErrors.add(actionRequest, EDAMUserException.class);
		} catch (EDAMSystemException e) {
			LOG.error(e);
			SessionErrors.add(actionRequest, EDAMSystemException.class);
		} catch (TException e) {
			LOG.error(e);
			SessionErrors.add(actionRequest, TException.class);
		} catch (EDAMNotFoundException e) {
			LOG.error(e);
			SessionErrors.add(actionRequest, EDAMNotFoundException.class);
		}
		if (!SessionErrors.isEmpty(actionRequest)) {
			actionResponse.setRenderParameter(MVC_PATH, getInitParameter(CREATE_NOTE_JSP));
		}

	}

	public void createNotebook(ActionRequest request,
			ActionResponse response) throws IOException, PortletException {

		PortletSession portletSession = request.getPortletSession();
		String accessToken = (String) portletSession.getAttribute(ACCESS_TOKEN);

		EvernoteAuth evernoteAuth = new EvernoteAuth(EVERNOTE_SERVICE,
				accessToken);
		NoteStoreClient noteStoreClient;
		String name = ParamUtil.getString(request, NEW_NOTEBOOK_NAME);
		
		if (Validator.isNotNull(name)) {
			try {
				noteStoreClient = new ClientFactory(evernoteAuth)
				.createNoteStoreClient();
				
				// To create a new note, simply create a new Note object and fill in
				// attributes such as the note's title.
				Notebook notebook = new Notebook();
				notebook.setName(name);
				
				Notebook createdNotebook = noteStoreClient.createNotebook(notebook);
				String newNotebookGuid = createdNotebook.getGuid();
				
				if (LOG.isDebugEnabled()) {
					LOG.debug(GUID_SUCCESSFULL_CREATED_MESSAGE
						+ newNotebookGuid);
				}
				
			} catch (EDAMUserException e) {
				LOG.error(e);
				SessionErrors.add(request, EDAMUserException.class);
			} catch (EDAMSystemException e) {
				LOG.error(e);
				SessionErrors.add(request, EDAMSystemException.class);
			} catch (TException e) {
				LOG.error(e);
				SessionErrors.add(request, TException.class);
			}
			
		} else {
			SessionErrors.add(request, NOTEBOOK_EMPTY_ERROR);
		}
		
		if (!SessionErrors.isEmpty(request)) {
			response.setRenderParameter(MVC_PATH, getInitParameter(CREATE_NOTE_JSP));
		}
		
	}

	public void deleteNote(ActionRequest request,
			ActionResponse response) {

		PortletSession portletSession = request.getPortletSession();
		String accessToken = (String) portletSession.getAttribute(ACCESS_TOKEN);

		EvernoteAuth evernoteAuth = new EvernoteAuth(EVERNOTE_SERVICE,
				accessToken);
		NoteStoreClient noteStoreClient;
		try {
			noteStoreClient = new ClientFactory(evernoteAuth)
					.createNoteStoreClient();
			String noteGuid = ParamUtil.getString(request,
				NOTE_GUID_DELETE);
			if (!Validator.isNull(noteGuid)) {
				noteStoreClient.deleteNote(noteGuid);
				if (LOG.isDebugEnabled()) {
					LOG.debug(GUID_SUCCESSFULL_DELETED_MESSAGE + noteGuid);
				}
			} else {
				throw new NoNoteException();
			}
			
		} catch (EDAMUserException e) {
			LOG.error(e);
			SessionErrors.add(request, EDAMUserException.class);
		} catch (EDAMSystemException e) {
			LOG.error(e);
			SessionErrors.add(request, EDAMSystemException.class);
		} catch (TException e) {
			LOG.error(e);
			SessionErrors.add(request, TException.class);
		} catch (EDAMNotFoundException e) {
			LOG.error(e);
			SessionErrors.add(request, EDAMNotFoundException.class);
		} catch (NoNoteException e) {
			LOG.error(e);
			SessionErrors.add(request, NoNoteException.class);
		}
		
		if (!SessionErrors.isEmpty(request)) {
			response.setRenderParameter(MVC_PATH, viewTemplate);
		}
	}

}