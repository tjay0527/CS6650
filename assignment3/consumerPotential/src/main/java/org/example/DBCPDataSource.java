package org.example;
import org.apache.commons.dbcp2.BasicDataSource;

public class DBCPDataSource {
    private static BasicDataSource dataSource;

    // NEVER store sensitive information below in plain text!
    private static final String HOST_NAME = "mysql.ckokfgaignv6.us-west-2.rds.amazonaws.com";
    private static final String PORT = "3306";
    private static final String DATABASE = "Twinder";
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "password";

    static {
        dataSource = new BasicDataSource();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String url = String.format("jdbc:mysql://%s:%s/%s?enabledTLSProtocols=TLSv1.2", HOST_NAME, PORT, DATABASE);
        dataSource.setUrl(url);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        dataSource.setInitialSize(10);
        dataSource.setMaxTotal(60);
    }

    public static BasicDataSource getDataSource() {
        return dataSource;
    }
}
