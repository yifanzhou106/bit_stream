package client;


import org.json.simple.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Create events
 */
public class ReceiverServlet extends BaseServlet {
    private FileMap fm;
    private Map<String, TreeMap<Integer, byte[]>> filemap;
    private TreeMap<Integer, byte[]> filePieces;

    public ReceiverServlet(FileMap fm) {
        this.fm = fm;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setStatus(400);

    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            PrintWriter out = response.getWriter();
            String body = extractPostRequestBody(request);
            JSONObject jsonobj = readJsonObj(body);
            String host = (String)jsonobj.get("host");
            String port = (String)jsonobj.get("port");
            String pieceid = (String)jsonobj.get("pieceid");
            String filename = (String)jsonobj.get("filename");

            byte[] responseS;
            String url = "http://" + host + ":" + port + "/seed";
            System.out.println(url);
            String s;
            JSONObject obj = new JSONObject();
            JSONObject item = new JSONObject();
            item.put("filename", filename);
            item.put("pieceid", pieceid);

            obj.put("request", item);
            s = obj.toString();
            responseS = getBytePiece(url, s);

            fm.addFile(filename,Integer.parseInt(pieceid),responseS);
            System.out.println(responseS);


            out.println(body);


        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
        }

    }

}
