package com.smartventure.smartventure.config;

import okhttp3.OkHttpClient;
import javax.net.ssl.*;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class SslConfig {
    private static final String CERT_PATH = "src/main/resources/certs/russian_trusted_sub_ca.cer";

    public static OkHttpClient.Builder configureSsl(OkHttpClient.Builder builder) throws Exception {
        var cf = CertificateFactory.getInstance("X.509");
        var cert = (X509Certificate) cf.generateCertificate(new FileInputStream(CERT_PATH));

        var ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(null, null);
        ks.setCertificateEntry("caCert", cert);

        var tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ks);

        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(null, tmf.getTrustManagers(), null);

        builder.sslSocketFactory(ctx.getSocketFactory(), (X509TrustManager) tmf.getTrustManagers()[0]);
        return builder;
    }
}