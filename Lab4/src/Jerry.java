import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Jerry {
    public static String ReadHTML(String htmlFilename) {
        StringBuilder contentBuilder = new StringBuilder();
        try {
            BufferedReader in = new BufferedReader(new FileReader(htmlFilename));
            String str;
            while ((str = in.readLine()) != null) {
                contentBuilder.append(str);
            }
            in.close();
        } catch (IOException e) {
        }
        String content = contentBuilder.toString();
        System.out.println(content);
        return content;
    }

    public static void main(String args[]) {
        Socket clientSocket = null;
        try {
            int serverPort = 7777; // the server port we are using

            // Create a new server socket
            ServerSocket listenSocket = new ServerSocket(serverPort);

            while (true) {
                /*
                 * Block waiting for a new connection request from a client.
                 * When the request is received, "accept" it, and the rest
                 * the tcp protocol handshake will then take place, making
                 * the socket ready for reading and writing.
                 */
                clientSocket = listenSocket.accept();
                // If we get here, then we are now connected to a client.

                // Set up "inFromSocket" to read from the client socket
                Scanner inFromSocket;
                inFromSocket = new Scanner(clientSocket.getInputStream());

                // Set up "outToSocket" to write to the client socket
                PrintWriter outToSocket;
                outToSocket = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));

                String filePath;
                String fileName = null;

                /*
                 * Forever,
                 *   read a line from the socket
                 *   print it to the console
                 *   echo it (i.e. write it) back to the client
                 */
//            int count = 0;
                while (true) {
                    if (inFromSocket.hasNext()) {
                        String data = inFromSocket.nextLine();
                        filePath = "";
                        if (data.contains("GET")) {
                            filePath = data;
                        }

                        if (filePath.contains(" ")){
                            fileName = filePath.split(" ")[1].substring(1);
                            filePath = "/Users/linhungfan/Desktop/CMU/Semester 2/Distributed System/Lab4/" + fileName;
                            System.out.println("== File Path " + filePath);


                        }

//                        String htmlContent = ReadHTML(filePath);

                        if (new File(fileName).exists()) {
                            outToSocket.println("HTTP/1.1 200 OK");

                            try {
                                BufferedReader in = new BufferedReader(new FileReader(fileName));
                                String str;
                                while ((str = in.readLine()) != null) {
                                    outToSocket.println(str);
                                    outToSocket.flush();
                                }
                                in.close();
                            } catch (IOException e) {
                            }
                            System.out.println("HTML Read complete");
                            outToSocket.close();

                        } else {
                            outToSocket.println("HTTP/1.1 404 File Not Found\n\n");
                            outToSocket.flush();
                            outToSocket.close();
                        }
//                        System.out.println("Echoing: " + data);
                    } else {
                        break;
                    }
                }
            }
            // Handle exceptions
        } catch (IOException e) {
            System.out.println("IO Exception:" + e.getMessage());

            // If quitting (typically by you sending quit signal) clean up sockets
        } finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                // ignore exception on close
            }
        }
    }
}
