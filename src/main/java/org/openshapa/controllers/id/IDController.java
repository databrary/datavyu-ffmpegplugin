package org.openshapa.controllers.id;

import org.openshapa.models.id.ID;
import org.openshapa.models.id.Identifier;


/**
 * Controller for generating {@link Identifier} objects.
 */
public enum IDController {

    INSTANCE;

    /** Sequence number. */
    private long sn;

    private IDController() {
        sn = 1;
    }

    private Identifier makeID() {
        return new ID(sn++);
    }

    public static synchronized Identifier generateIdentifier() {
        return INSTANCE.makeID();
    }

}
