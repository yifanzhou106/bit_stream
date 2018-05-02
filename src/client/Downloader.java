package client;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import static client.BitTorrentClient.*;

public class Downloader extends BaseServlet implements Runnable {
    private ExecutorService threads;
    private String filename;
    private FileMap fm;
    private CountDownLatch countdowntimer;


    public Downloader(ExecutorService threads, FileMap fm, String filename) {
        this.threads = threads;
        this.fm = fm;
        this.filename = filename;
    }

    @Override
    public void run() {
        try {
            /**
             * Send self info and receive nodes and file detail info
             */
            String responseS;
            String url = "http://" + TRACKER_HOST + ":" + TRACKER_PORT + "/download";
            String s;
            JSONObject obj = new JSONObject();
            obj.put("host", HOST);
            obj.put("port", String.valueOf(PORT));
            obj.put("filename", filename);

            s = obj.toString();
            responseS = sendPost(url, s);
            System.out.println(responseS);

            JSONObject jsonobj = readJsonObj(responseS);
            JSONObject fileinfo = (JSONObject) jsonobj.get("fileinfo");
            JSONArray nodes = (JSONArray) jsonobj.get("nodes");
            int size = Integer.parseInt((String) fileinfo.get("size"));
            int piecenum = Integer.parseInt((String) fileinfo.get("piecenum"));
            countdowntimer = new CountDownLatch(piecenum);
            for (int i = 0; i < piecenum; i++) {
                JSONObject singlepiece = (JSONObject) nodes.get(i);
                singlepiece.put("filename", filename);
                url = "http://" + HOST + ":" + PORT + "/receive";
                threads.submit(new sendReceive(url, singlepiece.toString(), countdowntimer));
//                sendPostResponse(url, singlepiece.toString());
            }
            countdowntimer.await();
            byte[] byteValue = fm.getFile(filename, piecenum);
            String string = new String(byteValue);
            System.out.println(string);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
