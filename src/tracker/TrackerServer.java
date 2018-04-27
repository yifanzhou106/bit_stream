package tracker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
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
    public static int PORT = 5600;
    public static String EVENT_PORT = "7000";
    public static  String EVENT_HOST = "localhost";
    static int USER_PORT = 2000;
    static String USER_HOST = "mc01";


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
        ServletContextHandler context = new ServletContextHandler();

        context.addServlet(CreateInfoServlet.class, "/create");
//        context.addServlet(UserServlet.class, "/users/*");
//        context.addServlet(EventServlet.class, "/events");
//        context.addServlet(CreateEventServlet.class, "/events/create");
//        context.addServlet(EventServlet.class, "/events/*");
//        context.addServlet(UpdatePrimaryNodeServlet.class, "/nodes");



        server.setHandler(context);

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