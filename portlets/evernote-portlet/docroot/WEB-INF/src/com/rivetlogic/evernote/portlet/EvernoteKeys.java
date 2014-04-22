/**
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

package com.rivetlogic.evernote.portlet;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsUtil;

public class EvernoteKeys {
	private static final String EVERNOTE_CONSUMER_KEY="evernote.consumnerKey";
	private static final String EVERNOTE_CONSUMER_SECRET="evernote.consumerSecret";
	
	public static String getConsumerKey(long companyId) throws SystemException{
		return PrefsPropsUtil.getString(companyId, EVERNOTE_CONSUMER_KEY, 
				GetterUtil.getString(PropsUtil.get(EVERNOTE_CONSUMER_KEY)));
	}
	
	public static String getConsumerSecret(long companyId) throws SystemException{
		return PrefsPropsUtil.getString(companyId, EVERNOTE_CONSUMER_SECRET, 
				GetterUtil.getString(PropsUtil.get(EVERNOTE_CONSUMER_SECRET)));
	}
}
