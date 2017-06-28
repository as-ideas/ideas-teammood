package de.axelspringer.ideas.team.mood.letsencrypt;

import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import spark.resource.ClassPathResource;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;

public class TeamMoodHttpClientFactory {

    private static final char[] KEYSTORE_PASSWORD = "letsencrypt".toCharArray();
    private static final String LETSENCRYPT_TRUSTSTORE_PATH = "letsencrypt/letsencrypt-truststore";

    private final SecureRandom secureRandom = new SecureRandom();

    public CloseableHttpClient init() {
        final SSLContext sslContext = createSslContext();

        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext);

        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(
                RegistryBuilder.<ConnectionSocketFactory>create()
                        .register("http", PlainConnectionSocketFactory.getSocketFactory())
                        .register("https", sslSocketFactory)
                        .build()
        );

        return HttpClients.custom()
                .setConnectionManager(cm)
                .build();
    }

    private SSLContext createSslContext() {
        final SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("TLS");
            final TrustManagerFactory javaDefaultTrustManager = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            javaDefaultTrustManager.init((KeyStore) null);
            final TrustManagerFactory customCaTrustManager = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            customCaTrustManager.init(getKeyStore());

            sslContext.init(
                    null,
                    new TrustManager[]{
                            new TrustManagerDelegate(
                                    (X509TrustManager) customCaTrustManager.getTrustManagers()[0],
                                    (X509TrustManager) javaDefaultTrustManager.getTrustManagers()[0]
                            )
                    },
                    secureRandom
            );

        } catch (final Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
        return sslContext;
    }

    private KeyStore getKeyStore() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        KeyStore ks = KeyStore.getInstance("JKS");
        try (InputStream is = new ClassPathResource(LETSENCRYPT_TRUSTSTORE_PATH).getInputStream()) {
            ks.load(is, KEYSTORE_PASSWORD);
        }
        return ks;
    }

}