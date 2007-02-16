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
package org.riotfamily.forms.resource;

public final class Resources {

	private Resources() {
	}
	
	public static final ScriptResource PROTOTYPE = 
			new ScriptResource("prototype/prototype.js", "Prototype");
	
	public static final ScriptResource SCRIPTACULOUS_EFFECTS = 
			new ScriptResource("scriptaculous/effects.js", "Effect");
	
	public static final ScriptResource SCRIPTACULOUS_DRAG_DROP = 
			new ScriptResource("scriptaculous/dragdrop.js", "Droppables");
	
	public static final ScriptResource SCRIPTACULOUS_SLIDER = 
			new ScriptResource("scriptaculous/slider.js", "Control.Slider");
	
	public static final ScriptSequence SCRIPTACULOUS_DRAG_DROP_SEQ =
			new ScriptSequence(new ScriptResource[] {
				PROTOTYPE, SCRIPTACULOUS_EFFECTS, SCRIPTACULOUS_DRAG_DROP 
			});
	
	public static final ScriptResource RIOT_WINDOW_CALLBACK = 
			new ScriptResource("riot-js/window-callback.js", "WindowCallback");
				
	public static final ScriptResource RIOT_NUMBER_INPUT = 
			new ScriptResource("riot-js/number-input.js");
	
	public static final ScriptResource RIOT_IMAGE_CHECKBOX = 
			new ScriptResource("riot-js/image-checkbox.js");

}
