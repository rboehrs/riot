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
package org.riotfamily.revolt.refactor;

import org.riotfamily.revolt.Dialect;
import org.riotfamily.revolt.Refactoring;
import org.riotfamily.revolt.Script;
import org.riotfamily.revolt.definition.Column;
import org.riotfamily.revolt.definition.Database;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * 
 */
public class ModifyColumn implements Refactoring {

	private String table;

	private Column column;

	
	public ModifyColumn() {
	}

	public ModifyColumn(String table, Column column) {
		this.table = table;
		this.column = column;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public void setColumn(Column column) {
		this.column = column;
	}

	public void alterModel(Database database) {
		database.getTable(table).getColumn(column.getName()).merge(column);
	}
	
	public Script getScript(Dialect dialect) {
		return dialect.modifyColumn(table, column);
	}

}
