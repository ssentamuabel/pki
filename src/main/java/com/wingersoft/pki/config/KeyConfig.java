package com.wingersoft.pki.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;


@Configuration
public class KeyConfig {

    @Value("${security.client-cert.path}")
    private Resource clientCert;


    @Value("${security.server-cert.path}")
    private Resource serverCert;


    @Value("${security.keystore.path}")
    private Resource keystore;

    @Value("${security.keystore.password}")
    private String keyStorePassword;

    @Value("${security.keystore.alias}")
    private String keystoreAlias;



    @Bean
    public PublicKey clientPublicKey() throws Exception{

        CertificateFactory factory = CertificateFactory.getInstance("X.509");

        try (InputStream in = clientCert.getInputStream()) {
            X509Certificate certificate = (X509Certificate) factory.generateCertificate(in);
            return certificate.getPublicKey();
        }
    }

    @Bean
    public PublicKey serverPublicKey() throws Exception{

        CertificateFactory factory = CertificateFactory.getInstance("X.509");

        try (InputStream in = serverCert.getInputStream()) {
            X509Certificate certificate = (X509Certificate) factory.generateCertificate(in);
            return certificate.getPublicKey();
        }
    }

    @Bean
    public PrivateKey serverPrivatekey() throws Exception{
        KeyStore ks = KeyStore.getInstance("PKCS12");

        ks.load(keystore.getInputStream(), keyStorePassword.toCharArray());

        Key key  = ks.getKey(keystoreAlias, keyStorePassword.toCharArray());

        if (!(key instanceof PrivateKey)) {
            throw new IllegalStateException("Key is not a private key for alias: " + keystoreAlias);
        }

        return (PrivateKey) key;
    }


    @Bean
    public String serverPublicKeyPem() throws Exception {
        CertificateFactory factory = CertificateFactory.getInstance("X.509");

        try (InputStream in = serverCert.getInputStream()) {
            X509Certificate cert =
                    (X509Certificate) factory.generateCertificate(in);

            PublicKey publicKey = cert.getPublicKey();
            byte[] spkiBytes = publicKey.getEncoded();

            String base64 = Base64.getMimeEncoder(64, new byte[]{'\n'})
                    .encodeToString(spkiBytes);

            return "-----BEGIN PUBLIC KEY-----\n"
                    + base64
                    + "\n-----END PUBLIC KEY-----";
        }
    }



//    @Bean
//    public String serverPublicCertPem() throws Exception {
//        CertificateFactory factory = CertificateFactory.getInstance("X.509");
//
//        try (InputStream in = serverCert.getInputStream()) {
//            X509Certificate certificate =
//                    (X509Certificate) factory.generateCertificate(in);
//
//            byte[] derBytes = certificate.getEncoded();
//
//            String base64 = Base64.getMimeEncoder(64, new byte[]{'\n'})
//                    .encodeToString(derBytes);
//
//            return "-----BEGIN CERTIFICATE-----\n"
//                    + base64
//                    + "\n-----END CERTIFICATE-----";
//        }
//    }





}




//@Configuration
//public class KeyConfig {
//
//    @Value("${security.truststore.path}")
//    private Resource truststore;
//
//    @Value("${security.truststore.password}")
//    private String password;
//
//    @Value("${security.truststore.alias}")
//    private String alias;
//
//    @Bean
//    public PublicKey clientPublicKey() throws Exception{
//
//        KeyStore ks = KeyStore.getInstance("PKCS12");
//        ks.load(truststore.getInputStream(), password.toCharArray());
//
//        Certificate cert = ks.getCertificate(alias);
//        if (cert  == null){
//            throw new IllegalStateException("No certificate found for alias: " + alias);
//        }
//        return cert.getPublicKey();
//    }
//
//
//}
