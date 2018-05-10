package client;


import org.json.simple.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.TreeMap;

import static client.BitTorrentClient.*;


/**
 * Create connection with a node and receive a single piece
 * If a node fails, then ask tracker for a new node
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
        PrintWriter out = response.getWriter();
        String body = extractPostRequestBody(request);

        try {
            while (true) {
                JSONObject jsonobj = readJsonObj(body);
                String host = (String) jsonobj.get("host");
                String port = (String) jsonobj.get("port");
                String pieceid = (String) jsonobj.get("pieceid");
                String filename = (String) jsonobj.get("filename");

                byte[] responseS;
                String url = "http://" + host + ":" + port + "/seed";
                String s;
                JSONObject obj = new JSONObject();
                obj.put("filename", filename);
                obj.put("pieceid", pieceid);

                s = obj.toString();
                try {
                    responseS = getBytePiece(url, s);
                } catch (Exception e) {

                    /**
                     * Ask and receive a new node, then update local variables
                     */
                    System.out.println("\nCan not connect to: " + host + port);
                    System.out.println("Resend request");
                    url = "http://" + TRACKER_HOST + ":" + TRACKER_PORT + "/remove";
                    obj.put("filename", filename);
                    obj.put("pieceid", pieceid);
                    obj.put("host", host);
                    obj.put("port", port);
                    body = sendPost(url, obj.toString());
                    continue;
                }
//            System.out.println("Piece Id is "+ pieceid + "Item: " + Arrays.toString(responseS));
                fm.addFile(filename, Integer.parseInt(pieceid), responseS);

                url = "http://" + TRACKER_HOST + ":" + TRACKER_PORT + "/addpiece";
                obj.put("filename", filename);
                obj.put("pieceid", pieceid);
                obj.put("host", HOST);
                obj.put("port", String.valueOf(PORT));
                sendPost(url, obj.toString());

                out.println();
                break;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
