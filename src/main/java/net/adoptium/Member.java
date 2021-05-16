package net.adoptium;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Member implements Comparable<Member> {
    private String memberName;
    private String memberLink;
    private String memberLogo;
    private String memberAltText;
    private String organizationType;
    private boolean isValid = false;

    // Empty constructor is needed for ObjectMapper
    public Member(){
    }

    public String getMemberName() {
        return memberName;
    }

    public String getMemberLink() {
        return memberLink;
    }

    public String getMemberLogo() {
        return memberLogo;
    }

    public String getMemberAltText() {
        return memberAltText;
    }

    public String getOrganizationType() {
        return organizationType;
    }

    public boolean getIsValid(){
        return isValid;
    }

    public void validateURL() {
        String regex = "((http?|https|ftp|file)://)?((W|w){3}.)?[a-zA-Z0-9]+.[a-zA-Z]+(/)?";

        Pattern p = Pattern.compile(regex);

        String url = memberLink;

        if (url == null) {
            isValid = false;
        }

        Matcher m = p.matcher(url);
        isValid = m.matches();
    }

    @Override
    public int compareTo(@NotNull Member o) {
        return this.memberName.toLowerCase().compareTo(o.memberName.toLowerCase());
    }
}