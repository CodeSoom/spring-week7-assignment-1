package com.codesoom.assignment.common.message;

public enum NormalMessage {
    JOIN_OK("회원가입이 성공했습니다!");

    private final String errorMsg;

    NormalMessage(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getNormalMsg() {
        return errorMsg;
    }

    public String getNormalMsg(Object... arg) {
        return String.format(errorMsg, arg);
    }
}
