package com.umcvn.qlnhansutrenline;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SOService {
    @GET("/api/Values")
    Call<List<PersonInfo>> getAllStaffs();

    @GET("/api/process")
    Call<ProcessModel> getProcessInfo();

    @GET("/api/Values")
    Call<PersonInfo> getStaffBy(@Query("staffCode") String staffCode);

    @POST("/api/Values")
    @FormUrlEncoded
    Call<ResponseAPI> savePost(@Field("STAFF_CODE") String staffCode,
                        @Field("LINE_ID") String lineID,
                        @Field("PROCESS") String process,
                        @Field("ID") String ID);

}
