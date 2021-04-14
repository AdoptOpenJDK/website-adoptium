package net.adoptium;

import java.util.HashMap;
import java.util.Map;

public class UserAgentParser {

    public static Map<String, String[]> osMap;
    static {
        osMap = new HashMap<>();
        osMap.put("linux", new String[]{"linux"});
        osMap.put("windows", new String[]{"windows"});
        osMap.put("mac", new String[]{"mac"});
        osMap.put("solaris", new String[]{});
        osMap.put("aix", new String[]{"aix"});
        osMap.put("alpine-linux", new String[]{});
    }

    public static Map<String, String[]> archMap;
    static {
        archMap = new HashMap<>();
        archMap.put("x64", new String[]{"x64", "win64", "wow64", "x86_64", "x86-64", "amd64"});
        archMap.put("x32", new String[]{"x32", "win32"});
        archMap.put("ppc64", new String[]{"ppc64"});
        archMap.put("ppc64le", new String[]{"ppc64le"});
        archMap.put("s390x", new String[]{});
        archMap.put("aarch64", new String[]{"arch64"});
        archMap.put("arm", new String[]{"arm"});
        archMap.put("sparcv9", new String[]{});
        archMap.put("riscv64", new String[]{});
    }

    public static String[] getOsAndArch(String userAgent){
        userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/605.1.15 (KHTML, like Gecko)";
        userAgent = userAgent.toLowerCase();
        String os;
        String arch;

        os = parseOS(userAgent);

        arch = parseArch(userAgent);

        return new String[]{os, arch};
    }

    private static String parseArch(String ua) {
        return getSupportedOsArchFromMap(ua, archMap);
    }


    private static String parseOS(String ua) {
        return getSupportedOsArchFromMap(ua, osMap);
    }

    private static String getSupportedOsArchFromMap(String ua, Map<String, String[]> archMap) {
        for (Map.Entry<String, String[]> archEntry : archMap.entrySet()) {
            for(int i =0; i < archEntry.getValue().length; i++)
            {
                if(ua.contains(archEntry.getValue()[i]))
                {
                    return archEntry.getKey();
                }
            }
        }
        return null;
    }
}
