package com.testmongo.demo.exception;

public class PredictException extends BaseException {
    public PredictException(String code) {
        super("predict." + code);
    }

    public static PredictException predictError() {
        return new PredictException("error");
    }

}
