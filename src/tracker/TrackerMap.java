package tracker;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TrackerMap {
    private Map<String, HashMap<String, Integer>> fileinfo;
    private HashMap<String, Integer> singleFileinfo;

    private Map<String, HashMap<String, TreeSet<Integer>>> filemap;
    private HashMap<String, TreeSet<Integer>> fileNodeDetail;
    private TreeSet<Integer> piecelist;
    private Map<String, HashMap<String, String>> nodeMap;
    private HashMap<String, String> singleNodeMap;

    private ReentrantReadWriteLock fileinfolock;
    private ReentrantReadWriteLock filemaplock;
    private ReentrantReadWriteLock nodelock;

    public TrackerMap() {
        fileinfo = new HashMap<>();
        nodeMap = new HashMap<>();
        filemaplock = new ReentrantReadWriteLock();
        nodelock = new ReentrantReadWriteLock();
        filemap = new HashMap<>();
    }

    public void addnode(String host, String port) {
        nodelock.writeLock().lock();
        try {
            if (!nodeMap.containsKey(host + port)) {
                singleNodeMap = new HashMap<>();
                singleNodeMap.put("host", host);
                singleNodeMap.put("port", port);
                nodeMap.put(host + port, singleNodeMap);
            }
        } finally {
            nodelock.writeLock().lock();
        }
    }

    public void addNewFileInfo(String filename, int pieceNum, int size) {
        fileinfolock.writeLock().lock();
        try {
            singleFileinfo = new HashMap<>();
            singleFileinfo.put("piecenum", pieceNum);
            singleFileinfo.put("size", size);

            fileinfo.put(filename, singleFileinfo);
        } finally {
            fileinfolock.writeLock().unlock();
        }
    }

    public void addNewFile(String filename, int pieceNum, String nodekey, int size) {
        filemaplock.writeLock().lock();
        try {
            fileNodeDetail = new HashMap<>();
            piecelist = new TreeSet<>();

            for (int i = 0; i < pieceNum; i++)
                piecelist.add(i);

            fileNodeDetail.put(nodekey, piecelist);
            filemap.put(filename, fileNodeDetail);
            addNewFileInfo(filename, pieceNum, size);
        } finally {
            filemaplock.writeLock().unlock();
        }
    }

    public void updateFile(String filename, int finishedPiece, String nodekey) {
        filemaplock.writeLock().lock();
        try {
            fileNodeDetail = filemap.get(filename);
            if (fileNodeDetail.containsKey(nodekey)) {
                piecelist = fileNodeDetail.get(nodekey);
                piecelist.add(finishedPiece);
                fileNodeDetail.remove(nodekey);

            } else {
                piecelist = new TreeSet<>();
                piecelist.add(finishedPiece);
            }

            fileNodeDetail.put(nodekey, piecelist);
            filemap.remove(filename);
            filemap.put(filename, fileNodeDetail);

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

    public Integer getPieceNum(String filename) {
        fileinfolock.readLock().lock();
        try {
            singleFileinfo = fileinfo.get(filename);
            return singleFileinfo.get("piecenum");
        } finally {
            fileinfolock.readLock().unlock();
        }
    }

    public JSONArray nodeJsonArray(String filename, int piecenum) {
        nodelock.readLock().lock();
        filemaplock.readLock().lock();
        try {
            int count = 0;
            String nodekey, host, port;
            JSONArray array = new JSONArray();
            JSONObject nodeinfo;
            fileNodeDetail = filemap.get(filename);
            while (count != (piecenum - 1))
                for (HashMap.Entry<String, TreeSet<Integer>> entry : fileNodeDetail.entrySet()) {
                    piecelist = entry.getValue();
                    nodekey = entry.getKey();
                    if (piecelist.contains(count)) {
                        nodeinfo = new JSONObject();
                        singleNodeMap = nodeMap.get(nodekey);
                        host = singleNodeMap.get("host");
                        port = singleNodeMap.get("port");
                        nodeinfo.put("host", host);
                        nodeinfo.put("port", port);
                        nodeinfo.put("pieceid", String.valueOf(count));
                        array.add(nodeinfo);
                        count++;
                    }
                }
            return array;
        } finally {
            nodelock.readLock().unlock();
            filemaplock.readLock().unlock();
        }
    }

    public JSONObject getWholeJson(String filename) {
        fileinfolock.readLock().lock();

        try {
            JSONObject obj = new JSONObject();
            JSONObject item;
            JSONArray itemArray;
            singleFileinfo = fileinfo.get(filename);
            int piecenum = singleFileinfo.get("piecenum");
            int size = singleFileinfo.get("size");
            item = fileinfoJson(filename, String.valueOf(size), String.valueOf(piecenum));
            obj.put("fileinfo", item);
            itemArray = nodeJsonArray(filename, piecenum);
            obj.put("nodes", itemArray);

            return obj;
        } finally {
            fileinfolock.readLock().unlock();

        }
    }

}
