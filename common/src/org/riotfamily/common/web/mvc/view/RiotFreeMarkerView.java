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
package org.riotfamily.common.web.mvc.view;

import java.io.IOException;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.collection.GuardedMap;
import org.riotfamily.common.util.Generics;
import org.riotfamily.common.web.support.RequestHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerView;

import freemarker.ext.servlet.FreemarkerServlet;
import freemarker.template.Template;

/**
 * Differences to Spring's FreeMarkerView:
 * <ul>
 * <li>Model attributes may override attributes from the request or the session</li>
 * <li>The plain HttpServletRequest object is exposed under the key "request"</li>
 * <li>The model is not exposed to the request, unless the 
 * {@link #setFreeMarkerServletMode(boolean) freeMarkeServletMode} is enabled</li>
 * <li>Provides the possibility to post process the model via the 
 * {@link ModelPostProcessor} interface</li>
 * </ul> 
 */
public class RiotFreeMarkerView extends FreeMarkerView {	
	
	public static final String REQUEST_KEY = "request";
	
	public static final String MODEL_ATTRIBUTE = 
			RiotFreeMarkerView.class.getName() + ".model";

	private Logger log = LoggerFactory.getLogger(RiotFreeMarkerView.class);
	
	private boolean allowModelOverride = true;
	
	private boolean freeMarkerServletMode = false;

	private Collection<ModelPostProcessor> modelPostProcessors;

	/**
	 * Sets whether the model may contain keys that are also present as request
	 * or session attributes. Otherwise an exception will be thrown when Spring
	 * tries to expose an attribute that conflicts with a key in the model. 	   
	 */
	public void setAllowModelOverride(boolean allowModelOverride) {
		this.allowModelOverride = allowModelOverride;
	}
	
	/**
	 * Sets whether the view should mimic the behavior of the 
	 * {@link FreemarkerServlet}, i.e. provide support JSP tag libraries, the
	 * "Request", "Session" and "Application" HashModels. By default this 
	 * mode is disabled, mainly because JSP support requires exposure of the
	 * model as separate request attributes, which can be quite confusing when
	 * working with nested views.
	 * 
	 * @param freeMarkerServletMode
	 */
	public void setFreeMarkerServletMode(boolean freeMarkerServletMode) {
		this.freeMarkerServletMode = freeMarkerServletMode;
	}
	
	public void setModelPostProcessors(Collection<ModelPostProcessor> modelPostProcessors) {
		this.modelPostProcessors = modelPostProcessors;
	}

	@Override
	public final void render(Map<String, ?> model, HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
	
		if (allowModelOverride) {
			Map<String, Object> emptyModel = Generics.newHashMap();
			emptyModel.put(MODEL_ATTRIBUTE, model);
			model = emptyModel;
		}
		super.render(model, request, wrapResponse(request, response));
	}

	/**
	 * Subclasses may override this method in order to wrap the 
	 * HttpServletResponse before it is passed on to the render method.
	 * The default implementation simply returns the given response. 
	 */
	protected HttpServletResponse wrapResponse(HttpServletRequest request, 
			HttpServletResponse response) {
		
		return response;
	}
	
	@SuppressWarnings("unchecked")
	private void unwrapModel(Map model) {
		Map originalModel = (Map) model.remove(MODEL_ATTRIBUTE);
		if (originalModel != null) {
			model.putAll(originalModel);
		}
	}
	
	@Override
	public Template getTemplate(Locale locale) throws IOException {
		return super.getTemplate(locale);
	}
	
	@Override
	protected void renderMergedTemplateModel(final Map<String, Object> model, 
			final HttpServletRequest request, 
			final HttpServletResponse response) 
			throws Exception {

		try {
			RequestHolder.set(request, response);
			unwrapModel(model);
			model.put(REQUEST_KEY, request);
			if (modelPostProcessors != null) {
				Map<String, Object> guardedModel = GuardedMap.guard(model);
				for (ModelPostProcessor processor : modelPostProcessors) {
					processor.postProcess(guardedModel, request, response);
				}
			}
			if (freeMarkerServletMode) {
				super.doRender(model, request, response);
			}
			else {
				if (log.isDebugEnabled()) {
					log.debug("Rendering FreeMarker template [" + getUrl() 
							+ "] in FreeMarkerView '" + getBeanName() + "'");
				}
				Locale locale = RequestContextUtils.getLocale(request);
				getTemplate(locale).process(model, response.getWriter());
			}
		}
		finally {
			RequestHolder.unset();
		}
	}

}
