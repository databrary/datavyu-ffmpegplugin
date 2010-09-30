package org.openshapa.controllers;

import org.openshapa.models.db.legacy.SystemErrorException;

/**
 *
 */
public interface Controller {
    void execute() throws SystemErrorException;
}
