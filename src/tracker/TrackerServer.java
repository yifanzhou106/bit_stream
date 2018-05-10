package tracker;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;


/**
 * Project 5 - BT
 * Tracker server
 *
 * @Author Yifan Zhou
 */
public class TrackerServer extends BaseServlet {
    public static String HOST = "localhost";
    public static int PORT = 7600;

    private TrackerMap tm;

    public TrackerServer() {
        tm = new TrackerMap();
    }

    public static void main(String[] args) {
        TrackerServer ts = new TrackerServer();

        if (args.length > 0) {
            if (args[0].equals("-localhost")) {
                HOST = args[1];
                System.out.println(HOST);
            }
            if (args[2].equals("-localport")) {
                PORT = Integer.parseInt(args[3]);
                System.out.println(PORT);
            }
        }
        Server server = new Server(PORT);

        ServletHandler handler = new ServletHandler();
        server.setHandler(handler);

        handler.addServletWithMapping(new ServletHolder(new CreateInfoServlet(ts.tm)), "/create");
        handler.addServletWithMapping(new ServletHolder(new DownloadServlet(ts.tm)), "/download");
        handler.addServletWithMapping(new ServletHolder(new RemoveNodeServlet(ts.tm)), "/remove");
        handler.addServletWithMapping(new ServletHolder(new AddPieceServlet(ts.tm)), "/addpiece");


        server.setHandler(handler);


        System.out.println("Starting server on port " + PORT + "...");

        try {
            System.out.println("Tracker port: " + PORT);
            server.start();
            server.join();

            System.out.println("Exiting...");
        } catch (Exception ex) {
            System.exit(-1);
        }
    }


}