/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openshapa.models.db.legacy;

import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;
import java.util.Vector;
import java.util.logging.Level;

/**
 *
 * @author harold
 */
public class DataCellTO implements java.io.Serializable {
    /** The logger for this class. */
    private static final Logger LOGGER = UserMetrix.getLogger(DataCellTO.class);    
    /** onset of cell */
    public TimeStamp onset = null;
    /** offset of cell */
    public TimeStamp offset = null;    
    /** Argument list of the Matrix. */
    public Vector argList = null;    
    
    public DataCellTO(DataCell cell) {
        try {
            argList = new Vector();
            this.onset = cell.getOnset();
            this.offset = cell.getOffset();
            /** value of cell */
            Matrix val = cell.getVal();
            int numArgs = val.getNumArgs();
            for (int i = 0; i< numArgs; i++) {
                DataValue dv = val.getArgCopy(i);
                if ( dv instanceof TimeStampDataValue ) {
                                 argList.add(i, ((TimeStampDataValue)dv).getItsValue());  
                }
                else if ( dv instanceof NominalDataValue ) {
                                 argList.add(i, ((NominalDataValue)dv).getItsValue());                                 
                }
                else if ( dv instanceof TextStringDataValue ) {
                                 argList.add(i, ((TextStringDataValue)dv).getItsValue());    
                }
                else if ( dv instanceof PredDataValue ) {
                                 argList.add(i, ((PredDataValue)dv).getItsValue());                            
                }
                else if ( dv instanceof QueryVarDataValue ) {
                                 argList.add(i, ((QueryVarDataValue)dv).getItsValue());          
                }
                else if ( dv instanceof ColPredDataValue ) { 
                                 argList.add(i, ((ColPredDataValue)dv).getItsValue());                                                                   
                }
                else if ( dv instanceof QuoteStringDataValue ) {
                                 argList.add(i, ((QuoteStringDataValue)dv).getItsValue());                                    
                }
                else if ( dv instanceof FloatDataValue ) {
                                 argList.add(i, new Double(((QuoteStringDataValue)dv).getItsValue()));                                 
                }
                else if ( dv instanceof IntDataValue ) {
                                 argList.add(i, new Long(((IntDataValue)dv).getItsValue()));                                     
                }
                else if ( dv instanceof UndefinedDataValue ) {
                                 argList.add(i, ((UndefinedDataValue)dv).getItsValue());                                   
                }
               
            }                        
        } catch (SystemErrorException e) {
            LOGGER.error("Unable to get DataCell.", e);
        }       
    }
    
}
