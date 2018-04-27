package tracker;


import org.json.simple.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * Create event, send info to event server
 */
public class CreateInfoServlet extends BaseServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setStatus(400);

    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        PrintWriter out = response.getWriter();

        try {
            String body = extractPostRequestBody(request);
            JSONObject obj = readJsonObj(body);
            long userid = (Long) obj.get("userid");
            String eventname = (String) obj.get("eventname");
            long numtickets = (Long) obj.get("numtickets");


            out.println();
        } catch (Exception e) {
            response.setStatus(400);
            e.printStackTrace();
        }
    }
}
