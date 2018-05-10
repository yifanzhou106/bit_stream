package tracker;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Several maps to store node and file info
 */
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
        fileinfolock = new ReentrantReadWriteLock();
        filemaplock = new ReentrantReadWriteLock();
        nodelock = new ReentrantReadWriteLock();
        filemap = new HashMap<>();
    }

    /**
     * Add a node
     *
     * @param host
     * @param port
     */
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
            nodelock.writeLock().unlock();
        }
    }

    /**
     * Add a new file info
     *
     * @param filename
     * @param pieceNum
     * @param size
     */
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

    /**
     * For a new seed, its piecelist will automatically be updated to full
     *
     * @param filename
     * @param pieceNum
     * @param nodekey
     * @param size
     */
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

    /**
     * Add a single pieceid into list
     *
     * @param filename
     * @param nodekey
     * @param finishedPiece
     */
    public void updateFile(String filename, String nodekey, int finishedPiece) {
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
            System.out.println(filemap);

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

    public Integer getFileSize(String filename) {
        fileinfolock.readLock().lock();
        try {
            singleFileinfo = fileinfo.get(filename);
            return singleFileinfo.get("size");
        } finally {
            fileinfolock.readLock().unlock();
        }
    }

    /**
     * A simple algorithm to select node to create a complete file
     *
     * @param filename
     * @param piecenum
     * @return
     */
    public JSONArray nodeJsonArray(String filename, int piecenum) {
        nodelock.readLock().lock();
        filemaplock.readLock().lock();
        try {
            int count = 0;
            String nodekey, host, port;
            JSONArray array = new JSONArray();
            JSONObject nodeinfo;
            fileNodeDetail = filemap.get(filename);
            while (count != piecenum) {
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
            }
            return array;
        } finally {
            nodelock.readLock().unlock();
            filemaplock.readLock().unlock();
        }
    }

    /**
     * Remove node from map
     *
     * @param host
     * @param port
     */
    public void removeNode(String host, String port) {
        nodelock.writeLock().lock();
        filemaplock.writeLock().lock();
        try {
            String key = host + port;
            String filename;
            if (nodeMap.containsKey(key)) {
                nodeMap.remove(key);
                for (Map.Entry<String, HashMap<String, TreeSet<Integer>>> entry : filemap.entrySet()) {
                    filename = entry.getKey();
                    fileNodeDetail = entry.getValue();
                    if (fileNodeDetail.containsKey(key)) ;
                    {
                        fileNodeDetail.remove(key);
                        filemap.remove(filename);
                        filemap.put(filename, fileNodeDetail);
                    }

                }
            }

        } finally {
            nodelock.writeLock().unlock();
            filemaplock.writeLock().unlock();
        }
    }

    /**
     * If a node fails, get a new node here
     *
     * @param filename
     * @param pieceid
     * @return
     */
    public JSONObject getSinglePiece(String filename, int pieceid) {
        nodelock.readLock().lock();
        filemaplock.readLock().lock();
        try {
            String nodekey, host, port;
            JSONObject nodeinfo = new JSONObject();
            fileNodeDetail = filemap.get(filename);
            for (HashMap.Entry<String, TreeSet<Integer>> entry : fileNodeDetail.entrySet()) {
                piecelist = entry.getValue();
                nodekey = entry.getKey();
                if (piecelist.contains(pieceid)) {
                    singleNodeMap = nodeMap.get(nodekey);
                    host = singleNodeMap.get("host");
                    port = singleNodeMap.get("port");
                    nodeinfo.put("host", host);
                    nodeinfo.put("port", port);
                    nodeinfo.put("filename", filename);
                    nodeinfo.put("pieceid", String.valueOf(pieceid));

                    return nodeinfo;
                }
            }
            return nodeinfo;
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
