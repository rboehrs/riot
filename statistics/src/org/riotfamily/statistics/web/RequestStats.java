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
package org.riotfamily.statistics.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.util.Generics;
import org.riotfamily.statistics.domain.FaultyRepsonseStatsItem;
import org.riotfamily.statistics.domain.RequestStatsItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestStats {

	private static Logger log = LoggerFactory.getLogger(RequestStats.class);

	private long warnThreshold;
	
	private long maxRequests;
	
	private int maxListSize = 45;
	
	private long parallelRequestsHWM;
	
	private int totalRequestCount = 0;

	private int faultyResonseCount = 0;

	private long totalResponseTime = 0;
	
	private String monitoredUrl;
	
	private boolean enabled = false;
	
	private boolean ignoreUploads = false;
	
	private LinkedList<RequestStatsItem> currentRequests = Generics.newLinkedList();

	private LinkedList<RequestStatsItem> criticalRequests = Generics.newLinkedList();

	private LinkedList<FaultyRepsonseStatsItem> faultyResponses = Generics.newLinkedList();

	private List<Integer> faultStatusCodes = Collections.singletonList(
			HttpServletResponse.SC_NOT_FOUND);
	
	
	boolean signalFailure(String url) {
		return currentRequests.size() > maxRequests && monitoredUrl.equalsIgnoreCase(url); 
	}
	
	synchronized void updateStatsBefore(RequestStatsItem reqStats) {
		totalRequestCount++;
		currentRequests.add(reqStats);
		if (parallelRequestsHWM < currentRequests.size()) {
			parallelRequestsHWM = currentRequests.size();
		}
	}

	void updateStatsAfter(RequestStatsItem reqStats) {
		reqStats.responseDone();
		synchronized (this) {
			totalResponseTime += reqStats.getResponseTime();
			currentRequests.remove(reqStats);
			if (!ignoreUploads || !reqStats.isUpload()) {
				checkCriticalCandidate(reqStats);
			}
		}
	}
	
	private void checkCriticalCandidate(RequestStatsItem reqStats) {
		if (reqStats.getResponseTime() > warnThreshold) {
			if (criticalRequests.size() < maxListSize) {
				addCriticalRequest(reqStats);
			} 
			else {
				RequestStatsItem fastestReq = findFastest(criticalRequests);
				if (reqStats.getResponseTime() > fastestReq.getResponseTime()) {
					criticalRequests.remove(fastestReq);
					addCriticalRequest(reqStats);
				}
			}
			log.warn("Response time slow for URL {} ({} s)", 
					reqStats.getName(), reqStats.getResponseTime() / 1000);
		}
	}

	private void addCriticalRequest(RequestStatsItem reqStats) {
		int i = 0;
		for (RequestStatsItem item : criticalRequests) {
			if (item.getResponseTime() < reqStats.getResponseTime()) {
				criticalRequests.add(i, reqStats);
				return;
			}
		}
		criticalRequests.add(reqStats);
	}

	private RequestStatsItem findFastest(LinkedList<RequestStatsItem> list) {
		if (!list.isEmpty()) {
			return list.getFirst();
		}
		return null;
	}

	void checkFaultyResponse(HttpServletRequest request, Integer status) {
		if (faultStatusCodes.contains(status)) {
			synchronized (this) {
				faultyResonseCount++;
				addFaultyResponse(
						new FaultyRepsonseStatsItem(request, status));
			}
		}
	}

	private void addFaultyResponse(FaultyRepsonseStatsItem reqStats) {
		for (FaultyRepsonseStatsItem item : faultyResponses) {
			if (item.getName().equals(reqStats.getName())
					&& item.getStatus() == reqStats.getStatus()) {
				
				item.count();
				faultyResponses.remove(item);
				faultyResponses.add(item);
				return;
			}
		}
		faultyResponses.add(reqStats);
		if (faultyResponses.size() > maxListSize) {
			faultyResponses.removeFirst();
		}
	}
	
	public synchronized void reset() {
		criticalRequests.clear();
		faultyResponses.clear();
		totalRequestCount = 0;
		faultyResonseCount = 0;
		totalResponseTime = 0;
		parallelRequestsHWM = 0;
	}

	public long getAvgResponseTime() {
		if (totalRequestCount > 0) {
			return totalResponseTime / (totalRequestCount);
		}
		return -1;
	}

	public long getWarnThreshold() {
		return warnThreshold;
	}

	public void setWarnThreshold(long warnThreshold) {
		this.warnThreshold = warnThreshold;
	}

	public long getMaxRequests() {
		return maxRequests;
	}

	public void setMaxRequests(long maxRequests) {
		this.maxRequests = maxRequests;
	}

	public String getMonitoredUrl() {
		return monitoredUrl;
	}

	public void setMonitoredUrl(String monitoredUrl) {
		this.monitoredUrl = monitoredUrl;
	}

	public synchronized int getCurrentRequestCount() {
		return currentRequests.size();
	}

	public int getTotalRequestCount() {
		return totalRequestCount;
	}

	public int getFaultyResponseCount() {
		return faultyResonseCount;
	}
	
	public long getTotalResponseTime() {
		return totalResponseTime;
	}

	public List<RequestStatsItem> getCurrentRequests() {
		return currentRequests;
	}

	public List<RequestStatsItem> getCriticalRequests() {
		return criticalRequests;
	}

	public LinkedList<FaultyRepsonseStatsItem> getFaultyResponses() {
		return faultyResponses;
	}
	
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isIgnoreUploads() {
		return ignoreUploads;
	}

	public void setIgnoreUploads(boolean ignoreUploads) {
		this.ignoreUploads = ignoreUploads;
	}

	public long getParallelRequestsHWM() {
		return parallelRequestsHWM;
	}

	public long getCriticalRequestCount() {
		synchronized (this) {
			return criticalRequests.size();
		}
	}

	public void setFaultStatusCodes(String statusCodes) {
		String[] codeSplit = statusCodes.split(",");
		this.faultStatusCodes = new ArrayList<Integer>(codeSplit.length); 
		for (String code : codeSplit) {
			this.faultStatusCodes.add(Integer.valueOf(code));
		}
	}

}
