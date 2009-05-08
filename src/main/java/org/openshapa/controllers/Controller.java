package org.openshapa.controllers;

import org.openshapa.db.LogicErrorException;
import org.openshapa.db.SystemErrorException;

/**
 *
 */
public interface Controller {
    void execute() throws SystemErrorException, LogicErrorException;
}
