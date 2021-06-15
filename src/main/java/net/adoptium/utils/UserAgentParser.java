package net.adoptium.utils;

import net.adoptium.model.UserSystem;
import net.adoptopenjdk.api.v3.models.Architecture;
import net.adoptopenjdk.api.v3.models.OperatingSystem;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class UserAgentParser {

    private static final Map<OperatingSystem, String[]> osMap;

    static {
        osMap = new LinkedHashMap<>();
        osMap.put(OperatingSystem.linux, new String[]{"linux"});
        osMap.put(OperatingSystem.windows, new String[]{"windows"});
        osMap.put(OperatingSystem.mac, new String[]{"mac"});
        osMap.put(OperatingSystem.solaris, new String[]{"sunos"});
        osMap.put(OperatingSystem.aix, new String[]{"aix"});
        osMap.put(OperatingSystem.valueOf("alpine-linux"), new String[]{});
    }

    private static final Map<Architecture, String[]> archMap;

    static {
        archMap = new LinkedHashMap<>();
        archMap.put(Architecture.x64, new String[]{"x64", "win64", "wow64", "x86_64", "x86-64", "amd64"});
        archMap.put(Architecture.x32, new String[]{"x32", "win32", "x86_32"});
        archMap.put(Architecture.ppc64, new String[]{"ppc64"});
        archMap.put(Architecture.ppc64le, new String[]{"ppc64le"});
        archMap.put(Architecture.s390x, new String[]{});
        archMap.put(Architecture.aarch64, new String[]{"arch64"});
        archMap.put(Architecture.arm, new String[]{"arm"});
        archMap.put(Architecture.sparcv9, new String[]{});
        archMap.put(Architecture.riscv64, new String[]{});
    }

    private static final Map<OperatingSystem, Architecture> defaultArchOfOS;

    static {
        defaultArchOfOS = new LinkedHashMap<>();
        defaultArchOfOS.put(OperatingSystem.linux, Architecture.x64);
        defaultArchOfOS.put(OperatingSystem.windows, Architecture.x64);
        defaultArchOfOS.put(OperatingSystem.mac, Architecture.x64);
        defaultArchOfOS.put(OperatingSystem.solaris, Architecture.x64);
        defaultArchOfOS.put(OperatingSystem.aix, Architecture.ppc64);
        defaultArchOfOS.put(OperatingSystem.valueOf("alpine-linux"), Architecture.x64);
    }

    public static UserSystem getOsAndArch(final String userAgent) {
        userAgent = userAgent.toLowerCase(Locale.ENGLISH);
        final OperatingSystem os = parseOS(userAgent);

        // only call parseArch if os is not null
        return new UserSystem(os, os != null ? parseArch(userAgent, os) : null);
    }

    private static Architecture parseArch(final String ua, final OperatingSystem os) {
        Architecture arch = (Architecture) getSupportedOsArchFromMap(ua, archMap);
        if (arch == null) {
            arch = setDefaultArchOfOS(os);
        }
        return arch;
    }

    private static Architecture setDefaultArchOfOS(final OperatingSystem os) {
        for (final Map.Entry<OperatingSystem, Architecture> defaultEntry : defaultArchOfOS.entrySet()) {
            if (os.equals(defaultEntry.getKey())) {
                return defaultEntry.getValue();
            }
        }
        return null;
    }


    private static OperatingSystem parseOS(final String ua) {
        return (OperatingSystem) getSupportedOsArchFromMap(ua, osMap);
    }

    private static Object getSupportedOsArchFromMap(final String ua, final Map<?, String[]> map) {
        for (final Map.Entry<?, String[]> entry : map.entrySet()) {
            for (int i = 0; i < entry.getValue().length; i++) {
                if (ua.contains(entry.getValue()[i])) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }
}
