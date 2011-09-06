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
package org.openshapa.plugins.spectrum.engine;

/**
 * Enumeration of engine states.
 */
public enum EngineState {

    /** Engine is initializing. */
    INITIALIZING("Initializing"),

    /** Engine is adjusting playback speed. */
    ADJUSTING_SPEED("Adjusting speed"),

    /** Engine is seeking through media. */
    SEEKING("Seeking"),

    /** Engine is playing back media. */
    PLAYING("Playing"),

    /** Engine is stopping media playback. */
    STOPPING("Stopping"),

    /**
     * This state overwrites the engine state once a task has been handled. Not
     * to be queued.
     */
    TASK_COMPLETE("Task complete");

    /** Name/description of the state. */
    private String stateName;

    /**
     * Create a new state with the given name/description.
     *
     * @param stateName
     */
    EngineState(final String stateName) {
        this.stateName = stateName;
    }

    @Override
    public String toString() {
        return stateName;
    }
}
