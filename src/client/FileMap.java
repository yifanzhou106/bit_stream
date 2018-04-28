package client;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class FileMap {

    private Map<String, TreeMap<Integer, byte[]>> filemap;
    private TreeMap<Integer, byte[]> filePieces;

    private ReentrantReadWriteLock filemaplock;

    public FileMap() {
        filemaplock = new ReentrantReadWriteLock();
        filemap = new HashMap<>();
    }


    public void addFile(String filename, int pieceid, byte[] pieces) {
        filemaplock.writeLock().lock();
        try {
            if (!filemap.containsKey(filename)){
                filePieces = new TreeMap<>();
                filePieces.put(pieceid,pieces);
                filemap.put(filename,filePieces);
            }
            else {
                filePieces = filemap.get(filename);
                filePieces.put(pieceid,pieces);
                filemap.remove(filename);
                filemap.put(filename,filePieces);
            }
        } finally {
            filemaplock.writeLock().unlock();
        }
    }


    public JSONObject nodeJson(String host, String port, String piece) {

        JSONObject node = new JSONObject();
        node.put("host", host);
        node.put("port", port);
        node.put("piece", piece);

        return node;

    }

    public JSONObject fileinfoJson(String filename, String size, String piecenum) {

        JSONObject fileinfo = new JSONObject();
        fileinfo.put("filename", filename);
        fileinfo.put("size", size);
        fileinfo.put("piecenum", piecenum);

        return fileinfo;

    }

}
