package net.adoptium;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Member {
    public String memberName;
    public String memberLink;
    public String memberLogo;
    public String memberAltText;
    public String organizationType;

    public Member(){
    }

    public Member(String memberName, String memberLink, String memberLogo, String memberAltText, String organizationType){
        this.memberName = memberName;
        this.memberLink = memberLink;
        this.memberLogo = memberLogo;
        this.memberAltText = memberAltText;
        this.organizationType = organizationType;
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
}