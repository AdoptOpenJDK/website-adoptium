package net.adoptium;

import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DownloadResourceRegexExtractionTest {
/*
    @Test
    public void testRegexPatternWithExistingVersion() {
        String testString = "windows-x64-hotspot-jdk-normal-jdk-ga-adoptopenjdk-11.0.10+9";
        Pattern pattern = Pattern.compile(DownloadResource.ARGS_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(testString);
        assertEquals(true, matcher.find());
    }

    @Test
    public void testRegexPatternWithTypingMistake() {
        String testString = "windows-x64-hotspot-jdk-normal-jdk-ga-adoptopenjdk-11.0.10-+9";
        Pattern pattern = Pattern.compile(DownloadResource.ARGS_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(testString);
        assertEquals(true, matcher.find());
    }


    @Test
    public void testRegexGroups() {
        String testString = "os-arch-jvm_impl-image_type-heap_size-project-release_type-vendor-version\n";
        String[] groupValues = testString.split("-");
        Pattern pattern = Pattern.compile(DownloadResource.ARGS_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(testString);
        matcher.find();
        System.out.println(matcher.group());
        assertEquals(groupValues.length, matcher.groupCount());
    }*/
}