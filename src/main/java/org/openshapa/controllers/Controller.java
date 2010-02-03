package org.openshapa.controllers;

import org.openshapa.models.db.SystemErrorException;

/**
 *
 */
public interface Controller {
    void execute() throws SystemErrorException;
}
