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
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.forms.resource;

import java.io.PrintWriter;
import java.util.Collection;

import org.springframework.util.Assert;

/**
 *
 */
public class StylesheetResource implements FormResource {

	private String href;
	
	public StylesheetResource(String href) {
		Assert.notNull(href);
		this.href = href;
	}
	
	public void renderLoadingCode(PrintWriter writer, Collection loadedResources) {
		if (!loadedResources.contains(this)) {
			writer.print("Resources.loadStyleSheet('");
			writer.print(href);
			writer.print("');");
		}
	}
	
	public int hashCode() {
		return href.hashCode();
	}
	
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj instanceof StylesheetResource) {
			StylesheetResource other = (StylesheetResource) obj;
			return this.href.equals(other.href);
		}
		return false;
	}

}