package client;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.ByteArrayOutputStream;
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
            if (!filemap.containsKey(filename)) {
                filePieces = new TreeMap<>();
                filePieces.put(pieceid, pieces);
            } else {
                filePieces = filemap.get(filename);
                filePieces.put(pieceid, pieces);
                filemap.remove(filename);
            }
            filemap.put(filename, filePieces);

        } finally {
            filemaplock.writeLock().unlock();
        }
    }

    public byte[] getPiece(String filename, int pieceid) {
        filemaplock.readLock().lock();
        try {
            byte[] piece;
            filePieces = filemap.get(filename);
            piece = filePieces.get(pieceid);
            return piece;

        } finally {
            filemaplock.readLock().unlock();
        }
    }

    public byte[] getFile(String filename, int piecenum) {
        filemaplock.readLock().lock();
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            filePieces = filemap.get(filename);
            for (int i = 0; i < piecenum; i++)
                output.write(filePieces.get(i));

            return output.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;

        } finally {
            filemaplock.readLock().unlock();
        }
    }

    public JSONObject fileinfoJson(String filename, String size, String piecenum) {

        JSONObject fileinfo = new JSONObject();
        fileinfo.put("filename", filename);
        fileinfo.put("size", size);
        fileinfo.put("piecenum", piecenum);

        return fileinfo;

    }

}
