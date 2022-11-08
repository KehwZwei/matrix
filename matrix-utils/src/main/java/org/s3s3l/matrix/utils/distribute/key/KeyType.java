package org.s3s3l.matrix.utils.distribute.key;

public enum KeyType {
    REGISTER("register"), CONFIG("config");

    private final String str;

    private KeyType(String str) {
        this.str = str;
    }

    public String str() {
        return this.str;
    }
}
