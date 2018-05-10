package tracker;


import org.json.simple.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * If a client cannot connect to a node, it will send a request here
 * to remove the invalid node and request a new node
 */
public class RemoveNodeServlet extends BaseServlet {
    private TrackerMap tm;

    public RemoveNodeServlet(TrackerMap tm) {
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
//            System.out.println(body);
            JSONObject obj = readJsonObj(body);

            host = (String) obj.get("host");
            port = (String) obj.get("port");

            filename = (String) obj.get("filename");
            pieceid = Integer.parseInt((String) obj.get("pieceid"));

            tm.removeNode(host, port);
            System.out.println("\nRemove node " + host + port);
            obj = tm.getSinglePiece(filename, pieceid);
            System.out.println("Provide a new node" + obj.toString());
            out.println(obj.toString());
        } catch (Exception e) {
            response.setStatus(400);
            e.printStackTrace();
        }
    }
}
