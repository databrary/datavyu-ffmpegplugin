package org.openshapa.controllers;

import org.openshapa.db.LogicErrorException;
import org.openshapa.db.SystemErrorException;

/**
 *
 * @author cfreeman
 */
public interface Controller {
    void execute() throws SystemErrorException, LogicErrorException;
}
