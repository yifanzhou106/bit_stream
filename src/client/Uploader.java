package client;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;

import static client.BitTorrentClient.*;

public class Uploader extends BaseServlet implements Runnable {
    private ExecutorService threads;
    private String filelocation;
    final private int FIXED_PIECE_SIZE = 256;
    private FileMap fm;


    public Uploader(ExecutorService threads, FileMap fm, String filelocation) {
        this.threads = threads;
        this.filelocation = filelocation;
        this.fm = fm;
    }

    @Override
    public void run() {
        try {
            String item = "As a globally-distributed database, Spanner provides several interesting features. First, the replication configurations for data can be dynamically controlled at a fine grain by applications Second, Spanner has two features that are difficult to implement in a distributed database: it provides externally consistent reads and writes, and globally-consistent reads across the database at a timestamp. These features enable Spanner to support consistent backups, consistent MapReduce executions, and atomic schema updates, all at global scale, and even in the presence of ongoing transactions.";
            String filename = "file1";
            byte[] byteItem = item.getBytes(Charset.forName("UTF-8"));
            int blockcount = (byteItem.length) / FIXED_PIECE_SIZE;
            byte[] piece;
            System.out.println(byteItem.length);
            for (int i = 0; i < blockcount + 1; i++) {
                if (i == blockcount) {
                    piece = Arrays.copyOfRange(byteItem, i * FIXED_PIECE_SIZE, byteItem.length);
                } else {
                    piece = Arrays.copyOfRange(byteItem, i * FIXED_PIECE_SIZE, (i + 1) * FIXED_PIECE_SIZE);
                }
                fm.addFile(filename, i, piece);
            }
//            byte[] byteValue = fm.getFile(filename, blockcount + 1);
//            String string = new String(byteValue);
//            System.out.println(string);

            JSONObject obj = new JSONObject();
            JSONObject node = new JSONObject();
            JSONObject file = new JSONObject();

            node.put("host", HOST);
            node.put("port", String.valueOf(PORT));
            obj.put("node", node);

            file.put("filename", filename);
            file.put("piecenum", String.valueOf(blockcount+1));
            file.put("size", String.valueOf(byteItem.length));
            obj.put("file",file);
            String url = "http://" + TRACKER_HOST + ":" + TRACKER_PORT + "/create";
            sendPost(url,obj.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
