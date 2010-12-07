package org.openshapa.util;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

import org.testng.annotations.Test;


public class OFileUtilsTest {

    @Test public void unixLongestCommonDir() {

        assertEquals("/var/data/",
            OFileUtils.longestCommonDir("/var/data/stuff/xyz.dat",
                "/var/data/"));
        assertEquals("/a/", OFileUtils.longestCommonDir("/a/b/c", "/a/x/y/"));
        assertEquals("/m/n/o/a/",
            OFileUtils.longestCommonDir("/m/n/o/a/b/c", "/m/n/o/a/x/y/"));
    }

    @Test public void longestCommonDirWindows1() {
        String target = "C:\\Windows\\Boot\\Fonts\\chs_boot.ttf";
        String base = "C:\\Windows\\Speech\\Common\\sapisvr.exe";

        assertEquals("C:/Windows/", OFileUtils.longestCommonDir(target, base));
    }

    @Test public void longestCommonDirWindows2() {
        String target = "C:\\Windows\\Boot\\Fonts\\chs_boot.ttf";
        String base = "C:\\Windows\\Speech\\Common\\";

        assertEquals("C:/Windows/", OFileUtils.longestCommonDir(target, base));
    }

    @Test public void longestCommonDirWindows3() {
        String target = "C:\\Windows\\Boot\\Fonts";
        String base = "C:\\Windows\\Speech\\Common\\foo.txt";

        assertEquals("C:/Windows/", OFileUtils.longestCommonDir(target, base));
    }

    @Test public void longestCommonDirWindows4() {
        String target = "C:\\Windows\\Boot\\";
        String base = "C:\\Windows\\Speech\\Common\\";

        assertEquals("C:/Windows/", OFileUtils.longestCommonDir(target, base));
    }

    @Test public void longestCommonDirWindowsDifferentPrefix() {
        String target = "D:\\sources\\recovery\\RecEnv.exe";
        String base =
            "C:\\Java\\workspace\\AcceptanceTests\\Standard test data\\geo\\";

        assertNull(OFileUtils.longestCommonDir(target, base));
    }

    @Test public void testLevelDifference1() {
        String base = "C:\\Windows\\";
        String target = "C:\\Windows\\Boot\\Fonts\\";

        assertEquals(2, OFileUtils.levelDifference(base, target));
    }

    @Test public void testLevelDifference2() {
        String base = "C:\\Windows\\";
        String target = "C:\\Windows\\";

        assertEquals(0, OFileUtils.levelDifference(base, target));
    }

    @Test public void testLevelDifference3() {
        String base = "C:\\Windows\\Boot\\";
        String target = "C:\\Windows\\Boot\\Fonts\\foo.ttf";

        assertEquals(1, OFileUtils.levelDifference(base, target));
    }

    @Test public void testLevelDifference4() {
        String base = "/a/";
        String target = "/a/b/f/";

        assertEquals(2, OFileUtils.levelDifference(base, target));
    }

    @Test public void testLevelDifference5() {
        String base = "/a/";
        String target = "/a/";

        assertEquals(0, OFileUtils.levelDifference(base, target));
    }

    @Test public void testLevelDifference6() {
        String base = "/a/b/";
        String target = "/a/b/f/foo.ttf";

        assertEquals(1, OFileUtils.levelDifference(base, target));
    }

    @Test public void testLevelDifference7() {
        String base = "/a/b/";
        String target = "/";

        assertEquals(-1, OFileUtils.levelDifference(base, target));
    }

    @Test public void testLevelDifference8() {
        String base = "C:/a/b/";
        String target = "C:/";

        assertEquals(-1, OFileUtils.levelDifference(base, target));
    }

    @Test public void relativeToBase1() {
        String base = "C:\\Windows\\Boot\\";
        String target = "C:\\Windows\\Boot\\Fonts\\foo.ttf";

        assertEquals("Fonts/foo.ttf", OFileUtils.relativeToBase(base, target));
    }

    @Test public void relativeToBase2() {
        String base = "C:\\Windows\\Boot\\";
        String target = "C:\\Windows\\Boot\\boot.rom";

        assertEquals("boot.rom", OFileUtils.relativeToBase(base, target));
    }

    @Test public void relativeToBase3() {
        String base = "/a/b/";
        String target = "/a/b/boot.rom";

        assertEquals("boot.rom", OFileUtils.relativeToBase(base, target));
    }

    @Test public void relativeToBase4() {
        String base = "/a/b/";
        String target = "/a/b/c/d/e.file";

        assertEquals("c/d/e.file", OFileUtils.relativeToBase(base, target));
    }

    @Test public void relativeToBase5() {
        String base = "C:\\Windows\\Boot\\";
        String target = "/a/b/c/d/e.file";

        assertNull(OFileUtils.relativeToBase(base, target));
    }

}
