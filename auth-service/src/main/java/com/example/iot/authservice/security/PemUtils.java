package com.example.iot.authservice.security;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;

class PemUtils {

    private static String readClasspathFile(String path) throws Exception {
        try (InputStream is = PemUtils.class.getClassLoader().getResourceAsStream(path)) {
            if (is == null) throw new IllegalArgumentException("File not found: " + path);
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    static PrivateKey readPrivateKeyFromPem(String classpathPath) throws Exception {
        String pem = readClasspathFile(classpathPath)
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(pem);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(new PKCS8EncodedKeySpec(decoded));
    }

    static PublicKey readPublicKeyFromPem(String classpathPath) throws Exception {
        String pem = readClasspathFile(classpathPath)
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(pem);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(new X509EncodedKeySpec(decoded));
    }
}