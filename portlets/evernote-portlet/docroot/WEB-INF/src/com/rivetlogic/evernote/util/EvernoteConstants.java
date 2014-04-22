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
	
	
	public static final String ACCESS_TOKEN = "accessToken";
	public static final String REQUEST_TOKEN = "requestToken";
	public static final String REQUEST_TOKEN_SECRET = "requestTokenSecret";
	public static final String NEED_AUTHORIZE = "needAuthorize";
	
	public static final String NOTES_LOADED = "notesLoaded";
	public static final String NOTES_LOADED_DEFAULT_VALUE = "3";

}
