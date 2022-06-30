import java.util.*;
import java.io.*;
import java.net.*;

public class SimpleChatServer {

  ArrayList<PrintWriter> clientOutputStreams;

  public static void main(String[] args) {
    new SimpleChatServer().go();
  }

  public void go(){
    try {
      ServerSocket serverSocket = new ServerSocket(5000);
      clientOutputStreams = new ArrayList<PrintWriter>();
      while(true){
        Socket clientSocket = serverSocket.accept();
        clientOutputStreams.add(new PrintWriter(clientSocket.getOutputStream()));

        Thread t = new Thread(new ClientHandler(clientSocket));
        t.start();
        System.out.println("Got a connection!");
      }
    } catch(IOException ioe){
      ioe.printStackTrace();
    }
  }

  public class ClientHandler implements Runnable{

    Socket clientSocket;
    BufferedReader reader;

    public ClientHandler(Socket clientSocket){
      this.clientSocket = clientSocket;
      try {
        InputStreamReader streamReader = new InputStreamReader(clientSocket.getInputStream());
        reader = new BufferedReader(streamReader);
      } catch(IOException ioe){
        ioe.printStackTrace();
      }
    }

    @Override
    public void run(){
      try {
        String message;

        while((message = reader.readLine()) != null){
          System.out.println("read: " + message);
          tellEveryone(message);
        }

      } catch(IOException ioe){
        ioe.printStackTrace();
      }
    }
  }

  public void tellEveryone(String message){
    Iterator<PrintWriter> it = clientOutputStreams.iterator();
    while(it.hasNext()){
        PrintWriter writer = it.next();
        writer.println(message);
        writer.flush();
    }
  }

}
