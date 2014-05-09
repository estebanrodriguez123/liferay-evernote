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

import static com.rivetlogic.evernote.util.EvernoteConstants.ACCESS_TOKEN;
import static com.rivetlogic.evernote.util.EvernoteConstants.EVERNOTE_SERVICE;
import static com.rivetlogic.evernote.util.EvernoteConstants.REQUEST_TOKEN;
import static com.rivetlogic.evernote.util.EvernoteConstants.REQUEST_TOKEN_SECRET;
import static com.rivetlogic.evernote.util.EvernoteConstants.INVALID_API_KEY_ERROR;
import static com.rivetlogic.evernote.util.EvernoteConstants.OAUTH_VERIFIER;
import static com.rivetlogic.evernote.util.EvernoteConstants.AUTHORIZATION_URL;

import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.servlet.http.HttpServletRequest;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.EvernoteApi;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import com.evernote.auth.EvernoteAuth;
import com.evernote.auth.EvernoteService;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.rivetlogic.evernote.exception.InvalidApiKeyException;
import com.rivetlogic.evernote.portlet.EvernoteKeys;

/**
 * @author dong-junkim
 *
 */
public class EvernoteUtil {
	private static Log LOG = LogFactoryUtil.getLog(EvernoteUtil.class);
	
	public static OAuthService getOAuthService(HttpServletRequest request, ThemeDisplay themeDisplay) 
		throws SystemException, InvalidApiKeyException {
		
		OAuthService service = null;
		String consumerKey = StringPool.BLANK; 
		String consumerSecret = StringPool.BLANK;

		// Set up the Scribe OAuthService.
	    String cbUrl = PortalUtil.getCurrentCompleteURL(request);
	        
	    Class<? extends EvernoteApi> providerClass = 
	    		(EVERNOTE_SERVICE == EvernoteService.PRODUCTION) ? 
	    				org.scribe.builder.api.EvernoteApi.class : 
	    				EvernoteApi.Sandbox.class;
		// Get consumer key and consumer secret
        
		try {
			consumerKey = EvernoteKeys.getConsumerKey(themeDisplay.getCompanyId());
			consumerSecret = EvernoteKeys.getConsumerSecret(themeDisplay.getCompanyId());
			
			service = new ServiceBuilder().provider(providerClass)
							.apiKey(consumerKey).apiSecret(consumerSecret).callback(cbUrl)
							.build();
	
		} catch (IllegalArgumentException e) {
			LOG.error(INVALID_API_KEY_ERROR);
			throw new InvalidApiKeyException(INVALID_API_KEY_ERROR);
		}

		return service;
	}
	
	public static void authenticateEvernote(RenderRequest renderRequest, PortletSession portletSession, ThemeDisplay themeDisplay) 
		throws SystemException {
		
		HttpServletRequest request = PortalUtil.getHttpServletRequest(renderRequest);
		String authorizationUrl = StringPool.BLANK;
		
		OAuthService service = getOAuthService(request, themeDisplay);

		if (PortalUtil.getOriginalServletRequest(request).getParameter(OAUTH_VERIFIER) == null) {
			// Send an OAuth message to the Provider asking for a new Request
			// Token because we don't have access to the current user's account.
			Token scribeRequestToken = service.getRequestToken();  
			 
			portletSession.setAttribute(REQUEST_TOKEN, scribeRequestToken.getToken());
			portletSession.setAttribute(REQUEST_TOKEN_SECRET, scribeRequestToken.getSecret());
			
			authorizationUrl = EVERNOTE_SERVICE.getAuthorizationUrl(scribeRequestToken.getToken());
			
		} else {
			// Send an OAuth message to the Provider asking to exchange the
			// existing Request Token for an Access Token
			Token scribeRequestToken = new Token(
					portletSession.getAttribute(REQUEST_TOKEN).toString(), 
					portletSession.getAttribute(REQUEST_TOKEN_SECRET).toString());
			
			Verifier scribeVerifier = new Verifier(
					PortalUtil.getOriginalServletRequest(request).getParameter(OAUTH_VERIFIER));
			
			Token scribeAccessToken = service.getAccessToken(scribeRequestToken, scribeVerifier);
			
			EvernoteAuth evernoteAuth = EvernoteAuth.parseOAuthResponse(
					EVERNOTE_SERVICE, scribeAccessToken.getRawResponse());
			
			portletSession.setAttribute(ACCESS_TOKEN, evernoteAuth.getToken());
		}                 
		
		renderRequest.setAttribute(AUTHORIZATION_URL, authorizationUrl);   
	}
}
