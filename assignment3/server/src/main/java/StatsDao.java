import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StatsDao {
    private static BasicDataSource dataSource;

    public StatsDao() {
        dataSource = DBCPDataSource.getDataSource();
    }

    public String getStats(String swiper) throws SQLException {
        StringBuilder res = new StringBuilder();
        String selectMatches = "SELECT * FROM Twinder.Like WHERE swiper = ?";
        Connection connection = null;
        PreparedStatement selectStmt = null;
        ResultSet results = null;
        try {
            connection = dataSource.getConnection();
            selectStmt = connection.prepareStatement(selectMatches);
            selectStmt.setString(1, swiper);
            results = selectStmt.executeQuery();
            if (results.next()) {
                String stats = "";
                String likes = results.getString("likes");
                String dislikes = results.getString("dislikes");
                stats = "likes: " + likes + ", dislikes: " + dislikes;
                res.append(stats);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (connection != null) {
                connection.close();
            }
            if (selectStmt != null) {
                selectStmt.close();
            }
            if (results != null) {
                results.close();
            }
        }
        return res.toString();
    }
}
