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

    public static Map<String, String> defaultArchOfOS;
    static {
        defaultArchOfOS = new HashMap<>();
        defaultArchOfOS.put("linux", "x64");
        defaultArchOfOS.put("windows", "x64");
        defaultArchOfOS.put("mac", "x64");
        defaultArchOfOS.put("solaris", "x64");
        defaultArchOfOS.put("aix", "ppc64");
        defaultArchOfOS.put("alpine-linux", "x64");
    }

    public static String[] getOsAndArch(String userAgent){
        userAgent = userAgent.toLowerCase();
        String os;
        String arch = null;

        os = parseOS(userAgent);

        if(os != null) {
            arch = parseArch(userAgent, os);
        }

        return new String[]{os, arch};
    }

    private static String parseArch(String ua, String os) {
        String arch = getSupportedOsArchFromMap(ua, archMap);
        if(arch == null) {
            arch = setDefaultArchOfOS(os);
        }
        return arch;
    }

    private static String setDefaultArchOfOS(String os) {
        for (Map.Entry<String, String> defaultEntry : defaultArchOfOS.entrySet()) {
            if(os.equals(defaultEntry.getKey())){
                return defaultEntry.getValue();
            }
        }
        return null;
    }


    private static String parseOS(String ua) {
        return getSupportedOsArchFromMap(ua, osMap);
    }

    private static String getSupportedOsArchFromMap(String ua, Map<String, String[]> map) {
        for (Map.Entry<String, String[]> entry : map.entrySet()) {
            for(int i =0; i < entry.getValue().length; i++)
            {
                if(ua.contains(entry.getValue()[i]))
                {
                    return entry.getKey();
                }
            }
        }
        return null;
    }
}
