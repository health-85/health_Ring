package com.healthy.rvigor.bean;

import java.io.Serializable;

/**
 * Oos接口类
 */
public class Aliyun implements Serializable {

    private String requestId;
    private CredentialsDTO credentials;
    private AssumedRoleUserDTO assumedRoleUser;

    public static class CredentialsDTO implements Serializable {
        private String securityToken;
        private String accessKeySecret;
        private String accessKeyId;
        private String expiration;

        public String getSecurityToken() {
            return securityToken;
        }

        public void setSecurityToken(String securityToken) {
            this.securityToken = securityToken;
        }

        public String getAccessKeySecret() {
            return accessKeySecret;
        }

        public void setAccessKeySecret(String accessKeySecret) {
            this.accessKeySecret = accessKeySecret;
        }

        public String getAccessKeyId() {
            return accessKeyId;
        }

        public void setAccessKeyId(String accessKeyId) {
            this.accessKeyId = accessKeyId;
        }

        public String getExpiration() {
            return expiration;
        }

        public void setExpiration(String expiration) {
            this.expiration = expiration;
        }
    }

    public static class AssumedRoleUserDTO implements Serializable {
        private String arn;
        private String assumedRoleId;

        public String getArn() {
            return arn;
        }

        public void setArn(String arn) {
            this.arn = arn;
        }

        public String getAssumedRoleId() {
            return assumedRoleId;
        }

        public void setAssumedRoleId(String assumedRoleId) {
            this.assumedRoleId = assumedRoleId;
        }
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public CredentialsDTO getCredentials() {
        return credentials;
    }

    public void setCredentials(CredentialsDTO credentials) {
        this.credentials = credentials;
    }

    public AssumedRoleUserDTO getAssumedRoleUser() {
        return assumedRoleUser;
    }

    public void setAssumedRoleUser(AssumedRoleUserDTO assumedRoleUser) {
        this.assumedRoleUser = assumedRoleUser;
    }
}
