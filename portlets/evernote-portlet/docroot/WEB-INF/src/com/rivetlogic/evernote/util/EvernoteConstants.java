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

package com.rivetlogic.evernote.util;

import com.evernote.auth.EvernoteService;

public final class EvernoteConstants {
	/*
	 * Replace this value with EvernoteService.PRODUCTION to switch from the Evernote
	 * sandbox server to the Evernote production server.
	 */
	public static final EvernoteService EVERNOTE_SERVICE = EvernoteService.PRODUCTION;
	public static final String EVERNOTE_SERVICE_NOTEBOOK_URL = "/shard/s1/view/notebook/";
	
	public static final String ACCESS_TOKEN = "accessToken";
	public static final String REQUEST_TOKEN = "requestToken";
	public static final String REQUEST_TOKEN_SECRET = "requestTokenSecret";
	public static final String NEED_AUTHORIZE = "needAuthorize";
	
	public static final String NOTES_LOADED = "notesLoaded";
	public static final String NOTES_LOADED_DEFAULT_VALUE = "3";
	
	public static final String GUID = "guid";
	public static final String NOTE_NAME = "name";
	public static final String NOTE_GUID = "noteGuid";
	public static final String NOTE_TITLE = "title";
	public static final String NOTE_LIST = "noteList";
	public static final String NOTE_CONTENT = "noteContent";
	public static final String NOTEBOOK_GUID = "notebookGuid";
	public static final String EDIT_NOTE_URL = "editNoteURL";
	public static final String AUTHORIZATION_URL = "authorizationUrl";
	public static final String SUCCESS_KEY = "success";
	
	public static final String NEW_NOTE_NOTEBOOK = "newNoteNotebook";
	public static final String NEW_NOTEBOOK_NAME = "newNotebookName";
	public static final String NEW_NOTE_TITLE = "newNoteTitle";
	public static final String NOTE_GUID_DELETE = "noteGuidDelete";
	
	public static final String LOAD_NOTES_ACTION = "loadNotes";
	public static final String LOAD_MORE_NOTES_ACTION = "loadMoreNotes";
	
	public static final String SELECT_NOTE_ACTION = "selectNote";
	public static final String LOAD_MORE = "loadMore";
	public static final String COUNT_LIST = "countLI";
	public static final String OAUTH_VERIFIER = "oauth_verifier";
	
	public static final String JSP_PAGE = "jspPage";
	public static final String GUID_SUCCESSFULL_CREATED_MESSAGE = "Successfully created a new notebook with GUID: ";
	public static final String GUID_SUCCESSFULL_DELETED_MESSAGE = "Successfully deleted note with the GUID: ";
	public static final String JSON_RETURNING_ERROR = "Error while returning json";
	public static final String EVERNOTE_AUTHENTICATION_ERROR = "Cannot Authenticate to Evernote";
	
	public static final String CREATE_NOTE_JSP = "create-note-jsp";
	
	
}
