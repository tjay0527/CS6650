package org.example;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SwipeDao {
    private static BasicDataSource dataSource;

    public SwipeDao() {
        dataSource = DBCPDataSource.getDataSource();
    }
    public void insertToLike(String swiper, String leftOrRight) {
        Connection connection = null;
        PreparedStatement statement = null;
        String columnName = leftOrRight.equals("right") ? "likes" : "dislikes";
        String sql =
                "INSERT INTO Twinder.Like (swiper, likes, dislikes) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE "
                        + columnName + " = " + columnName + " + 1";
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, swiper);
            statement.setInt(2, leftOrRight.equals("right") ? 1 : 0);
            statement.setInt(3, leftOrRight.equals("right") ? 0 : 1);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }
}

