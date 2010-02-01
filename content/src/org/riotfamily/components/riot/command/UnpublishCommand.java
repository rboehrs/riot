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
package org.riotfamily.components.riot.command;

import org.riotfamily.components.model.ContentContainerOwner;
import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.CommandResult;
import org.riotfamily.core.screen.list.command.impl.support.AbstractBatchCommand;
import org.riotfamily.core.screen.list.command.result.RefreshListResult;

public class UnpublishCommand extends AbstractBatchCommand<ContentContainerOwner> {

	@Override
	protected String getName() {
		return "unpublish";
	}
	
	@Override
	protected String getAction() {
		return "publish";
	}
	
	@Override
	protected String getIcon() {
		return "stop";
	}
	
	@Override
	protected boolean isEnabled(CommandContext context, 
			ContentContainerOwner owner, int index, int selectionSize) {
		
		return owner.getContentContainer().getLiveVersion() != null;
	}
	
	@Override
	protected CommandResult execute(CommandContext context, 
			ContentContainerOwner owner, int index, int selectionSize) {

		owner.getContentContainer().unpublish();
		return new RefreshListResult();
	}
}
