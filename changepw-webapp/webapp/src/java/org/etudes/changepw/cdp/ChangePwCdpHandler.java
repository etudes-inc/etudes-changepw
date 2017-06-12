/**********************************************************************************
 * $URL: https://source.etudes.org/svn/apps/changepw/trunk/changepw-webapp/webapp/src/java/org/etudes/changepw/cdp/ChangePwCdpHandler.java $
 * $Id: ChangePwCdpHandler.java 5681 2013-08-21 18:40:41Z ggolden $
 ***********************************************************************************
 *
 * Copyright (c) 2013 Etudes, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/

package org.etudes.changepw.cdp;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.cdp.api.CdpHandler;
import org.etudes.cdp.api.CdpStatus;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.user.api.UserAlreadyDefinedException;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.user.api.UserEdit;
import org.sakaiproject.user.api.UserLockedException;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiproject.user.api.UserPermissionException;
import org.sakaiproject.util.StringUtil;

/**
 */
public class ChangePwCdpHandler implements CdpHandler
{
	/** Our log (commons). */
	private static Log M_log = LogFactory.getLog(ChangePwCdpHandler.class);

	public String getPrefix()
	{
		return "changepw";
	}

	public Map<String, Object> handle(HttpServletRequest req, HttpServletResponse res, Map<String, Object> parameters, String requestPath,
			String path, String authenticatedUserId) throws ServletException, IOException
	{
		if (requestPath.equals("setPassword"))
		{
			return dispatchSetPassword(req, res, parameters, path, authenticatedUserId);
		}

		return null;
	}

	protected Map<String, Object> dispatchSetPassword(HttpServletRequest req, HttpServletResponse res, Map<String, Object> parameters, String path,
			String userId) throws ServletException, IOException
	{
		Map<String, Object> rv = new HashMap<String, Object>();

		// get the pw parameter
		String pw = (String) parameters.get("pw");
		if (pw == null)
		{
			M_log.warn("dispatchSetPassword - no pw parameter");

			// add status parameter
			rv.put(CdpStatus.CDP_STATUS, CdpStatus.badRequest.getId());
			return rv;
		}

		// the email parameter
		String email = StringUtil.trimToNull((String) parameters.get("email"));

		try
		{
			UserEdit user = userDirectoryService().editUser(userId);
			user.setPassword(pw);
			user.setEmail(email);
			userDirectoryService().commitEdit(user);

			Session s = sessionManager().getCurrentSession();
			s.setAttribute("user.password.strength", Boolean.TRUE);
		}
		catch (UserNotDefinedException e)
		{
			M_log.warn("dispatchSetPassword - no user record: " + userId);

			// add status parameter
			rv.put(CdpStatus.CDP_STATUS, CdpStatus.badRequest.getId());
			return rv;
		}
		catch (UserPermissionException e)
		{
			M_log.warn("dispatchSetPassword - on user record: " + userId + " : " + e);

			// add status parameter
			rv.put(CdpStatus.CDP_STATUS, CdpStatus.badRequest.getId());
			return rv;
		}
		catch (UserLockedException e)
		{
			M_log.warn("dispatchSetPassword - on user record: " + userId + " : " + e);

			// add status parameter
			rv.put(CdpStatus.CDP_STATUS, CdpStatus.badRequest.getId());
			return rv;
		}
		catch (UserAlreadyDefinedException e)
		{
			M_log.warn("dispatchSetAccount - on user record: " + userId + " : " + e);

			// add status parameter
			rv.put(CdpStatus.CDP_STATUS, CdpStatus.badRequest.getId());
			return rv;
		}

		// add status parameter
		rv.put(CdpStatus.CDP_STATUS, CdpStatus.success.getId());

		return rv;
	}

	/**
	 * @return The SessionManager, via the component manager.
	 */
	protected SessionManager sessionManager()
	{
		return (SessionManager) ComponentManager.get(SessionManager.class);
	}

	/**
	 * @return The UserDirectoryService, via the component manager.
	 */
	protected UserDirectoryService userDirectoryService()
	{
		return (UserDirectoryService) ComponentManager.get(UserDirectoryService.class);
	}
}
