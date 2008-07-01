/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Riot.
 *
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   flx
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.website.txt2img;

import java.io.File;
import java.io.FileWriter;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.io.IOUtils;
import org.riotfamily.common.util.SpringUtils;
import org.riotfamily.common.web.view.MacroHelperFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.util.WebUtils;

public class Txt2ImgMacroHelperFactory implements ServletContextAware,
		ApplicationContextAware, InitializingBean, MacroHelperFactory {

	private Map<String, ButtonStyle> buttons;
	
	private File buttonDir;
	
	public void setServletContext(ServletContext servletContext) {
		buttonDir = new File(WebUtils.getTempDir(servletContext), "txt2img");
	}

	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		buttons = SpringUtils.beansOfType(ctx, ButtonStyle.class);
	}
	
	public void afterPropertiesSet() throws Exception {
		IOUtils.clearDirectory(buttonDir);
		File styleSheet = new File(buttonDir, "buttons.css");
		FileWriter out = new FileWriter(styleSheet);
		for (ButtonStyle button : buttons.values()) {
			out.write(button.getRules());
		}
		out.close();
	}
	
	public File getButtonDir() {
		return buttonDir;
	}
	
	public Object createMacroHelper(HttpServletRequest request,
			HttpServletResponse response) {

		return new Txt2ImgMacroHelper(buttonDir, buttons, request);
	}

}