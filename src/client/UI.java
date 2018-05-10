package client;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;

import static client.BitTorrentClient.isShutdown;

/**
 * UI: handle with user input
 */
public class UI implements Runnable {

    private ExecutorService threads;
    private FileMap fm;


    public UI(ExecutorService threads, FileMap fm) {
        this.threads = threads;
        this.fm = fm;
    }

    @Override
    public void run() {
        while (!isShutdown) {
            Scanner reader = new Scanner(System.in);
            System.out.println("Enter your choices (Enter \"help\" for help): ");
            String userChoice = reader.nextLine();
            String[] splitedUserChoice = userChoice.split(" ");


            switch (splitedUserChoice[0]) {
                case "help":
                    System.out.println("\n**************************");
                    System.out.println("\n1. download\n");
                    System.out.println("\n2. upload\n");
                    System.out.println("\n**************************");
                    break;

                case "upload":
                    System.out.println("File Location? ");
                    String filelocation = reader.nextLine();
                    threads.submit(new Uploader(threads, fm, filelocation));

                    break;

                case "download":
                    System.out.println("File name? ");
                    String filename = reader.nextLine();
                    threads.submit(new Downloader(threads, fm, filename));
                    break;

                case "exit":
                    isShutdown = true;
                    threads.shutdownNow();
                    System.exit(0);
                    break;

                default:
                    System.out.println("\nWrong Input\n");
                    break;
            }
        }
    }
}




