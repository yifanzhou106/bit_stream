package client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Project 5 - BT
 *
 * @Author Yifan Zhou
 */
public class BitTorrentClient extends BaseServlet {
    public static String HOST = "localhost";
    public static int PORT = 6000;
    public static String TRACKER_PORT = "7600";
    public static String TRACKER_HOST = "localhost";
    public static boolean isDebug = false;
    public static volatile boolean isShutdown = false;
    public final ExecutorService threads = Executors.newCachedThreadPool();

    private UI ui;
    private FileMap fm;

    public BitTorrentClient() {
        fm = new FileMap();
        ui = new UI(threads,fm);
        threads.submit(ui);
    }


    public static void main(String[] args) {
        BitTorrentClient bt = new BitTorrentClient();

//        if (args.length > 0) {
//            if (args[0].equals("-localhost")) {
//                HOST = args[1];
//                System.out.println(HOST);
//            }
//            if (args[2].equals("-localport")) {
//                PORT = Integer.parseInt(args[3]);
//                System.out.println(PORT);
//            }
//            if (args[4].equals("-trackerhost")) {
//                EVENT_HOST = args[5];
//                System.out.println(EVENT_HOST);
//            }
//            if (args[6].equals("-trackerport")) {
//                EVENT_PORT = args[7];
//                System.out.println(EVENT_PORT);
//            }
//            if (args.length>8){
//                if (args[8].equals("-debug"))
//                    isDebug = true;
//            }
//        }
        Server server = new Server(PORT);

        ServletHandler handler = new ServletHandler();
        server.setHandler(handler);

        handler.addServletWithMapping(new ServletHolder(new ReceiverServlet(bt.fm)), "/receive");
        handler.addServletWithMapping(new ServletHolder(new SenderServlet(bt.fm)), "/seed");
        handler.addServletWithMapping(new ServletHolder(new PrintJPG(bt.fm)), "/image/jpg");


        server.setHandler(handler);


        try {
            System.out.println("Client port: " + PORT);
            server.start();
            server.join();

        } catch (Exception ex) {
            System.exit(-1);
        }
    }


}