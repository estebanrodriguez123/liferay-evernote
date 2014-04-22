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

package com.rivetlogic.evernote.configuration;

import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import static com.rivetlogic.evernote.util.EvernoteConstants.*;

public class ConfigurationActionImpl extends DefaultConfigurationAction {
	
	@Override
	public String render(PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
        
		PortletPreferences portletPreferences = renderRequest.getPreferences();
		
		String notesLoaded = portletPreferences.getValue(NOTES_LOADED, NOTES_LOADED_DEFAULT_VALUE.toString());
		
		renderRequest.setAttribute(NOTES_LOADED, notesLoaded);
	        
	    PortletConfig selPortletConfig = getSelPortletConfig(renderRequest);
		String configTemplate = selPortletConfig.getInitParameter(
			"config-template");
		return configTemplate;
	}
	
	
    @Override
    public void processAction(PortletConfig portletConfig, ActionRequest actionRequest, 
    		ActionResponse actionResponse) throws Exception {
    	
    	PortletPreferences prefs = actionRequest.getPreferences();
    	prefs.setValue(NOTES_LOADED, ParamUtil.getString(actionRequest, NOTES_LOADED));
    	prefs.store();
        
        SessionMessages.add(actionRequest, "success");
    }
}
