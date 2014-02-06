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
package org.datavyu.controllers;

import org.datavyu.Datavyu;
import org.datavyu.views.NewProjectV;

import javax.swing.*;

/**
 * Controller for creating a new project.
 */
public class NewProjectC {

    public NewProjectC() {
        // Create the view, register this controller with it and display it.
        JFrame mainFrame = Datavyu.getApplication().getMainFrame();
        NewProjectV view = new NewProjectV(mainFrame, true);
        Datavyu.getApplication().show(view);
    }

}
