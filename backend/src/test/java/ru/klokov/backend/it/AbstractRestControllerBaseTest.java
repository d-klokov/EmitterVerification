package ru.klokov.backend.it;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

public abstract class AbstractRestControllerBaseTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER;

    static {
        POSTGRE_SQL_CONTAINER = new PostgreSQLContainer<>("postgres:15.4-alpine")
                .withUsername("klokov")
                .withPassword("klokov")
                .withDatabaseName("emitterverification_testcontainers");
    }

}
