package net.adoptium;

import org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserAgentParserTest {

    @BeforeEach
    void setUp() {

    }

    @Test
    void getOsAndArchTestWindowsX64() {
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:87.0) Gecko/20100101 Firefox/87.0";
        String[] arch = {"windows", "x64"};
        String[] osArch = UserAgentParser.getOsAndArch(userAgent);
        assertArrayEquals(arch,osArch);
    }

    @Test
    void getOsAndArchTestLinuxX64() {
        String userAgent = "Mozilla/5.0 (X11; Linux x86_64; rv:2.1.1) Gecko/ Firefox/5.0.1";
        String[] arch = {"linux", "x64"};
        String[] osArch = UserAgentParser.getOsAndArch(userAgent);
        assertArrayEquals(arch,osArch);
    }

    @Test
    void getOsAndArchTestLinuxNotWindows() {
        String userAgent = "Mozilla/5.0 (X11; Linux x86_64; rv:2.1.1) Gecko/ Firefox/5.0.1";
        String[] arch = {"windows", "x64"};
        String[] osArch = UserAgentParser.getOsAndArch(userAgent);
        assertNotEquals(arch[0],osArch[0]);
    }
}