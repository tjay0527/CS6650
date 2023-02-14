import com.google.gson.Gson;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

//@WebServlet(name = "TwinderServlet", value = "/swipe/*")
@WebServlet(name = "TwinderServlet", value = "/TwinderServlet/*")
public class TwinderServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        String urlPath = req.getPathInfo();

        // check we have a URL
        if (urlPath == null || urlPath.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            res.getWriter().write("missing paramterers");
            return;
        }

        String[] urlParts = urlPath.split("/");
        // urlPath  = "/swipe/left"
        // urlParts = [, swipe, left]
        if (urlParts.length != 3 || !(urlParts[2].equals("left") || urlParts[2].equals("right"))) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            res.getWriter().write("Valid url input: left or right");
            return;
        }

        Gson gson = new Gson();
        StringBuilder sb = new StringBuilder();
        String s;
        while ((s = req.getReader().readLine()) != null) {
            sb.append(s);
        }
        Swipe swipe = (Swipe) gson.fromJson(sb.toString(), Swipe.class);

        try {
            boolean invalidInPut = false;
            //if the swiper id is not within [1,5000]
            if(Integer.parseInt(swipe.getSwiper()) < 1 || 5000 < Integer.parseInt(swipe.getSwiper())){
                invalidInPut = true;
                res.getWriter().write("The swiper id is not within 1, 5000");
            }
            //if the swipee id is not within [1,1000000]
            if(Integer.parseInt(swipe.getSwipee()) < 1 || 1000000 < Integer.parseInt(swipe.getSwipee())){
                invalidInPut = true;
                res.getWriter().write("The swipee id is not within 1, 1000000");
            }
            //if the length of the string is less than or equal to 256
            if(swipe.getComment() == null || swipe.getComment().length() > 256){
                invalidInPut = true;
                res.getWriter().write("The length of the comment is longer than 256");
            }
            //if the input is invalid
            if(invalidInPut){
                res.setStatus((HttpServletResponse.SC_NOT_FOUND));
                res.getWriter().flush();
                return;
            }
        } catch (NumberFormatException e) {
            //if the body is null or id contains characters
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            res.getWriter().write("Invalid input");
            res.getWriter().flush();
            return;
        }
        res.setStatus(HttpServletResponse.SC_OK);
        res.getWriter().write(sb.toString());
        res.getWriter().flush();
    }
}
