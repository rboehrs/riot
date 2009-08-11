/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.pages.riot.form;

import org.riotfamily.cachius.CacheService;
import org.riotfamily.components.editor.ContentFormController;
import org.riotfamily.components.model.ContentContainer;
import org.riotfamily.forms.controller.FormContextFactory;
import org.riotfamily.forms.factory.FormRepository;
import org.riotfamily.pages.model.PageProperties;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * The pages module replaces the ContentFormController provided by the
 * components module by this implementation. It checks if the  
 * {@link ContentContainer} being edited is a {@link PageProperties} instance
 * and wraps the form elements with {@link PagePropertyElement}s.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class PagePropertiesFormController extends ContentFormController {

	public PagePropertiesFormController(FormContextFactory formContextFactory,
			FormRepository formRepository,
			PlatformTransactionManager transactionManager,
			CacheService cacheService) {
		
		super(formContextFactory, formRepository, transactionManager, cacheService);
	}

	/*
	protected Form createForm(HttpServletRequest request) {
		ContentContainer container = getContainer(request);
		Content content = (Content) getFormBackingObject(request);
		if (container instanceof PageProperties) {
			if (container.getPreviewVersion().equals(content)) {
				return createPagePropertiesForm(request,
						(PageProperties) container);
			}
		}
		return super.createForm(request);
	}
	
	protected Form createPagePropertiesForm(HttpServletRequest request, 
			PageProperties props) {
		
		Page masterPage = props.getPage().getMasterPage();
		
		Form form = new Form(props);
		LocalizedEditorBinder binder = new LocalizedEditorBinder(
				new ContentEditorBinder());
		
		form.setEditorBinder(binder);
		
		String formId = getFormId(request);
		form.setId(formId);
		
		FormFactory factory = getFormRepository().getFormFactory(formId);
		
		for (ElementFactory ef : factory.getChildFactories()) {
			form.addElement(new PagePropertyElement(ef, binder, masterPage));
		}
		return form;
	}
	*/
}
