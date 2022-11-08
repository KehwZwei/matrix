package org.s3s3l.matrix.utils.convert;

import java.time.Instant;
import java.time.ZonedDateTime;

public class TimeConvertor implements Convertor<String, Long> {

    @Override
    public Long convert(String input) {
        return Instant.from(ZonedDateTime.parse(input)).toEpochMilli();
    }
    
}
