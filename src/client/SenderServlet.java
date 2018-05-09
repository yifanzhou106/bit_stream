package client;


import org.json.simple.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.sql.Array;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;


/**
 * Create events
 */
public class SenderServlet extends BaseServlet {
    private FileMap fm;
    private Map<String, TreeMap<Integer, byte[]>> filemap;
    private TreeMap<Integer, byte[]> filePieces;

    public SenderServlet(FileMap fm) {
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
            String body = extractPostRequestBody(request);
            JSONObject jsonobj = readJsonObj(body);

            String pieceid = (String) jsonobj.get("pieceid");
            String filename = (String) jsonobj.get("filename");

            byte[] piece = fm.getPiece(filename, Integer.parseInt(pieceid));
            response.getOutputStream().write(piece);

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
        }

    }

}
