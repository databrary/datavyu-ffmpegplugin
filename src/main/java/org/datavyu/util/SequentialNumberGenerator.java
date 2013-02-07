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
package org.datavyu.util;

/**
 * Create a new static sequential number generator, as an external sequential 
 * validation tool.
 *
 */
public class SequentialNumberGenerator {

    private static SequentialNumberGenerator sng;
    private static int num = 0;

    private SequentialNumberGenerator(){
    }

    /*
     * Get a reference to the SNG
     */
    private static SequentialNumberGenerator getInstance(){

        if(sng == null){
            sng = new SequentialNumberGenerator();
        }

        return sng;
    }

    /*
     * Get the next number in the sequence
     */
    public static int getNextSeqNum(){
        return num++;
    }


}
