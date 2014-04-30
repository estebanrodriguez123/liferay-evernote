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
import static com.rivetlogic.evernote.util.EvernoteConstants.JSP_PAGE;
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
import static com.rivetlogic.evernote.util.EvernoteConstants.GUID_SUCCESSFULL_CREATED_MESSAGE;
import static com.rivetlogic.evernote.util.EvernoteConstants.NEW_NOTE_NOTEBOOK;
import static com.rivetlogic.evernote.util.EvernoteConstants.NEW_NOTEBOOK_NAME;
import static com.rivetlogic.evernote.util.EvernoteConstants.NOTE_GUID_DELETE;
import static com.rivetlogic.evernote.util.EvernoteConstants.GUID_SUCCESSFULL_DELETED_MESSAGE;
import static com.rivetlogic.evernote.util.EvernoteConstants.JSON_RETURNING_ERROR;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.rivetlogic.evernote.exception.InvalidApiKeyException;
import com.rivetlogic.evernote.exception.NoNoteException;
import com.rivetlogic.evernote.util.EvernoteUtil;

public class EvernotePortlet extends MVCPortlet {

	private static final Log LOG = LogFactoryUtil.getLog(EvernotePortlet.class);

	@Override
	public void doView(RenderRequest renderRequest,
			RenderResponse renderResponse) throws PortletException, IOException {
		PortletSession portletSession = renderRequest.getPortletSession();

		HttpServletRequest request = PortalUtil
				.getHttpServletRequest(renderRequest);
		ThemeDisplay themeDisplay = (ThemeDisplay) request
				.getAttribute(WebKeys.THEME_DISPLAY);
		if (themeDisplay.isSignedIn()) {
			if (((String) portletSession.getAttribute(ACCESS_TOKEN)) != null) {
				renderRequest.setAttribute(ACCESS_TOKEN,
						(String) portletSession.getAttribute(ACCESS_TOKEN));
				renderRequest.setAttribute(NEED_AUTHORIZE, Boolean.FALSE);
			} else {
				renderRequest.setAttribute(NEED_AUTHORIZE, Boolean.TRUE);
				try {
					EvernoteUtil.authenticateEvernote(renderRequest,
							portletSession, themeDisplay);
				} catch (SystemException e) {
					if (e instanceof InvalidApiKeyException) {
						if (SessionErrors.isEmpty(renderRequest)) {
							SessionMessages.add(renderRequest, 
								renderRequest.getAttribute(WebKeys.PORTLET_ID) + 
								SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_ERROR_MESSAGE);
						}
						SessionErrors.add(renderRequest, InvalidApiKeyException.class);
						
					} else {
						LOG.error(e);
						SessionErrors.add(renderRequest, SystemException.class);
					}
				}
			}
		}
		
		super.doView(renderRequest, renderResponse);
	}

	@Override
	public void serveResource(ResourceRequest request, ResourceResponse response) {

		String cmd = ParamUtil.getString(request, Constants.CMD);

		if (LOAD_NOTES_ACTION.equals(cmd)) {
			JSONArray resultJsonArray = JSONFactoryUtil.createJSONArray();
			resultJsonArray = loadNotes(request);
			returnJSON(response, resultJsonArray);
		}

		else {
			JSONObject resultJsonObject = JSONFactoryUtil.createJSONObject();
			if (LOAD_MORE_NOTES_ACTION.equals(cmd)) {
				resultJsonObject = loadMoreNotes(request);
			} else if (SELECT_NOTE_ACTION.equals(cmd)) {
				resultJsonObject = selectNote(request);
			}
			returnJSON(response, resultJsonObject);
		}

	}

	public JSONArray loadNotes(ResourceRequest resourceRequest) {
		HttpServletRequest request = PortalUtil
				.getHttpServletRequest(resourceRequest);

		PortletSession portletSession = resourceRequest.getPortletSession();
		PortletPreferences prefs = resourceRequest.getPreferences();
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

	public JSONObject loadMoreNotes(ResourceRequest resourceRequest) {
		HttpServletRequest request = PortalUtil
				.getHttpServletRequest(resourceRequest);

		PortletSession portletSession = resourceRequest.getPortletSession();
		PortletPreferences prefs = resourceRequest.getPreferences();
		String accessToken = (String) portletSession.getAttribute(ACCESS_TOKEN);
		int notesLoaded = GetterUtil.getInteger(prefs.getValue(NOTES_LOADED,
				NOTES_LOADED_DEFAULT_VALUE));

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		String notebookGuid = ParamUtil.getString(resourceRequest,
			NOTEBOOK_GUID);
		Integer currentNotes = ParamUtil.getInteger(resourceRequest, COUNT_LIST) - 1;

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
		HttpServletRequest request = PortalUtil
				.getHttpServletRequest(actionRequest);

		PortletSession portletSession = actionRequest.getPortletSession();
		String accessToken = (String) portletSession.getAttribute(ACCESS_TOKEN);

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

			// The content of an Evernote note is represented using Evernote
			// Markup Language
			// (ENML). The full ENML specification can be found in the Evernote
			// API
			// Overview at
			// http://dev.evernote.com/documentation/cloud/chapters/ENML.php
			String content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<!DOCTYPE en-note SYSTEM \"http://xml.evernote.com/pub/enml2.dtd\">"
					+ "<en-note>"
					+ ParamUtil.getString(actionRequest, "newNoteContent")
					+ "</en-note>";

			String notebookGuid = ParamUtil.getString(actionRequest,
				NEW_NOTE_NOTEBOOK);

			note.setTitle(title);
			note.setContent(content);
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

			actionResponse.setRenderParameter(JSP_PAGE, viewTemplate);
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
		if (!SessionErrors.isEmpty(request)) {
			actionResponse.setRenderParameter(JSP_PAGE, getInitParameter(CREATE_NOTE_JSP));
		}

	}

	public void createNotebook(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException, PortletException {
		HttpServletRequest request = PortalUtil
				.getHttpServletRequest(actionRequest);

		PortletSession portletSession = actionRequest.getPortletSession();
		String accessToken = (String) portletSession.getAttribute(ACCESS_TOKEN);

		EvernoteAuth evernoteAuth = new EvernoteAuth(EVERNOTE_SERVICE,
				accessToken);
		NoteStoreClient noteStoreClient;
		try {
			noteStoreClient = new ClientFactory(evernoteAuth)
					.createNoteStoreClient();
			String name = ParamUtil.getString(actionRequest, NEW_NOTEBOOK_NAME);

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
		// no matter what happens we are going back to create note page
		actionResponse.setRenderParameter(JSP_PAGE, getInitParameter(CREATE_NOTE_JSP));
	}

	public void deleteNote(ActionRequest actionRequest,
			ActionResponse actionResponse) {
		HttpServletRequest request = PortalUtil
				.getHttpServletRequest(actionRequest);

		PortletSession portletSession = actionRequest.getPortletSession();
		String accessToken = (String) portletSession.getAttribute(ACCESS_TOKEN);

		EvernoteAuth evernoteAuth = new EvernoteAuth(EVERNOTE_SERVICE,
				accessToken);
		NoteStoreClient noteStoreClient;
		try {
			noteStoreClient = new ClientFactory(evernoteAuth)
					.createNoteStoreClient();
			String noteGuid = ParamUtil.getString(actionRequest,
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
		
		actionResponse.setRenderParameter(JSP_PAGE, viewTemplate);
	}

	public static void returnJSON(PortletResponse response, Object jsonObj) {
		HttpServletResponse servletResponse = PortalUtil
				.getHttpServletResponse(response);
		PrintWriter pw;
		try {
			pw = servletResponse.getWriter();
			pw.write(jsonObj.toString());
			pw.close();
		} catch (IOException e) {
			LOG.error(JSON_RETURNING_ERROR, e);
		}
	}

}