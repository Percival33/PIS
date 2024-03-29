package com.parcel.reportgenerator;

import org.junit.ClassRule;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class BaseIntegrationTest {
    @ClassRule
    public static final MinIOContainer minIOContainer;

    static {
        minIOContainer = new MinIOContainer("minio/minio");
        minIOContainer.start();
        System.getProperties().setProperty("spring.minio.host", minIOContainer.getHost());
        System.getProperties().setProperty("spring.minio.port", String.valueOf(minIOContainer.getFirstMappedPort()));
        System.getProperties().setProperty("spring.minio.username", minIOContainer.getUserName());
        System.getProperties().setProperty("spring.minio.password", minIOContainer.getPassword());
    }
}
