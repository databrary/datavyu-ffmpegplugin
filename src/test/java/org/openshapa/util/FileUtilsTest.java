package org.openshapa.util;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

import org.testng.annotations.Test;


public class FileUtilsTest {

    @Test public void unixLongestCommonDir() {

        assertEquals("/var/data/",
            FileUtils.longestCommonDir("/var/data/stuff/xyz.dat",
                "/var/data/"));
        assertEquals("/a/", FileUtils.longestCommonDir("/a/b/c", "/a/x/y/"));
        assertEquals("/m/n/o/a/",
            FileUtils.longestCommonDir("/m/n/o/a/b/c", "/m/n/o/a/x/y/"));
    }

    @Test public void longestCommonDirWindows1() {
        String target = "C:\\Windows\\Boot\\Fonts\\chs_boot.ttf";
        String base = "C:\\Windows\\Speech\\Common\\sapisvr.exe";

        assertEquals("C:/Windows/", FileUtils.longestCommonDir(target, base));
    }

    @Test public void longestCommonDirWindows2() {
        String target = "C:\\Windows\\Boot\\Fonts\\chs_boot.ttf";
        String base = "C:\\Windows\\Speech\\Common\\";

        assertEquals("C:/Windows/", FileUtils.longestCommonDir(target, base));
    }

    @Test public void longestCommonDirWindows3() {
        String target = "C:\\Windows\\Boot\\Fonts";
        String base = "C:\\Windows\\Speech\\Common\\foo.txt";

        assertEquals("C:/Windows/", FileUtils.longestCommonDir(target, base));
    }

    @Test public void longestCommonDirWindows4() {
        String target = "C:\\Windows\\Boot\\";
        String base = "C:\\Windows\\Speech\\Common\\";

        assertEquals("C:/Windows/", FileUtils.longestCommonDir(target, base));
    }

    @Test public void longestCommonDirWindowsDifferentPrefix() {
        String target = "D:\\sources\\recovery\\RecEnv.exe";
        String base =
            "C:\\Java\\workspace\\AcceptanceTests\\Standard test data\\geo\\";

        assertNull(FileUtils.longestCommonDir(target, base));
    }

    @Test public void testLevelDifference1() {
        String base = "C:\\Windows\\";
        String target = "C:\\Windows\\Boot\\Fonts\\";

        assertEquals(2, FileUtils.levelDifference(base, target));
    }

    @Test public void testLevelDifference2() {
        String base = "C:\\Windows\\";
        String target = "C:\\Windows\\";

        assertEquals(0, FileUtils.levelDifference(base, target));
    }

    @Test public void testLevelDifference3() {
        String base = "C:\\Windows\\Boot\\";
        String target = "C:\\Windows\\Boot\\Fonts\\foo.ttf";

        assertEquals(1, FileUtils.levelDifference(base, target));
    }

    @Test public void testLevelDifference4() {
        String base = "/a/";
        String target = "/a/b/f/";

        assertEquals(2, FileUtils.levelDifference(base, target));
    }

    @Test public void testLevelDifference5() {
        String base = "/a/";
        String target = "/a/";

        assertEquals(0, FileUtils.levelDifference(base, target));
    }

    @Test public void testLevelDifference6() {
        String base = "/a/b/";
        String target = "/a/b/f/foo.ttf";

        assertEquals(1, FileUtils.levelDifference(base, target));
    }

    @Test public void testLevelDifference7() {
        String base = "/a/b/";
        String target = "/";

        assertEquals(-1, FileUtils.levelDifference(base, target));
    }

    @Test public void testLevelDifference8() {
        String base = "C:/a/b/";
        String target = "C:/";

        assertEquals(-1, FileUtils.levelDifference(base, target));
    }

}
