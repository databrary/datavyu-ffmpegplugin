package org.openshapa.models.layout;

import java.awt.Dimension;
import java.awt.Rectangle;

import java.util.List;

import com.google.common.collect.Lists;


public final class Tile {

    private final int width;
    private final int height;
    private final int x;
    private final int y;

    public Tile(final int width, final int height, final int x, final int y) {

        if ((width <= 0) || (height <= 0)) {
            throw new IllegalArgumentException("Invalid width or height.");
        }

        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
    }

    public Tile(final Rectangle r) {
        this.width = r.width;
        this.height = r.height;
        this.x = r.x;
        this.y = r.y;
    }

    /**
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * @return the x
     */
    public int getX() {
        return x;
    }

    /**
     * @return the y
     */
    public int getY() {
        return y;
    }

    /**
     * Fit the given item of dimension d into this tile, resizing if necessary.
     *
     * @param d
     * @return
     */
    public Rectangle fitToTile(final Dimension d) {

        if (d == null) {
            throw new NullPointerException();
        }

        if (!contains(d)) {

            // Resize the given item such that it fits inside of this tile and
            // maintains the item's aspect ratio.

            double scaler = height / (double) d.height;

            if (width < d.width) {
                double wscaler = width / (double) d.width;

                if (wscaler < scaler) {
                    scaler = wscaler;
                }
            }

            int newWidth = (int) (scaler * d.width);
            int newHeight = (int) (scaler * d.height);

            return new Rectangle(x, y, newWidth, newHeight);

        } else {
            return new Rectangle(x, y, d.width, d.height);
        }
    }

    /**
     * Calculate remainder tiles if an item with dimension d is placed inside
     * this tile. The minimum number of remainder tiles are calculated. Assumes
     * that the item will be placed in the top left corner.
     *
     * @param d
     */
    public Iterable<Tile> subtract(final Dimension d) {

        if (d == null) {
            throw new NullPointerException();
        }

        List<Tile> remainders = Lists.newLinkedList();

        if ((d.width < width) && (d.height < height)) {
            // We will get two blocks in this case. What we want to do is get
            // a split with the largest tile possible. This means having
            // to choose between splitting the tile either horizontally or
            // vertically.

            // Calculate vertical split advantage.
            int va1 = (width - d.width) * height;
            int va2 = d.width * (height - d.height);
            int maxVA = Math.max(va1, va2);

            // Calculate horizontal split advantage.
            int ha1 = (width - d.width) * (height - d.height);
            int ha2 = width * (height - d.height);
            int maxHA = Math.max(ha1, ha2);

            if (maxVA >= maxHA) {

                // Vertical is better.
                remainders.add(new Tile(width - d.width, height, x + d.width,
                        y));
                remainders.add(new Tile(d.width, height - d.height, x,
                        y + d.height));
            } else {

                // Horizontal is better.
                remainders.add(new Tile(width - d.width, height - d.height,
                        x + d.width, y));
                remainders.add(new Tile(width, height - d.height, x,
                        y + d.height));
            }

        } else {

            // Calculate horizontal remainders.
            if (d.width < width) {
                int rwidth = width - d.width;
                int rx = x + d.width;
                remainders.add(new Tile(rwidth, height, rx, y));
            }

            // Calculate vertical remainders.
            if (d.height < height) {
                int rheight = height - d.height;
                int ry = y + d.height;

                // Using d.width is intentional.
                remainders.add(new Tile(d.width, rheight, x, ry));
            }
        }

        return remainders;
    }

    /**
     * Area of this tile.
     */
    public int area() {
        return width * height;
    }

    /**
     * Calculates if the given tile is completely outside of this tile.
     */
    public boolean outside(final Tile t) {

        // Test if all of the corners in t are outside.
        if (enclosesPoint(t.x, t.y)) {
            return false;
        }

        if (enclosesPoint(t.x + t.width - 1, t.y)) {
            return false;
        }

        if (enclosesPoint(t.x, t.y + t.height - 1)) {
            return false;
        }

        if (enclosesPoint(t.x + t.width - 1, t.y + t.height - 1)) {
            return false;
        }

        // Test if this tile is outside of the given tile (for cases when the
        // given tile encloses this tile).
        if (t.enclosesPoint(x, y)) {
            return false;
        }

        if (t.enclosesPoint(x + width - 1, y)) {
            return false;
        }

        if (t.enclosesPoint(x, y + height - 1)) {
            return false;
        }

        if (t.enclosesPoint(x + width - 1, y + height - 1)) {
            return false;
        }

        return true;
    }

    /**
     * Test if a point is inside this tile.
     *
     * @param px
     * @param py
     * @return True if the given point is inside this tile; false otherwise.
     */
    public boolean enclosesPoint(final int px, final int py) {
        return (x <= px) && (px < (x + width)) && (y <= py)
            && (py < (y + height));
    }

    /**
     * Calculates if an item with dimension d can be placed inside of this
     * tile without resizing.
     *
     * @param d
     * @return True if the item can be placed without resizing, false otherwise.
     */
    private boolean contains(final Dimension d) {

        if (d == null) {
            throw new NullPointerException();
        }

        return (d.width <= width) && (d.height <= height);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + height;
        result = (prime * result) + width;
        result = (prime * result) + x;
        result = (prime * result) + y;

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

        Tile other = (Tile) obj;

        if (height != other.height)
            return false;

        if (width != other.width)
            return false;

        if (x != other.x)
            return false;

        if (y != other.y)
            return false;

        return true;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Tile [height=");
        builder.append(height);
        builder.append(", width=");
        builder.append(width);
        builder.append(", x=");
        builder.append(x);
        builder.append(", y=");
        builder.append(y);
        builder.append("]");

        return builder.toString();
    }
}
