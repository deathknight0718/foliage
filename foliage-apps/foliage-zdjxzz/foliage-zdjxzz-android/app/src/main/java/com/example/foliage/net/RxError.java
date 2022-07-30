package com.example.foliage.net;

/**
 * Description：Rx调用链上抛出的异常类型
 * Created by liang.qfzc@gmail.com on 2018/6/27.
 */

public class RxError extends Exception {

    /**
     * 错误码
     */
    public String errorCode = "400";

    /**
     * 错误类型
     */
//    public String type = ERROR_TYPE_COMMON;

    public RxError() {
    }

    private RxError(String detailMessage) {
        super(detailMessage);
    }

    private RxError(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    private RxError(Throwable throwable) {
        super(throwable);
    }

    /**
     * @param type 错误类型
     * @param errorCode 错误类型对应的错误码
     */
    public static RxError create(String type, String errorCode) {
        return create(type, errorCode, "", null);
    }

    public static RxError create(String type, String errorCode, String detailMessage) {
        return create(type, errorCode, detailMessage, null);
    }

    public static RxError create(String type, String errorCode, Throwable throwable) {
        return create(type, errorCode, "", throwable);
    }

    public static RxError create(String type, String errorCode, String detailMessage,
                                 Throwable throwable) {
        RxError e = new RxError(detailMessage, throwable);
        e.errorCode = errorCode;
//        e.type = type;
        return e;
    }
}
