package net.adoptium.utils;

import net.adoptium.exceptions.DownloadInvalidArgumentException;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static net.adoptium.utils.DownloadArgumentGroup.*;
import static org.junit.jupiter.api.Assertions.*;

class DownloadStringArgumentExtractorTest {

    @Test
    public void testArgumentGroups() {
        String testString = "windows-x64-hotspot-jdk-normal-jdk-ga-adoptopenjdk-11.0.10+9";
        Map<DownloadArgumentGroup, String> arguments = DownloadStringArgumentExtractor.getVersionDetails(testString);

        assertEquals(DownloadArgumentGroup.values().length, arguments.size());

        assertEquals("windows", arguments.get(OS));
        assertEquals("x64", arguments.get(ARCH));
        assertEquals("hotspot", arguments.get(JVM_IMPL));
        assertEquals("jdk", arguments.get(IMAGE_TYPE));
        assertEquals("normal", arguments.get(HEAP_SIZE));
        assertEquals("jdk", arguments.get(PROJECT));
        assertEquals("ga", arguments.get(RELEASE_TYPE));
        assertEquals("adoptopenjdk", arguments.get(VENDOR));
        assertEquals("11.0.10+9", arguments.get(VERSION));
    }

    @Test
    public void testVersionWithManyDots() {
        String testString = "windows-x64-hotspot-jdk-normal-jdk-ga-adoptopenjdk-11.0.0.0.0.0.0";
        Map<DownloadArgumentGroup, String> arguments = DownloadStringArgumentExtractor.getVersionDetails(testString);

        assertEquals(DownloadArgumentGroup.values().length, arguments.size());
        assertEquals("11.0.0.0.0.0.0", arguments.get(VERSION));
    }

    @Test
    public void testStringWithMinusSymbol() {
        String testString = "windows-x64-hotspot-jdk-normal-jdk-ga-adoptopenjdk-11.0.10-9";
        Map<DownloadArgumentGroup, String> arguments = DownloadStringArgumentExtractor.getVersionDetails(testString);

        assertEquals(DownloadArgumentGroup.values().length, arguments.size());
        assertEquals("11.0.10-9", arguments.get(VERSION));
    }

    @Test
    public void testFindsGroupsWithEnoughArgumentsAlthoughNotValidArguments() {
        //The DownloadStringArgumentExtractor should not check if the arguments are valid
        String testString = "windows-x-64-hotspot-jdk-normal-jdk-ga-adoptopenjdk-11.0.10-9";
        Map<DownloadArgumentGroup, String> arguments = DownloadStringArgumentExtractor.getVersionDetails(testString);

        assertEquals(DownloadArgumentGroup.values().length, arguments.size());
        assertEquals("x", arguments.get(ARCH));
        assertEquals("adoptopenjdk-11.0.10-9", arguments.get(VERSION));
    }

    @Test
    public void testInvalidStringPattern() {
        //There are not enough - symbols to generate the needed arguments
        String testString = "windows-x64-hotspot-jdk-normal-jdk-ga-adoptopenjdk";

        assertThrows(DownloadInvalidArgumentException.class, () -> {
           DownloadStringArgumentExtractor.getVersionDetails(testString);
        });
    }
}
