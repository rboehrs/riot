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
package org.riotfamily.common.xml;

import org.springframework.beans.factory.xml.PluggableSchemaResolver;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.Resource;
import org.xml.sax.EntityResolver;

/**
 * Convinience class to read and validate XML files with a DTD.
 */
public class ValidatingDocumentReader extends DocumentReader {

	public ValidatingDocumentReader(Resource resource) {
		super(resource);
	}
	
	protected int getValidationMode() {
		return XmlBeanDefinitionReader.VALIDATION_XSD;
	}

	protected EntityResolver getEntityResolver() {
		return new PluggableSchemaResolver(getClass().getClassLoader());
	}
}
