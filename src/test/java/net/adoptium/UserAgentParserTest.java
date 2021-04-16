package net.adoptium;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserAgentParserTest {

    @BeforeEach
    void setUp() {

    }

    @Test
    void getOsAndArchExpectedLinuxAsDefault() {
        String userAgent = "Mozilla/5.0 (X11; Linux x86_64; rv:2.1.1) Gecko/ Firefox/5.0.1";
        String[] arch = {"linux", "x64"};
        String[] osArch = UserAgentParser.getOsAndArch(userAgent);
        assertArrayEquals(arch,osArch);
    }

    @Test
    void getOsAndArchExpectedWindowsAsDefault() {
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:87.0) Gecko/20100101 Firefox/87.0";
        String[] arch = {"windows", "x64"};
        String[] osArch = UserAgentParser.getOsAndArch(userAgent);
        assertArrayEquals(arch,osArch);
    }

    @Test
    void getOsAndArchExpectedMacAsDefault() {
        String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_5) AppleWebKit/605.1.15 (KHTML, like Gecko)";
        String[] arch = {"mac", "x64"};
        String[] osArch = UserAgentParser.getOsAndArch(userAgent);
        assertArrayEquals(arch,osArch);
    }

    /*
    @Test
    void getOsAndArchExpectedSolarisAsDefault() {
        String userAgent = "Mozilla/5.0 (X11; U; SunOS sun4u; en-US; rv:1.8.0.5) Gecko/20060731 Firefox/1.5.0.3";
        String[] arch = {"solaris", "x64"};
        String[] osArch = UserAgentParser.getOsAndArch(userAgent);
        assertArrayEquals(arch,osArch);
    }
     */

    @Test
    void getOsAndArchExpectedAixAsDefault() {
        String userAgent = "Mozilla/5.0 (X11; U; AIX 6.1; en-US; rv:1.9.1.8) Gecko/20100623 Firefox/3.5.8";
        String[] arch = {"aix", "ppc64"};
        String[] osArch = UserAgentParser.getOsAndArch(userAgent);
        assertArrayEquals(arch,osArch);
    }

    /*
    @Test
    void getOsAndArchExpectedAlpineLinuxAsDefault() {
        String userAgent = "";
        String[] arch = {"alpine-linux", "x64"};
        String[] osArch = UserAgentParser.getOsAndArch(userAgent);
        assertArrayEquals(arch,osArch);
    }
     */

    @Test
    void getOsAndArchUserAgentToLowercaseWindows() {
        String userAgent = "Mozilla/5.0 (WiNdOwS NT 10.0; WIN32; rv:87.0) Gecko/20100101 Firefox/87.0";
        String[] arch = {"windows", "x32"};
        String[] osArch = UserAgentParser.getOsAndArch(userAgent);
        assertArrayEquals(arch,osArch);
    }

    @Test
    void getOsAndArchUserAgentToLowercaseLinux() {
        String userAgent = "Mozilla/5.0 (X11; Ubuntu; LINUX X86_32; rv:21.0) Gecko/20130331 Firefox/21.0";
        String[] arch = {"linux", "x32"};
        String[] osArch = UserAgentParser.getOsAndArch(userAgent);
        assertArrayEquals(arch,osArch);
    }

    @Test
    void getOsAndArchExpectedWindowsX32First() {
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; x32; rv:87.0) Gecko/20100101 Firefox/87.0";
        String[] arch = {"windows", "x32"};
        String[] osArch = UserAgentParser.getOsAndArch(userAgent);
        assertArrayEquals(arch,osArch);
    }

    @Test
    void getOsAndArchExpectedWindowsX32Second() {
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; win32; rv:87.0) Gecko/20100101 Firefox/87.0";
        String[] arch = {"windows", "x32"};
        String[] osArch = UserAgentParser.getOsAndArch(userAgent);
        assertArrayEquals(arch,osArch);
    }

    @Test
    void getOsAndArchExpectedWindowsX32Third() {
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; x86_32; rv:87.0) Gecko/20100101 Firefox/87.0";
        String[] arch = {"windows", "x32"};
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