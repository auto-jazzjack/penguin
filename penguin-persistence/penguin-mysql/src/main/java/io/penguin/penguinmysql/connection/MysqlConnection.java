package io.penguin.penguinmysql.connection;

import io.penguin.penguinmysql.config.MysqlIngredient;

import java.sql.Connection;
import java.sql.DriverManager;

public class MysqlConnection {

    private static Connection connection;

    public synchronized static Connection connection(MysqlIngredient mysqlIngredient) throws Exception {
        if (connection == null) {
            Class.forName(mysqlIngredient.getDriverClass());
            connection = DriverManager.getConnection(mysqlIngredient.getUrl(), mysqlIngredient.getUserName(), mysqlIngredient.getPassword());
        }
        return connection;
    }
}
