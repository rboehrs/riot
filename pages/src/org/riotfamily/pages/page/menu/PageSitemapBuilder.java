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
package org.riotfamily.pages.page.menu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.web.util.PathCompleter;
import org.riotfamily.pages.component.preview.ViewModeResolver;
import org.riotfamily.pages.member.MemberBinder;
import org.riotfamily.pages.member.MemberBinderAware;
import org.riotfamily.pages.member.WebsiteMember;
import org.riotfamily.pages.member.support.NullMemberBinder;
import org.riotfamily.pages.menu.MenuItem;
import org.riotfamily.pages.menu.SitemapBuilder;
import org.riotfamily.pages.page.Page;
import org.riotfamily.pages.page.PageMap;

public class PageSitemapBuilder implements SitemapBuilder, MemberBinderAware {

	private ViewModeResolver viewModeResolver;
	
	private MemberBinder memberBinder = new NullMemberBinder();
	
	private PageMap pageMap;
	
	private PathCompleter pathCompleter;
	
	protected Collection getRootPages(HttpServletRequest request) {
		return pageMap.getRootPages();
	}
	
	protected List createItems(Collection pages, HttpServletRequest request) {
		ArrayList items = new ArrayList();
		Iterator it = pages.iterator();
		while (it.hasNext()) {
			Page page = (Page) it.next();
			if (includePage(page, request)) {
				MenuItem item = new MenuItem(page);
				item.setLabel(page.getTitle());
				item.setLink(completeLink(request, page.getPath()));
				items.add(item);
				Collection childPages = page.getChildPages();
				if (childPages != null && !childPages.isEmpty()) {
					item.setChildItems(
						createItems(page.getChildPages(), request));
				}
			}
		}
		return items;
	}
	
	protected String completeLink(HttpServletRequest request, String path) {
		StringBuffer link = new StringBuffer();
		link.append(request.getContextPath());
		link.append(pathCompleter.addServletMapping(path));
		return link.toString();
	}
	
	protected boolean includePage(Page page, HttpServletRequest request) {
		WebsiteMember member = memberBinder.getMember(request);
		
		return !page.isHidden() && page.isAccessible(request, member) &&
			(page.isPublished() || viewModeResolver.isPreviewMode(request));		
	}
	
	public void setMemberBinder(MemberBinder memberBinder) {
		this.memberBinder = memberBinder;
	}
	
	public final List buildSitemap(Page root, HttpServletRequest request) {
		if (root != null) {
			return createItems(root.getChildPages(), request);
		} else {
			return createItems(getRootPages(request), request);
		}
	}
		
	public long getLastModified(HttpServletRequest request) {
		return pageMap.getLastModified();
	}

	public void setPageMap(PageMap pageMap) {
		this.pageMap = pageMap;
	}

	public void setViewModeResolver(ViewModeResolver viewModeResolver) {
		this.viewModeResolver = viewModeResolver;
	}

	public void setPathCompleter(PathCompleter pathCompleter) {
		this.pathCompleter = pathCompleter;
	}
}
