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
package org.riotfamily.common.scheduling;

import org.hibernate.SessionFactory;
import org.riotfamily.common.hibernate.HibernateCallbackWithoutResult;
import org.riotfamily.common.hibernate.ThreadBoundHibernateTemplate;
import org.springframework.core.Ordered;

/**
 * Scheduled task that executes code within a Hibernate session.
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 8.0
 */
public abstract class HibernateTask extends HibernateCallbackWithoutResult 
		implements ScheduledTask, Ordered {

	private String[] triggerNames;
	
	private int order = Ordered.LOWEST_PRECEDENCE;
	
	private SessionFactory sessionFactory;
	
	private ThreadBoundHibernateTemplate hibernateTemplate;
	
	public HibernateTask(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		this.hibernateTemplate = new ThreadBoundHibernateTemplate(sessionFactory);
	}
	
	protected SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	public String[] getTriggerNames() {
		return triggerNames;
	}
	
	public void setTriggerNames(String[] triggerNames) {
		this.triggerNames = triggerNames;
	}
	
	public int getOrder() {
		return order;
	}
	
	public void setOrder(int order) {
		this.order = order;
	}

	public void execute() throws Exception {
		hibernateTemplate.execute(this);
	}
		
}
