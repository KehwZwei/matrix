package org.s3s3l.matrix.utils.convert;

public class IdentityConvertor implements Convertor<String, String> {

    @Override
    public String convert(String input) {
        return input;
    }
    
}
