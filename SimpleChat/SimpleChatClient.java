import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SimpleChatClient {
  JTextField outgoing;
  JTextArea incoming;
  BufferedReader reader;
  PrintWriter writer;
  Socket socket;

  public static void main(String[] args){
    new SimpleChatClient().go();
  }

  public void go(){
    JFrame frame = new JFrame("SimpleChatClient");
    JPanel mainPanel = new JPanel();

    incoming = new JTextArea(15, 40);
    incoming.setLineWrap(true);
    incoming.setWrapStyleWord(true);
    incoming.setEditable(false);
    JScrollPane scrollPane = new JScrollPane(incoming);
    scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
    scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

    outgoing = new JTextField(20);

    JButton sendButton = new JButton("Send");
    sendButton.addActionListener(new SendButtonListener());

    mainPanel.add(scrollPane);
    mainPanel.add(outgoing);
    mainPanel.add(sendButton);

    setUpNetworking();

    Thread readerThread = new Thread(new IncomingReader());
    readerThread.start();

    frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
    frame.setSize(400, 500);
    frame.setVisible(true);
  }

  private void setUpNetworking(){
    try {
      socket = new Socket("127.0.0.1", 5000);
      InputStreamReader streamReader = new InputStreamReader(socket.getInputStream());
      reader = new BufferedReader(streamReader);
      writer = new PrintWriter(socket.getOutputStream());
      System.out.println("Link Established");
    } catch(IOException ioe){
      ioe.printStackTrace();
    }
  }

  public class SendButtonListener implements ActionListener{
    @Override
    public void actionPerformed(ActionEvent ae){
      try {
        writer.println(outgoing.getText());
        writer.flush();
      } catch(Exception e){
        e.printStackTrace();
      }
      outgoing.setText("");
      outgoing.requestFocus();
    }
  }

  public class IncomingReader implements Runnable {
    @Override
    public void run(){
      while(true){
        try {
          String message = null;
          while((message = reader.readLine()) != null){
            incoming.append(message);
            incoming.append("\n");
          }
        } catch(IOException ie){
          ie.printStackTrace();
        }
      }
    }
  }


}
