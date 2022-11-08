package org.s3s3l.matrix.utils.bean.web;

public interface JsonResult<T> {

    int getCode();

    boolean isSuccess();

    String getMsg();

    T getData();
}
