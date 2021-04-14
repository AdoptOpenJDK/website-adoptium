package net.adoptium;

public class UserAgentParser {
    public static String[] getOsAndArch(String userAgent){
        userAgent = userAgent.toLowerCase();
        String[] osArch = new String[2];
        if(userAgent.contains("windows")){
            osArch[0] = "windows";
        } else if(userAgent.contains("linux")){
            osArch[0] = "linux";
        }
        if(userAgent.contains("x64") || userAgent.contains("x86_64") || userAgent.contains("64")){
            osArch[1] = "x64";
        } else if(userAgent.contains("x86")){
            osArch[1] = "x86";
        }
        return osArch;
    }
}
