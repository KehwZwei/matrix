package org.s3s3l.matrix.utils.convert;

import java.net.URI;

public class NginxPathConvertor extends BasicPathConvertor {

    @Override
    public String convert(String input) {
        String url = input.split(" ")[1];
        String path;
        int indexOfParamTag = url.indexOf("?");
        if (indexOfParamTag < 0) {
            path = URI.create(url).getPath();
        } else {
            path = URI.create(url.substring(0, indexOfParamTag)).getPath();
        }

        return super.convert(path);
    }

}
