package org.s3s3l.matrix.utils.convert;

public class NumberToDoubleConvertor implements Convertor<Number, Double> {

    @Override
    public Double convert(Number input) {
        if (input == null) {
            return 0d;
        }
        return input.doubleValue();
    }
    
}
