package net.adoptium.model;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Member implements Comparable<Member> {
    // These private fields are necessary for mapping the JSON file objects. IDE doesn't recognize that.
    private String memberName;

    private String memberLink;

    private String memberLogo;

    private String memberAltText;

    private String organizationType;

    private boolean isURLValid = false;

    private boolean isImageFormatValid = false;

    /**
     * Empty constructor is needed for mapping JSON fields to objects with an ObjectMapper in MemberResource.java
     */
    public Member() {
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

    public boolean getIsURLValid() {
        return isURLValid;
    }

    public boolean getIsImageFormatValid() {
        return isImageFormatValid;
    }

    public void validateURL() {
        final String regex = "(http(s)?:)//(www\\.)?([a-zA-Z0-9-]{1,20}\\.){1,5}[a-zA-Z0-9-]{2,5}(/)?";

        final Pattern p = Pattern.compile(regex);

        final String url = memberLink;

        final Matcher m = p.matcher(url);
        isURLValid = m.matches();
    }

    public void validateImageFormat() {
        final String regex = "([a-zA-Z0-9-_]{1,20}/){1,10}[a-zA-Z0-9-_]+\\.(svg)";

        final Pattern p = Pattern.compile(regex);

        final String imageFileName = memberLogo;

        final Matcher m = p.matcher(imageFileName);
        isImageFormatValid = m.matches();
    }

    @Override
    public int compareTo(@NotNull final Member o) {
        return this.memberName.toLowerCase().compareTo(o.memberName.toLowerCase());
    }
}