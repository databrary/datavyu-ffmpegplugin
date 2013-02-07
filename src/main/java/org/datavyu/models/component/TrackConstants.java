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
package org.datavyu.models.component;

import java.awt.Color;

import javax.swing.ImageIcon;


public interface TrackConstants {

    static final Color BORDER_COLOR = new Color(73, 73, 73);
    static final int CARRIAGE_HEIGHT = 75;
    static final int HEADER_WIDTH = 140;
    static final int ACTION_BUTTON_WIDTH = 20;
    static final int ACTION_BUTTON_HEIGHT = 20;

    /** Icon for hiding a track video. */
    static final ImageIcon VIEWER_HIDE_ICON = new ImageIcon(TrackConstants.class
            .getResource("/icons/eye.png"));

    /** Icon for showing the video. */
    static final ImageIcon VIEWER_SHOW_ICON = new ImageIcon(TrackConstants.class
            .getResource("/icons/eye-shut.png"));

    /** Unlock icon. */
    static final ImageIcon UNLOCK_ICON = new ImageIcon(TrackConstants.class
            .getResource("/icons/track-unlock.png"));

    /** Lock icon. */
    static final ImageIcon LOCK_ICON = new ImageIcon(TrackConstants.class
            .getResource("/icons/track-lock.png"));

    /** Delete icon. */
    static final ImageIcon DELETE_ICON = new ImageIcon(TrackConstants.class
            .getResource("/icons/emblem-unreadable.png"));

}
