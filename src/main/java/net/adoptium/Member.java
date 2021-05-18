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
    private boolean isURLValid = false;
    private boolean isImageFormatValid = false;

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

    public boolean getIsURLValid(){
        return isURLValid;
    }

    public boolean getIsImageFormatValid() {
        return isImageFormatValid;
    }

    public void validateURL() {
        String regex = "^(http:\\/\\/www\\.|https:\\/\\/www\\.|http:\\/\\/|https:\\/\\/)?[a-zA-Z0-9]+([\\-\\.]{1}[a-zA-Z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(\\/.*)?$";

        Pattern p = Pattern.compile(regex);

        String url = memberLink;

        Matcher m = p.matcher(url);
        isURLValid = m.matches();
    }

    public void validateImageFormat() {
        String regex = "([a-zA-Z0-9-_]+/)*[a-zA-Z0-9-_]+\\.(svg)";

        Pattern p = Pattern.compile(regex);

        String imageFileName = memberLogo;

        Matcher m = p.matcher(imageFileName);
        isImageFormatValid = m.matches();
    }

    @Override
    public int compareTo(@NotNull Member o) {
        return this.memberName.toLowerCase().compareTo(o.memberName.toLowerCase());
    }
}