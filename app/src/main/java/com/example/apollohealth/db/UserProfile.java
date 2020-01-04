package com.example.apollohealth.db;

public class UserProfile {
    private String uName;
    private String uGender;
    private int uAge;
    private float uHeight;
    private float uWeight;

    public String getuName() {
        return uName;
    }

    public String getuGender() {
        return uGender;
    }

    public int getuAge() {
        return uAge;
    }

    public float getuHeight() {
        return uHeight;
    }

    public float getuWeight() {
        return uWeight;
    }

    private UserProfile(UserBuilder builder){
        this.uName = builder.uName;
        this.uGender = builder.uGender;
        this.uAge = builder.uAge;
        this.uHeight = builder.uHeight;
        this.uWeight = builder.uWeight;
    }

    public static class UserBuilder {
        private String uName;
        private String uGender;
        private int uAge;
        private float uHeight;
        private float uWeight;

        public UserBuilder uname(String uName) {
            this.uName = uName;
            return this;
        }

        public UserBuilder ugender(String uGender) {
            this.uGender = uGender;
            return this;
        }

        public UserBuilder uage(int uAge) {
            this.uAge = uAge;
            return this;
        }

        public UserBuilder uheight(float uHeight) {
            this.uHeight = uHeight;
            return this;
        }

        public UserBuilder uweight(float uWeight) {
            this.uWeight = uWeight;
            return this;
        }
        public UserProfile build(){
            UserProfile userProfile = new UserProfile(this);
            return userProfile;
        }
    }
}
