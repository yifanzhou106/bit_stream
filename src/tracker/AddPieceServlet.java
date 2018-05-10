package tracker;


import org.json.simple.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * If a piece is downloaded successfully, a request will be sent here to update map
 */
public class AddPieceServlet extends BaseServlet {
    private TrackerMap tm;

    public AddPieceServlet(TrackerMap tm) {
        this.tm = tm;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setStatus(400);

    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        PrintWriter out = response.getWriter();
        String host, port, filename;
        int pieceid;
        try {
            String body = extractPostRequestBody(request);
            System.out.println(body);
            JSONObject obj = readJsonObj(body);

            host = (String) obj.get("host");
            port = (String) obj.get("port");
            filename = (String) obj.get("filename");
            pieceid = Integer.parseInt((String) obj.get("pieceid"));
            System.out.println("\nNode: " + host + port + " finished file " + filename + " piece #" + pieceid);
            tm.updateFile(filename, host + port, pieceid);
            out.println(obj.toString());
        } catch (Exception e) {
            response.setStatus(400);
            e.printStackTrace();
        }
    }
}
