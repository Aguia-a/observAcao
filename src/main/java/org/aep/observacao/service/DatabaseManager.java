package org.aep.observacao.service;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_DIRECTORY = "persistence";
    private static final String JDBC_URL = "jdbc:h2:./" + DB_DIRECTORY + "/observacao-db;AUTO_SERVER=TRUE;DATABASE_TO_UPPER=false";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    public static void initializeDatabase() {
        createDataDirectory();
        try (Connection connection = getConnection()) {
            createTables(connection);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inicializar o banco de dados H2", e);
        }
    }

    private static void createDataDirectory() {
        File dataFolder = new File(DB_DIRECTORY);
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }

    private static void createTables(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS solicitacao ("
                            + "id INT PRIMARY KEY, "
                            + "protocolo VARCHAR(32) NOT NULL, "
                            + "categoria VARCHAR(100) NOT NULL, "
                            + "categoria_sla INT NOT NULL, "
                            + "descricao VARCHAR(1000) NOT NULL, "
                            + "localizacao VARCHAR(255) NOT NULL, "
                            + "prioridade VARCHAR(32) NOT NULL, "
                            + "status VARCHAR(32) NOT NULL, "
                            + "data_criacao VARCHAR(64) NOT NULL, "
                            + "usuario_id INT, "
                            + "usuario_nome VARCHAR(255), "
                            + "usuario_email VARCHAR(255), "
                            + "usuario_telefone VARCHAR(64), "
                            + "anonimo BOOLEAN NOT NULL"
                            + ")"
            );
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS historico_status ("
                            + "id INT PRIMARY KEY, "
                            + "solicitacao_id INT NOT NULL, "
                            + "status VARCHAR(32) NOT NULL, "
                            + "data VARCHAR(64) NOT NULL, "
                            + "responsavel VARCHAR(255) NOT NULL, "
                            + "comentario VARCHAR(1000) NOT NULL"
                            + ")"
            );
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    }
}
