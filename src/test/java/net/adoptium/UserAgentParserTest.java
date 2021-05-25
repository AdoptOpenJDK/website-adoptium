package net.adoptium;

import net.adoptium.model.UserSystem;
import net.adoptium.utils.UserAgentParser;
import net.adoptopenjdk.api.v3.models.Architecture;
import net.adoptopenjdk.api.v3.models.OperatingSystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserAgentParserTest {

    @BeforeEach
    void setUp() {

    }

    @Test
    void getOsAndArchExpectedLinuxAsDefault() {
        String userAgent = "Mozilla/5.0 (X11; Linux x86_64; rv:2.1.1) Gecko/ Firefox/5.0.1";
        UserSystem osArch = UserAgentParser.getOsAndArch(userAgent);
        assertThat(osArch.getArch()).isEqualTo(Architecture.x64);
        assertThat(osArch.getOs()).isEqualTo(OperatingSystem.linux);
    }

    @Test
    void getOsAndArchExpectedWindowsAsDefault() {
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:87.0) Gecko/20100101 Firefox/87.0";
        UserSystem osArch = UserAgentParser.getOsAndArch(userAgent);
        assertThat(osArch.getArch()).isEqualTo(Architecture.x64);
        assertThat(osArch.getOs()).isEqualTo(OperatingSystem.windows);
    }

    @Test
    void getOsAndArchExpectedMacAsDefault() {
        String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_5) AppleWebKit/605.1.15 (KHTML, like Gecko)";
        UserSystem osArch = UserAgentParser.getOsAndArch(userAgent);
        assertThat(osArch.getArch()).isEqualTo(Architecture.x64);
        assertThat(osArch.getOs()).isEqualTo(OperatingSystem.mac);
    }


    @Test
    void getOsAndArchExpectedSolarisAsDefault() {
        String userAgent = "Mozilla/5.0 (X11; U; SunOS sun4u; en-US; rv:1.8.0.5) Gecko/20060731 Firefox/1.5.0.3";
        UserSystem osArch = UserAgentParser.getOsAndArch(userAgent);
        assertThat(osArch.getArch()).isEqualTo(Architecture.x64);
        assertThat(osArch.getOs()).isEqualTo(OperatingSystem.solaris);
    }


    @Test
    void getOsAndArchExpectedAixAsDefault() {
        String userAgent = "Mozilla/5.0 (X11; U; AIX 6.1; en-US; rv:1.9.1.8) Gecko/20100623 Firefox/3.5.8";
        UserSystem osArch = UserAgentParser.getOsAndArch(userAgent);
        assertThat(osArch.getArch()).isEqualTo(Architecture.ppc64);
        assertThat(osArch.getOs()).isEqualTo(OperatingSystem.aix);
    }

    @Test
    void getOsAndArchUserAgentToLowercaseWindows() {
        String userAgent = "Mozilla/5.0 (WiNdOwS NT 10.0; WIN32; rv:87.0) Gecko/20100101 Firefox/87.0";
        UserSystem osArch = UserAgentParser.getOsAndArch(userAgent);
        assertThat(osArch.getArch()).isEqualTo(Architecture.x32);
        assertThat(osArch.getOs()).isEqualTo(OperatingSystem.windows);
    }

    @Test
    void getOsAndArchUserAgentToLowercaseLinux() {
        String userAgent = "Mozilla/5.0 (X11; Ubuntu; LINUX X86_32; rv:21.0) Gecko/20130331 Firefox/21.0";
        UserSystem osArch = UserAgentParser.getOsAndArch(userAgent);
        assertThat(osArch.getArch()).isEqualTo(Architecture.x32);
        assertThat(osArch.getOs()).isEqualTo(OperatingSystem.linux);
    }

    @Test
    void getOsAndArchExpectedWindowsX32First() {
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; x32; rv:87.0) Gecko/20100101 Firefox/87.0";
        UserSystem osArch = UserAgentParser.getOsAndArch(userAgent);
        assertThat(osArch.getArch()).isEqualTo(Architecture.x32);
        assertThat(osArch.getOs()).isEqualTo(OperatingSystem.windows);
    }

    @Test
    void getOsAndArchExpectedWindowsX32Second() {
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; win32; rv:87.0) Gecko/20100101 Firefox/87.0";
        UserSystem osArch = UserAgentParser.getOsAndArch(userAgent);
        assertThat(osArch.getArch()).isEqualTo(Architecture.x32);
        assertThat(osArch.getOs()).isEqualTo(OperatingSystem.windows);
    }

    @Test
    void getOsAndArchExpectedWindowsX32Third() {
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; x86_32; rv:87.0) Gecko/20100101 Firefox/87.0";
        UserSystem osArch = UserAgentParser.getOsAndArch(userAgent);
        assertThat(osArch.getArch()).isEqualTo(Architecture.x32);
        assertThat(osArch.getOs()).isEqualTo(OperatingSystem.windows);
    }

    @Test
    void getOsAndArchTestLinuxNotWindows() {
        String userAgent = "Mozilla/5.0 (X11; Linux x86_64; rv:2.1.1) Gecko/ Firefox/5.0.1";
        UserSystem osArch = UserAgentParser.getOsAndArch(userAgent);
        assertThat(osArch.getArch()).isEqualTo(Architecture.x64);
        assertThat(osArch.getOs()).isEqualTo(OperatingSystem.linux);
    }
}