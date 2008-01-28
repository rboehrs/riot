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
 * Portions created by the Initial Developer are Copyright (C) 2008
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages.riot.command;

import org.riotfamily.pages.dao.PageDao;
import org.riotfamily.pages.model.Page;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.CommandResult;
import org.riotfamily.riot.list.command.core.AbstractCommand;
import org.riotfamily.riot.list.command.result.ReloadResult;
import org.riotfamily.riot.list.command.result.ShowListResult;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class DiscardPageCommand extends AbstractCommand {

	public static final String ACTION_DELETE = "delete";
	
	public static final String ACTION_DISCARD = "discard";
	
	public static final String ACTION_UNPUBLISH = "unpublish";
	
	PageDao pageDao;
	
	
	public DiscardPageCommand(PageDao pageDao) {
		this.pageDao = pageDao;
	}

	protected String getAction(CommandContext context) {
		Page page = PageCommandUtils.getPage(context);
		if (!page.isPublished()) {
			return ACTION_DELETE;
		}
		if (page.isDirty()) {
			return ACTION_DISCARD;
		}
		return ACTION_UNPUBLISH;
	}
	
	protected boolean isEnabled(CommandContext context, String action) {
		if (ACTION_DISCARD.equals(action)) {
			return true;
		}
		return !PageCommandUtils.isSystemPage(context)
				&& PageCommandUtils.isTranslated(context);
	}
	
	public String getConfirmationMessage(CommandContext context) {
		Page page = (Page) context.getBean();
		if (!page.isPublished()) {
			return context.getMessageResolver().getMessage("confirm.delete",
					new Object[] {page.getTitle(true)},
					"Do you really want to delete '"
					 + page.getTitle(true) + "'?");
		}
		else if (page.isDirty()) {
			return context.getMessageResolver().getMessage("confirm.discard",
					new Object[] {page.getTitle(true)},
					"Do you really want to discard the changes made to '"
					 + page.getTitle(true) + "'?");
		}
		return null;
	}
	
	public CommandResult execute(CommandContext context) {
		Page page = (Page) context.getBean();
		if (!page.isPublished()) {
			pageDao.deletePage(page);
			return new ShowListResult(context);
		}
		if (page.isDirty()) {
			pageDao.discardPageProperties(page);
		}
		else {
			pageDao.unpublishPage(page);
		}
		return new ReloadResult();
	}
}