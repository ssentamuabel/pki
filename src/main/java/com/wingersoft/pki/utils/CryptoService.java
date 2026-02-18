package com.wingersoft.pki.utils;

import com.wingersoft.pki.dto.book.EncryptedPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.MGF1ParameterSpec;
import java.util.Base64;

@Service
public class CryptoService {

    @Autowired
    private PublicKey clientPublicKey;

    @Autowired
    private PublicKey serverPublicKey;

    @Autowired
    private PrivateKey serverPrivateKey;


    public <T> T decryptAndVerify(EncryptedPayload payload, Class<T> targetType) throws Exception{

        // 1. Base64 decode
        byte[] encryptedBytes = Base64.getDecoder()
                .decode(payload.getEncryptedData());

        byte[] signatureBytes = Base64.getDecoder()
                .decode(payload.getSignature());

        // 2. Verify signature before decryption
        Signature verifier  = Signature.getInstance("SHA256withRSA");
        verifier.initVerify(serverPublicKey);

        verifier.update(encryptedBytes);

        boolean isValid = verifier.verify(signatureBytes);

        if (isValid){
            throw new SecurityException("Invalid Signature");
        }

        // 3. decrypt with server private key
        OAEPParameterSpec oaepParams = new OAEPParameterSpec(
                "SHA-256",
                "MGF1",
                MGF1ParameterSpec.SHA256,
                PSource.PSpecified.DEFAULT
        );

        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPPadding");
        cipher.init(Cipher.DECRYPT_MODE, serverPrivateKey, oaepParams);

        byte[] decryptedBytes  = cipher.doFinal(encryptedBytes);

        // 4. Convert JSON -> Object
        String json = new String(decryptedBytes, StandardCharsets.UTF_8);
        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(json, targetType);

    }


    public EncryptedPayload encrypt(Object object) throws Exception{

        // 1. Convert object to JSON
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(object);
        byte[] plainBytes = json.getBytes(StandardCharsets.UTF_8);

//        RSAPublicKey rsaKey = (RSAPublicKey) clientPublicKey;
//        System.out.println("Java encrypt public modulus: " + rsaKey.getModulus());

        OAEPParameterSpec oaepParams = new OAEPParameterSpec(
                "SHA-256",
                "MGF1",
                MGF1ParameterSpec.SHA256,
                PSource.PSpecified.DEFAULT
        );




        // 2. Encrypt JSON using recipient public key
//        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
//        cipher.init(Cipher.ENCRYPT_MODE, clientPublicKey);
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPPadding");
        cipher.init(Cipher.ENCRYPT_MODE, clientPublicKey, oaepParams);
        byte[] encryptedBytes = cipher.doFinal(plainBytes);

        // 3. Sign the encrypted data using server private key
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(serverPrivateKey);
        signature.update(encryptedBytes);
        byte[] signatureBytes = signature.sign();

        // 4. Encode both as Base64 for transport
        return new EncryptedPayload(
                Base64.getEncoder().encodeToString(encryptedBytes),
                Base64.getEncoder().encodeToString(signatureBytes)
        );


    }

//    public EncryptedPayload encrypt(Object object) throws Exception{
//
//        ObjectMapper mapper = new ObjectMapper();
//        String json =  mapper.writeValueAsString(object);
//
//        // step 1: generate aes key
//        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
//        keyGen.init(256);
//        SecretKey aesKey = keyGen.generateKey();
//
//        // Encrypt Json with AES
//        Cipher aesCipher = Cipher.getInstance("AES/GCM/NoPadding");
//        byte[] iv = new byte[12];
//        SecureRandom random = new SecureRandom();
//        random.nextBytes(iv);
//        aesCipher.init(Cipher.ENCRYPT_MODE, aesKey, new GCMParameterSpec(128, iv));
//        byte[] encryptedData = aesCipher.doFinal(json.getBytes(StandardCharsets.UTF_8));
//
//
//        // Step 3: Encrypt AES key with client public key (RSA)
//        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
//        rsaCipher.init(Cipher.ENCRYPT_MODE, clientPublicKey);
//        byte[] encryptedKey = rsaCipher.doFinal(aesKey.getEncoded());
//
//        // Step 4: Return as base64
//        return new EncryptedPayload(
//                Base64.getEncoder().encodeToString(encryptedData),
//                Base64.getEncoder().encodeToString(encryptedKey),
//                Base64.getEncoder().encodeToString(iv)
//        );
//
//
//    }
}
