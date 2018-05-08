package client;


import org.json.simple.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.TreeMap;


/**
 * Create events
 */
public class PrintJPG extends BaseServlet {
    private FileMap fm;
    private Map<String, TreeMap<Integer, byte[]>> filemap;
    private TreeMap<Integer, byte[]> filePieces;

    public PrintJPG(FileMap fm) {
        this.fm = fm;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String filename = request.getParameter("filename");
        System.out.println(filename);
        int piecenum = fm.getPieceNum(filename);
        byte[] imageBytes = fm.getFile(filename, piecenum);
        response.setHeader("Content-Type", "image/jpg");// or png or gif, etc
        response.setHeader("Content-Length", String.valueOf(imageBytes.length));
        response.getOutputStream().write(imageBytes);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

    }

}
