package org.openshapa.controllers;

import org.openshapa.db.SystemErrorException;

/**
 *
 */
public interface Controller {
    void execute() throws SystemErrorException;
}
