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
package org.riotfamily.common.freemarker;

import java.util.Locale;
import java.util.Map;

import org.springframework.core.GenericCollectionTypeResolver;
import org.springframework.core.JdkVersion;
import org.springframework.core.Ordered;

import freemarker.core.Environment;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * ObjectWrapperPlugin that supports Maps containing {@link Locale} keys. When 
 * the map is to be wrapped, the entry for the current locale is returned 
 * instead of the map itself.
 *
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class LocalizedObjectWrapperPlugin implements ObjectWrapperPlugin, Ordered {

	private boolean exact = false;

	private Locale fallbackLocale = null;
	
	private int order = Ordered.LOWEST_PRECEDENCE;

	/**
	 * Sets whether the current locale must exactly match the key. If set to
	 * <code>false</code> (which is the default), a second lookup is performed
	 * with the language only in case no entry can be found.
	 */
	public void setExact(boolean exact) {
		this.exact = exact;
	}

	/**
	 * Sets a fallback locale that is used in case no entry is found for the
	 * current locale. Default is <code>null</code>.
	 */
	public void setFallbackLocale(Locale fallbackLocale) {
		this.fallbackLocale = fallbackLocale;
	}
	
	public int getOrder() {
		return order;
	}

	/**
	 * Sets the order. Default is {@link Ordered#LOWEST_PRECEDENCE}.
	 */
	public void setOrder(int order) {
		this.order = order;
	}

	public boolean supports(Object obj) {
		if (obj instanceof Map<?, ?>) {
			Map<?, ?> map = (Map<?,?>) obj;
			return hasLocaleKey(map);
		}
		return false;
	}
	
	public TemplateModel wrapSupportedObject(Object obj, 
			PluginObjectWrapper wrapper)
			throws TemplateModelException {
		
		Environment env = Environment.getCurrentEnvironment();
		Locale locale = env.getLocale();
		Object localizedEntry = getLocalizedEntry((Map<?,?>) obj, locale);
		return wrapper.wrap(localizedEntry);
	}

	private boolean hasLocaleKey(Map<?,?> map) {
		if (JdkVersion.isAtLeastJava15()) {
			Class<?> keyType = GenericCollectionTypeResolver.getMapKeyType(
					map.getClass());

			if (keyType != null) {
				return keyType.isAssignableFrom(Locale.class);
			}
		}
		if (!map.isEmpty()) {
			Object firstKey = map.keySet().iterator().next();
			return firstKey instanceof Locale;
		}
		return false;
	}

	private Object getLocalizedEntry(Map<?,?> map, Locale locale) {
		Object value = map.get(locale);
		if (value == null && !exact) {
			Locale lang = new Locale(locale.getLanguage());
			value = map.get(lang);
		}
		if (value == null && fallbackLocale != null
				&& !fallbackLocale.equals(locale)) {

			value = getLocalizedEntry(map, fallbackLocale);
		}
		return value;
	}

}
