/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.datavyu.undoableedits;

import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;


/**
 *
 */
public abstract class AddArgumentEdit extends VocabEditorEdit {
    /**
     * The logger for this class.
     */
    private static final Logger LOGGER = UserMetrix.getLogger(AddArgumentEdit.class);

    public AddArgumentEdit() {
        super();
    }

    @Override
    public String getPresentationName() {
        return "";
    }

    @Override
    public void undo() throws CannotRedoException {
        super.undo();

    }

    @Override
    public void redo() throws CannotUndoException {
        super.redo();

    }

}

