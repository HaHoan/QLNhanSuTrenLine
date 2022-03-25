package com.umcvn.qlnhansutrenline;

public class ApiUtils {
    public static final String BASE_URL = "http://172.28.10.17:112";

    public static SOService getSOService() {
        return RetrofitClient.getClient(BASE_URL).create(SOService.class);
    }
}
