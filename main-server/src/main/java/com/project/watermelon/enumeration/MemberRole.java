package com.project.watermelon.enumeration;

public enum MemberRole {
    MEMBER(Authority.MEMBER), // 필수 입력 값이 누락된 비정상 유저
    QUALIFIED_MEMBER(Authority.QUALIFIED_MEMBER); // 필수 입력 값을 모두 입력한 정상 유저

    private final String authority;

    MemberRole(String authority){
        this.authority = authority;
    }

    public String getAuthority(){
        return this.authority;
    }

    public static class Authority {
        public static final String MEMBER = "ROLE_MEMBER";
        public static final String QUALIFIED_MEMBER = "ROLE_QUALIFIED_MEMBER";
    }
}
