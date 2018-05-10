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

            /**
             * Read json string and begin to communicate with provided nodes
             */
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

            /**
             * Receive all pieces and begin to combine all pieces together
             */
            System.out.println("Download Finished");
            byte[] byteValue = fm.getFile(filename, piecenum);

            if (isDebug) {
                String string = new String(byteValue);
                System.out.println(string);
            } else {
                String newFileName = getNewFileName(filename);
                System.out.println(newFileName);
                String[] suffix = filename.split("\\.");
                if (suffix[1].equals("jpg"))
                    storeImage(newFileName, byteValue);
                else
                    storeVideo(newFileName, byteValue);

                System.out.println("Store " + newFileName + " successfully");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
