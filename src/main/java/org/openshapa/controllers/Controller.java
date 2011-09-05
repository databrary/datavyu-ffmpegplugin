package org.openshapa.controllers;

import database.SystemErrorException;

/**
 *
 */
public interface Controller {
    void execute() throws SystemErrorException;
}
