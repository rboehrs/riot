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
package org.riotfamily.forms;

import org.riotfamily.common.beans.property.ProtectedBeanWrapper;
import org.springframework.beans.BeanWrapper;
import org.springframework.util.Assert;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class BeanEditorBinder extends AbstractEditorBinder {

	private BeanWrapper beanWrapper;
	
	private boolean editingExistingBean;
	
	public BeanEditorBinder(Object backingObject) {
		Assert.notNull(backingObject);
		beanWrapper = new ProtectedBeanWrapper(backingObject);
		editingExistingBean = true;
	}
	
	public BeanEditorBinder(Class<?> beanClass) {
		beanWrapper = new ProtectedBeanWrapper(beanClass);
	}

	public Object getBackingObject() {
		return beanWrapper.getWrappedInstance();
	}

	public void setBackingObject(Object backingObject) {
		editingExistingBean = backingObject != null;
		if (backingObject != null) {
			beanWrapper = new ProtectedBeanWrapper(backingObject);
		}
		else {
			beanWrapper = new ProtectedBeanWrapper(getBeanClass());
		}
	}
	
	public boolean isEditingExistingBean() {
		return editingExistingBean;
	}
	
	public Class<?> getBeanClass() {
		return beanWrapper.getWrappedClass();
	}

	public Class<?> getPropertyType(String propertyName) {
		return beanWrapper.getPropertyType(propertyName);
	}
	
	public Object getPropertyValue(String propertyName) {
		return beanWrapper.getPropertyValue(propertyName);
	}

	public void setPropertyValue(String propertyName, Object value) {
		beanWrapper.setPropertyValue(propertyName, value);
	}

}
