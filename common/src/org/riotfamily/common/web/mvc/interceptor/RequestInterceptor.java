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
package org.riotfamily.common.web.mvc.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interface for general servlet request interception. Use this interface if
 * you want to intercept every request, regardless of the HandlerMapping.
 * <p>
 * Use the {@link Intercept} annotation to specify which types of requests
 * should be intercepted.
 */
public interface RequestInterceptor {
	
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response) throws Exception;
	
	public void postHandle(HttpServletRequest request, HttpServletResponse response) throws Exception;
	
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Exception ex) throws Exception;

}
