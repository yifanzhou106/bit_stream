package client;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.concurrent.ExecutorService;

import static client.BitTorrentClient.*;

public class Downloader extends BaseServlet implements Runnable {
    private ExecutorService threads;
    private String filename;

    public Downloader(ExecutorService threads, String filename) {
        this.threads = threads;
        this.filename = filename;
    }

    @Override
    public void run() {
        try {
            /**
             * Send self info and receive nodes and file detail info
             */
            String responseS;
            String url = "http://" + TRACKER_HOST + ":" + TRACKER_PORT + "/create";
            System.out.println(url);
            String s;
            JSONObject obj = new JSONObject();
            JSONObject item = new JSONObject();
            item.put("host", HOST);
            item.put("port", String.valueOf(PORT));
            item.put("filename", filename);

            obj.put("request", item);
            s = obj.toString();
            responseS = sendPost(url, s);
            System.out.println(responseS);

            JSONObject jsonobj = readJsonObj(responseS);
            JSONObject fileinfo = (JSONObject) jsonobj.get("fileinfo");
            JSONArray nodes = (JSONArray) jsonobj.get("nodes");
            int size = Integer.parseInt((String) fileinfo.get("size"));
            int piecenum = Integer.parseInt((String) fileinfo.get("piecenum"));
            byte[] image = new byte[size];

            for (int i = 0; i < piecenum; i++) {
                JSONObject singlepiece = (JSONObject) nodes.get(i);
                singlepiece.put("filename", filename);
                url = "http://" + HOST + ":" + PORT + "/receive";
                try {
                    sendPostResponse(url, singlepiece.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
