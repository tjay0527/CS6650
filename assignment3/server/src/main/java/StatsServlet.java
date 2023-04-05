import com.rabbitmq.client.Channel;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.pool2.impl.GenericObjectPool;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "StatsServlet", value = "/stats/*")
public class StatsServlet extends HttpServlet {
    private static BasicDataSource dataSource;
    @Override
    public void init() throws ServletException {
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
        // urlPath = "/stats/{userID}", urlParts = [, stats, userID]
        if (urlParts.length != 2) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            res.getWriter().write("Invalid url input: not status");
            return;
        }

        String swiperID = urlParts[1];
        try {
            String likesAndDislikes = "";
            likesAndDislikes = getStats(swiperID);
            System.out.println(likesAndDislikes);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getStats(String swiper) throws SQLException {
        StatsDao statsDao = new StatsDao();
        return statsDao.getStats(swiper);
    }
}
