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

import java.beans.PropertyEditor;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.riotfamily.forms.Editor;
import org.riotfamily.forms.EditorBinder;
import org.riotfamily.forms.EditorBinding;
import org.springframework.beans.PropertyEditorRegistrar;

/**
 * EditorBinder for {@link AbstractLocalizedElement localized elements}
 * that returns null, if the value is not 
 * {@link AbstractLocalizedElement#isOverwrite() overwritten}.
 */
public class LocalizedEditorBinder implements EditorBinder {

	private EditorBinder delegate;
	
	private Map<EditorBinding, AbstractLocalizedElement> elements = 
			new HashMap<EditorBinding, AbstractLocalizedElement>();
	
	public LocalizedEditorBinder(EditorBinder delegate) {
		this.delegate = delegate;
	}

	public void registerElement(EditorBinding binding, 
			AbstractLocalizedElement editor) {
		
		elements.put(binding, editor);
	}
	
	private boolean isOverwrite(EditorBinding binding) {
		AbstractLocalizedElement ele = elements.get(binding);
		return ele.isOverwrite();
	}
	
	private Object getValue(EditorBinding binding) {
		return isOverwrite(binding) ? binding.getEditor().getValue() : null;
	}
	
	public Object populateBackingObject() {
		for (EditorBinding binding : getBindings().values()) {
			setPropertyValue(binding.getProperty(), getValue(binding));
		}
		return getBackingObject();
	}

	public void bind(Editor editor, String property) {
		delegate.bind(editor, property);
	}

	@SuppressWarnings("unchecked")
	public PropertyEditor findCustomEditor(Class requiredType, String propertyPath) {
		return delegate.findCustomEditor(requiredType, propertyPath);
	}

	public Object getBackingObject() {
		return delegate.getBackingObject();
	}

	public Class<?> getBeanClass() {
		return delegate.getBeanClass();
	}

	public Map<String, EditorBinding> getBindings() {
		return delegate.getBindings();
	}

	public String[] getBoundProperties() {
		return delegate.getBoundProperties();
	}

	public Editor getEditor(String property) {
		return delegate.getEditor(property);
	}

	public Class<?> getPropertyType(String property) {
		return delegate.getPropertyType(property);
	}

	public Object getPropertyValue(String property) {
		return delegate.getPropertyValue(property);
	}
	
	public PropertyEditor getPropertyEditor(Class<?> type, String propertyPath) {
		return delegate.getPropertyEditor(type, propertyPath);
	}

	public void initEditors() {
		delegate.initEditors();
	}

	public boolean isEditingExistingBean() {
		return delegate.isEditingExistingBean();
	}

	@SuppressWarnings("unchecked")
	public void registerCustomEditor(Class requiredType,
			PropertyEditor propertyEditor) {
		delegate.registerCustomEditor(requiredType, propertyEditor);
	}

	@SuppressWarnings("unchecked")
	public void registerCustomEditor(Class requiredType, String propertyPath,
			PropertyEditor propertyEditor) {
		delegate.registerCustomEditor(requiredType, propertyPath,
				propertyEditor);
	}

	public void registerPropertyEditors(
			Collection<PropertyEditorRegistrar> registrars) {
		
		delegate.registerPropertyEditors(registrars);
	}

	public EditorBinder replace(EditorBinder previousBinder) {
		delegate.replace(previousBinder);
		return this;
	}

	public void setBackingObject(Object backingObject) {
		delegate.setBackingObject(backingObject);
	}

	public void setPropertyValue(String property, Object value) {
		delegate.setPropertyValue(property, value);
	}
	
}
