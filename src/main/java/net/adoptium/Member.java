package net.adoptium;

public class Member {
    private String memberName;
    private String memberLink;
    private String memberLogo;
    private String memberAltText;
    private String organizationType;

    // Empty constructor is needed for ObjectMapper
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