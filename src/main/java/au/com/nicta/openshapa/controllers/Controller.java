package au.com.nicta.openshapa.controllers;

import au.com.nicta.openshapa.db.LogicErrorException;
import au.com.nicta.openshapa.db.SystemErrorException;

/**
 *
 * @author cfreeman
 */
public interface Controller {
    void execute() throws SystemErrorException, LogicErrorException;
}
