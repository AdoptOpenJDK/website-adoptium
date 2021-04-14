package net.adoptium;

import ua_parser.Parser;

public class UserAgentParser {
    public static String[] getOsAndArch(String userAgent){
        String[] osArch = new String[2];
        Parser parser = new Parser();
        osArch[0] = parser.parse(userAgent).os.family;
        if(userAgent.contains("x64") || userAgent.contains("x86_64") || userAgent.contains("64")){
            osArch[1] = "x64";
        } else if(userAgent.contains("x86")){
            osArch[1] = "x86";
        }
        return osArch;
    }
}
