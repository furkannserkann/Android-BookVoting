package com.furkanyilmaz.items;

public class myEnums {

    public static enum Authority {
        KULLANICI("Kullanıcı", 0),
        ADMIN("Admin", 1);

        private String stringValue;
        private int intValue;
        private Authority(String stringValue, int intValue){
            this.stringValue = stringValue;
            this.intValue = intValue;
        }

        @Override
        public String toString() {
            return stringValue;
        }
    }

    public static enum Gender {
        MALE("Bay", 0),
        FEMALE("Bayan", 1);

        private String stringValue;
        private int intValue;
        private Gender(String stringValue, int intValue){
            this.stringValue = stringValue;
            this.intValue = intValue;
        }

        @Override
        public String toString() {
            return stringValue;
        }
    }

}
