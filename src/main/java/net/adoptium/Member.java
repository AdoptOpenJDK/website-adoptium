package net.adoptium;

import org.jetbrains.annotations.NotNull;

public class Member implements Comparable<Member> {
    private String memberName;
    private String memberLink;
    private String memberLogo;
    private String memberAltText;
    private String organizationType;

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

    @Override
    public int compareTo(@NotNull Member o) {
        return this.memberName.toLowerCase().compareTo(o.memberName.toLowerCase());
    }
}