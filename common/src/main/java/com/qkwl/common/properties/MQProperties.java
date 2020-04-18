package com.qkwl.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "aliyun.mq")
public class MQProperties {

    private String accessKey;
    private String secretKey;
    private String onsAddr;
    private final Pid pid = new Pid();
    private final Cid cid = new Cid();

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getOnsAddr() {
        return onsAddr;
    }

    public void setOnsAddr(String onsAddr) {
        this.onsAddr = onsAddr;
    }

    public Pid getPid() {
        return pid;
    }

    public Cid getCid() {
        return cid;
    }

    public static class Pid {
        private String entrustState;
        private String validate;
        private String score;
        private String userAction;
        private String adminAction;

        public String getEntrustState() {
            return entrustState;
        }

        public void setEntrustState(String entrustState) {
            this.entrustState = entrustState;
        }

        public String getValidate() {
            return validate;
        }

        public void setValidate(String validate) {
            this.validate = validate;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }

        public String getUserAction() {
            return userAction;
        }

        public void setUserAction(String userAction) {
            this.userAction = userAction;
        }

        public String getAdminAction() {
            return adminAction;
        }

        public void setAdminAction(String adminAction) {
            this.adminAction = adminAction;
        }
    }

    public static class Cid {
        private String entrustState;
        private String validate;
        private String score;
        private String userAction;
        private String adminAction;

        public String getEntrustState() {
            return entrustState;
        }

        public void setEntrustState(String entrustState) {
            this.entrustState = entrustState;
        }

        public String getValidate() {
            return validate;
        }

        public void setValidate(String validate) {
            this.validate = validate;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }

        public String getUserAction() {
            return userAction;
        }

        public void setUserAction(String userAction) {
            this.userAction = userAction;
        }

        public String getAdminAction() {
            return adminAction;
        }

        public void setAdminAction(String adminAction) {
            this.adminAction = adminAction;
        }
    }
}