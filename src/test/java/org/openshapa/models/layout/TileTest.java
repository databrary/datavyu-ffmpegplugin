package org.openshapa.models.layout;

import java.awt.Dimension;
import java.awt.Rectangle;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertFalse;

import org.testng.annotations.Test;

import com.google.common.collect.Iterables;


@Test public class TileTest {

    public void testRemainder1() {
        Tile tile = new Tile(1000, 1000, 0, 0);
        Dimension d = new Dimension(1000, 1000);

        Iterable<Tile> remainders = tile.subtract(d);
        assertEquals(0, Iterables.size(remainders));
    }

    public void testRemainder2() {
        Tile tile = new Tile(1000, 1000, 0, 0);
        Dimension d = new Dimension(1000, 500);

        Iterable<Tile> remainders = tile.subtract(d);
        assertEquals(1, Iterables.size(remainders));

        Tile r = Iterables.get(remainders, 0);
        assertEquals(1000, r.getWidth());
        assertEquals(500, r.getHeight());
        assertEquals(0, r.getX());
        assertEquals(500, r.getY());
    }

    public void testRemainder3() {
        Tile tile = new Tile(1000, 1000, 0, 0);
        Dimension d = new Dimension(500, 1000);

        Iterable<Tile> remainders = tile.subtract(d);
        assertEquals(1, Iterables.size(remainders));

        Tile r = Iterables.get(remainders, 0);
        assertEquals(500, r.getWidth());
        assertEquals(1000, r.getHeight());
        assertEquals(500, r.getX());
        assertEquals(0, r.getY());
    }

    public void testRemainder4() {
        Tile tile = new Tile(1000, 1000, 0, 0);
        Dimension d = new Dimension(500, 500);

        Iterable<Tile> remainders = tile.subtract(d);
        assertEquals(2, Iterables.size(remainders));

        Tile expected1 = new Tile(500, 1000, 500, 0);
        Tile expected2 = new Tile(500, 500, 0, 500);

        assertTrue(Iterables.contains(remainders, expected1));
        assertTrue(Iterables.contains(remainders, expected2));
    }

    public void testRemainder5() {
        Tile tile = new Tile(1000, 1000, 100, 100);
        Dimension d = new Dimension(1000, 1000);

        Iterable<Tile> remainders = tile.subtract(d);
        assertEquals(0, Iterables.size(remainders));
    }

    public void testRemainder6() {
        Tile tile = new Tile(1000, 1000, 100, 100);
        Dimension d = new Dimension(1000, 500);

        Iterable<Tile> remainders = tile.subtract(d);
        assertEquals(1, Iterables.size(remainders));

        Tile r = Iterables.get(remainders, 0);
        assertEquals(1000, r.getWidth());
        assertEquals(500, r.getHeight());
        assertEquals(100, r.getX());
        assertEquals(600, r.getY());
    }

    public void testRemainder7() {
        Tile tile = new Tile(1000, 1000, 100, 100);
        Dimension d = new Dimension(500, 1000);

        Iterable<Tile> remainders = tile.subtract(d);
        assertEquals(1, Iterables.size(remainders));

        Tile r = Iterables.get(remainders, 0);
        assertEquals(500, r.getWidth());
        assertEquals(1000, r.getHeight());
        assertEquals(600, r.getX());
        assertEquals(100, r.getY());
    }

    public void testRemainder8() {
        Tile tile = new Tile(1000, 1000, 100, 100);
        Dimension d = new Dimension(500, 500);

        Iterable<Tile> remainders = tile.subtract(d);
        assertEquals(2, Iterables.size(remainders));

        Tile expected1 = new Tile(500, 1000, 600, 100);
        Tile expected2 = new Tile(500, 500, 100, 600);

        assertTrue(Iterables.contains(remainders, expected1));
        assertTrue(Iterables.contains(remainders, expected2));
    }

    public void testFitToTile1() {
        Tile tile = new Tile(1000, 1000, 0, 0);
        Dimension d = new Dimension(500, 500);

        Rectangle expected = new Rectangle(0, 0, 500, 500);
        assertEquals(expected, tile.fitToTile(d));
    }

    public void testFitToTile2() {
        Tile tile = new Tile(1000, 1000, 0, 0);
        Dimension d = new Dimension(1000, 500);

        Rectangle expected = new Rectangle(0, 0, 1000, 500);
        assertEquals(expected, tile.fitToTile(d));
    }

    public void testFitToTile3() {
        Tile tile = new Tile(1000, 1000, 0, 0);
        Dimension d = new Dimension(500, 1000);

        Rectangle expected = new Rectangle(0, 0, 500, 1000);
        assertEquals(expected, tile.fitToTile(d));
    }

    public void testFitToTile4() {
        Tile tile = new Tile(1000, 1000, 10, 10);
        Dimension d = new Dimension(1000, 1000);

        Rectangle expected = new Rectangle(10, 10, 1000, 1000);
        assertEquals(expected, tile.fitToTile(d));
    }

    public void testFitToTile5() {
        Tile tile = new Tile(1000, 1000, 10, 10);
        Dimension d = new Dimension(500, 500);

        Rectangle expected = new Rectangle(10, 10, 500, 500);
        assertEquals(expected, tile.fitToTile(d));
    }

    public void testFitToTile6() {
        Tile tile = new Tile(1000, 1000, 10, 10);
        Dimension d = new Dimension(1000, 500);

        Rectangle expected = new Rectangle(10, 10, 1000, 500);
        assertEquals(expected, tile.fitToTile(d));
    }

    public void testFitToTile7() {
        Tile tile = new Tile(1000, 1000, 10, 10);
        Dimension d = new Dimension(500, 1000);

        Rectangle expected = new Rectangle(10, 10, 500, 1000);
        assertEquals(expected, tile.fitToTile(d));
    }

    public void testFitToTile8() {
        Tile tile = new Tile(1000, 1000, 10, 10);
        Dimension d = new Dimension(1000, 1000);

        Rectangle expected = new Rectangle(10, 10, 1000, 1000);
        assertEquals(expected, tile.fitToTile(d));
    }

    public void testFitToTile9() {
        Tile tile = new Tile(1000, 1000, 0, 0);
        Dimension d = new Dimension(1200, 1200);

        Rectangle expected = new Rectangle(0, 0, 1000, 1000);
        assertEquals(expected, tile.fitToTile(d));
    }

    public void testFitToTile10() {
        Tile tile = new Tile(1000, 1000, 0, 0);
        Dimension d = new Dimension(1200, 600);

        Rectangle expected = new Rectangle(0, 0, 1000, 500);
        assertEquals(expected, tile.fitToTile(d));
    }

    public void testFitToTile11() {
        Tile tile = new Tile(1000, 1000, 0, 0);
        Dimension d = new Dimension(600, 1200);

        Rectangle expected = new Rectangle(0, 0, 500, 1000);
        assertEquals(expected, tile.fitToTile(d));
    }

    public void testFitToTile12() {
        Tile tile = new Tile(1000, 1000, 0, 0);
        Dimension d = new Dimension(1200, 1100);

        // note: we are rounding down
        Rectangle expected = new Rectangle(0, 0, 1000, 916);
        assertEquals(expected, tile.fitToTile(d));
    }

    public void testFitToTile13() {
        Tile tile = new Tile(1000, 1000, 12, 12);
        Dimension d = new Dimension(1200, 1200);

        Rectangle expected = new Rectangle(12, 12, 1000, 1000);
        assertEquals(expected, tile.fitToTile(d));
    }

    public void testFitToTile14() {
        Tile tile = new Tile(1000, 1000, 12, 12);
        Dimension d = new Dimension(1200, 600);

        Rectangle expected = new Rectangle(12, 12, 1000, 500);
        assertEquals(expected, tile.fitToTile(d));
    }

    public void testFitToTile15() {
        Tile tile = new Tile(1000, 1000, 12, 12);
        Dimension d = new Dimension(600, 1200);

        Rectangle expected = new Rectangle(12, 12, 500, 1000);
        assertEquals(expected, tile.fitToTile(d));
    }

    public void testFitToTile16() {
        Tile tile = new Tile(1000, 1000, 12, 12);
        Dimension d = new Dimension(1200, 1100);

        // note: we are rounding down
        Rectangle expected = new Rectangle(12, 12, 1000, 916);
        assertEquals(expected, tile.fitToTile(d));
    }

    public void testOutside1() {
        Tile tile = new Tile(1000, 1000, 0, 0);
        Tile other = new Tile(1000, 1000, 1000, 0);

        assertTrue(tile.outside(other));
    }

    public void testOutside2() {
        Tile tile = new Tile(1000, 1000, 0, 0);
        Tile other = new Tile(1000, 1000, 500, 0);

        assertFalse(tile.outside(other));
    }

    public void testOutside3() {
        Tile tile = new Tile(100, 100, 100, 100);
        Tile other = new Tile(1000, 1000, 0, 0);

        assertFalse(tile.outside(other));
    }

}
