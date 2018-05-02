package tracker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Project 5 - BT
 *
 * @Author Yifan Zhou
 */
public class TrackerServer extends BaseServlet{
    protected static Logger log = LogManager.getLogger();
    public static String HOST = "localhost";
    public static int PORT = 7600;
    public static String EVENT_PORT = "7000";
    public static  String EVENT_HOST = "localhost";
    static int USER_PORT = 2000;
    static String USER_HOST = "mc01";

    private TrackerMap tm;

    public TrackerServer() {
        tm = new TrackerMap();
    }
    public static void main(String[] args) {
        TrackerServer ts = new TrackerServer();

//        if (args.length > 0) {
//            if (args[0].equals("-localhost")) {
//                HOST = args[1];
//                System.out.println(HOST);
//            }
//            if (args[2].equals("-localport")) {
//                PORT = Integer.parseInt(args[3]);
//                System.out.println(PORT);
//            }
//            if (args[4].equals("-primaryhost")) {
//                EVENT_HOST = args[5];
//                System.out.println(EVENT_HOST);
//            }
//            if (args[6].equals("-primaryport")) {
//                EVENT_PORT = args[7];
//                System.out.println(EVENT_PORT);
//            }
//        }
        Server server = new Server(PORT);

        ServletHandler handler = new ServletHandler();
        server.setHandler(handler);

        handler.addServletWithMapping(new ServletHolder(new CreateInfoServlet(ts.tm)), "/create");
        handler.addServletWithMapping(new ServletHolder(new DownloadServlet(ts.tm)), "/download");
        handler.addServletWithMapping(new ServletHolder(new RemoveNodeServlet(ts.tm)), "/remove");
        handler.addServletWithMapping(new ServletHolder(new AddPieceServlet(ts.tm)), "/addpiece");


        server.setHandler(handler);


        log.info("Starting server on port " + PORT + "...");

        try {
            server.start();
            server.join();

            log.info("Exiting...");
        } catch (Exception ex) {
            log.fatal("Interrupted while running server.", ex);
            System.exit(-1);
        }
    }



}