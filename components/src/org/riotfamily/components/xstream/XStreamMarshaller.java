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
package org.riotfamily.components.xstream;

import java.io.StringReader;
import java.io.StringWriter;

import org.riotfamily.components.model.Component;
import org.riotfamily.components.model.ComponentList;
import org.riotfamily.components.model.Content;
import org.riotfamily.components.model.ContentMap;
import org.riotfamily.components.model.ContentMapMarshaller;
import org.springframework.beans.factory.InitializingBean;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.DataHolder;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.mapper.Mapper;

public class XStreamMarshaller implements ContentMapMarshaller, 
		InitializingBean {

	private XStream xstream;
	
	private HierarchicalStreamDriver driver;
	
	public void setDriver(HierarchicalStreamDriver driver) {
		this.driver = driver;
	}
	
	public void afterPropertiesSet() throws Exception {
		if (driver == null) {
			driver = new DomDriver("UTF-8");
		}
		xstream = new XStream(driver);

		xstream.alias("component", Component.class);
		xstream.alias("component-list", ComponentList.class);
		xstream.alias("content-map", ContentMap.class);
		
		Mapper mapper = xstream.getMapper();
		
		xstream.registerConverter(new ActiveRecordConverter(mapper), 1);
		xstream.registerConverter(new ComponentListConverter(mapper), 1);
		xstream.registerConverter(new ComponentConverter(mapper), 2);
		xstream.registerConverter(new ContentMapConverter(mapper), 1);
	}
	
	private DataHolder createDataHolder(Content content) {
		DataHolder dataHolder = xstream.newDataHolder();
		dataHolder.put("content", content);
		return dataHolder;
	}
	
	public ContentMap unmarshal(Content owner, String xml) {
		owner.getReferences().clear();
		HierarchicalStreamReader reader = driver.createReader(new StringReader(xml));
		return (ContentMap) xstream.unmarshal(reader, null, createDataHolder(owner));	
	}
	
	public String marshal(ContentMap contentMap) {
		Content owner = contentMap.getOwner();
		owner.getReferences().clear();
		StringWriter sw = new StringWriter();
		HierarchicalStreamWriter writer = driver.createWriter(sw);
		xstream.marshal(contentMap, writer, createDataHolder(owner));
		return sw.toString();
	}
	
	public static void addReference(DataHolder dataHolder, Object ref) {
		Content content = (Content) dataHolder.get("content");
		content.getReferences().add(ref);
	}
}
