import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MatchesDao {
    private static BasicDataSource dataSource;

    public MatchesDao() {
        dataSource = DBCPDataSource.getDataSource();
    }

    public List<String> getMatches(String swiper) throws SQLException {
        List<String> res = new ArrayList<>();
        String selectMatches = "SELECT * FROM Twinder.Potential WHERE swiper = ?";
        Connection connection = null;
        PreparedStatement selectStmt = null;
        ResultSet results = null;
        try {
            connection = dataSource.getConnection();
            selectStmt = connection.prepareStatement(selectMatches);
            selectStmt.setString(1, swiper);
            results = selectStmt.executeQuery();
            while (results.next()) {
                String potential = results.getString("swipee");
                res.add(potential);
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
        return res;
    }
}
