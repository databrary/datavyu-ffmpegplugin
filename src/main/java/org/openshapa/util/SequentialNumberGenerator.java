package org.openshapa.util;

/**
 * Create a new static sequential number generator, as an external sequential 
 * validation tool.
 *
 * @author cfreeman
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
