package org.s3s3l.matrix.utils.convert;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.s3s3l.matrix.utils.common.StringUtils;

public class BasicPathConvertor implements Convertor<String, String> {
    private static final List<String> IGNORE_PATHS_PREFIX = Arrays.asList("/assets/", "/_", "/css/", "/img/", "/js/",
            "/images/",
            "/?source", "/.", "/bin/");
    private static final Pattern PATTERN = Pattern.compile("/\\d+/?|\\.\\w+$");

    @Override
    public String convert(String input) {
        if (StringUtils.isEmpty(
                input)
                || IGNORE_PATHS_PREFIX.stream().filter(r -> input.startsWith(r)).findAny().isPresent()
                || PATTERN.matcher(input).find()) {
            return "OTHERS";
        }

        return input.replaceAll("//", "/");
    }

}
