package tracker;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * If a client wants to download a file, its json will be sent here.
 * Tracker will store its node info and send it file info and a node list
 */
public class DownloadServlet extends BaseServlet {
    private TrackerMap tm;

    public DownloadServlet(TrackerMap tm) {
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
            JSONObject obj = readJsonObj(body);
            System.out.println(obj.toString());
            host = (String) obj.get("host");
            port = (String) obj.get("port");
            filename = (String) obj.get("filename");

            tm.addnode(host, port);
            piecenum = tm.getPieceNum(filename);
            size = tm.getFileSize(filename);

            JSONArray array = tm.nodeJsonArray(filename, piecenum);

            obj = new JSONObject();
            JSONObject fileinfo = new JSONObject();
            JSONObject nodes = new JSONObject();

            fileinfo.put("piecenum", String.valueOf(piecenum));
            fileinfo.put("size", String.valueOf(size));
            obj.put("fileinfo", fileinfo);
            obj.put("nodes", array);

            out.println(obj.toString());
        } catch (Exception e) {
            response.setStatus(400);
            e.printStackTrace();
        }
    }
}
