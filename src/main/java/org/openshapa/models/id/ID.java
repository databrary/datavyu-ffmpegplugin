package org.openshapa.models.id;

public class ID implements Identifier {

    private final long number;

    public ID(final long sn) {
        this.number = sn;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + (int) (number ^ (number >>> 32));

        return result;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override public boolean equals(final Object obj) {

        if (this == obj)
            return true;

        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        ID other = (ID) obj;

        if (number != other.number)
            return false;

        return true;
    }

}
