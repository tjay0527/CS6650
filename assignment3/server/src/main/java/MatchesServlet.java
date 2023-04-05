import org.apache.commons.dbcp2.BasicDataSource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "MatchesServlet", value = "/matches/*")
public class MatchesServlet extends HttpServlet {
    private static BasicDataSource dataSource;

    @Override
    public void init(){
        dataSource = DBCPDataSource.getDataSource();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        String urlPath = req.getPathInfo();
        // check if we have a URL
        if (urlPath == null || urlPath.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            res.getWriter().write("missing parameters");
            return;
        }

        String[] urlParts = urlPath.split("/");
        // urlPath = "/matches/{userID}", urlParts = [, matches, userID]
        if (urlParts.length != 2) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            res.getWriter().write("Invalid url input: not matches");
            return;
        }
        String swiperID = urlParts[1];
        try {
            List<String> matches = new ArrayList<>();
            matches = getMatchesList(swiperID);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getMatchesList(String swiper) throws SQLException {
        MatchesDao matchesDao = new MatchesDao();
        List<String> res = new ArrayList<>();
        return res = matchesDao.getMatches(swiper);
    }
}
