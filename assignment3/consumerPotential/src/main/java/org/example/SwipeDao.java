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

    public void insertToPotential(String swiper, String swipee, String leftOrRight) {
        if(leftOrRight.equals("left"))
            return;
        Connection connection = null;
        PreparedStatement statement = null;
        String sql =
                "INSERT INTO Twinder.Potential (swiper, swipee) VALUES (?, ?)";
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, swiper);
            statement.setString(2, swipee);
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


