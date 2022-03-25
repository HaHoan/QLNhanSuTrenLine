package com.umcvn.qlnhansutrenline;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PersonInfo {

        @SerializedName("ID")
        @Expose
        private String id;
        @SerializedName("STAFF_CODE")
        @Expose
        private String staffCode;
        @SerializedName("LINE_ID")
        @Expose
        private String lineId;
        @SerializedName("PROCESS")
        @Expose
        private String process;
        @SerializedName("UPD_TIME")
        @Expose
        private String updTime;

    public PersonInfo(String staffCode, String lineId, String process,String Id) {
        this.staffCode = staffCode;
        this.lineId = lineId;
        this.process = process;
        this.id = Id;
    }

    public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getStaffCode() {
            return staffCode;
        }

        public void setStaffCode(String staffCode) {
            this.staffCode = staffCode;
        }

        public String getLineId() {
            return lineId;
        }

        public void setLineId(String lineId) {
            this.lineId = lineId;
        }

        public String getProcess() {
            return process;
        }

        public void setProcess(String process) {
            this.process = process;
        }

        public String getUpdTime() {
            return updTime;
        }

        public void setUpdTime(String updTime) {
            this.updTime = updTime;
        }

}
