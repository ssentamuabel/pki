package com.wingersoft.pki.dto.book;

import lombok.Data;

@Data
public class EncryptedPayload {

    private String encryptedData;
    private String signature;


    public EncryptedPayload(String encryptedData, String signature) {
        this.encryptedData = encryptedData;
        this.signature = signature;

    }

//    private String encryptedData;
//    private String encryptedKey;
//    private String iv;
//
//    public EncryptedPayload(String encryptedData, String encryptedKey, String iv) {
//        this.encryptedData = encryptedData;
//        this.encryptedKey = encryptedKey;
//        this.iv = iv;
//    }


}
