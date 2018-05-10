package tracker;


import org.json.simple.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * If a client upload file, its json will be sent here.
 * Tracker will store its node info and file info
 */
public class CreateInfoServlet extends BaseServlet {
    private TrackerMap tm;

    public CreateInfoServlet(TrackerMap tm) {
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
        int piecenum, size;
        try {
            String body = extractPostRequestBody(request);
            System.out.println(body);
            JSONObject obj = readJsonObj(body);

            JSONObject node = (JSONObject) obj.get("node");
            JSONObject file = (JSONObject) obj.get("file");

            host = (String) node.get("host");
            port = (String) node.get("port");

            filename = (String) file.get("filename");
            piecenum = Integer.parseInt((String) file.get("piecenum"));
            size = Integer.parseInt((String) file.get("size"));
            tm.addnode(host, port);
            tm.addNewFile(filename, piecenum, host + port, size);

            out.println();
        } catch (Exception e) {
            response.setStatus(400);
            e.printStackTrace();
        }
    }
}
