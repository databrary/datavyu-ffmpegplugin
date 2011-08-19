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
    
     /**
     * Compares this DataCellTO against another object.
     *
     * @param obj The object to compare this against.
     *
     * @return true if the Object obj is logically equal.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (obj.getClass() != this.getClass())) {
            return false;
        }

        // Must be this class to be here
        DataCellTO dcTO = (DataCellTO) obj;
        return onset == dcTO.onset && offset == dcTO.offset && this.compareVal(dcTO);
    }    
    
    public boolean compareVal(DataCellTO dcTO) {
        boolean result = true;
        int size = argList.size();
        int sizeDcTO = dcTO.argList.size();
        if (size != sizeDcTO) {
            result = false;
        } else {
            for (int i =0; i < size; i++) {
                Object o1 = this.argList.get(i);
                Object o2 = dcTO.argList.get(i);
                if (o1.equals(o2) == false) {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }
    
    public String argListToString() {
        String str = "";
        for (Object arg : argList) {
            if (arg != null) {
                str += arg.toString() + " ";
            } else {
                str += "<val>";
            }
        }
        return str;
    }
    
}
