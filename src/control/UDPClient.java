package control;

import database.Messages;
import user.User;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

/**
 * UDPChat
 * Created by Aleksandar on 9.4.2017..
 */
public class UDPClient  {
    public static UDPClient instance;
    private DatagramSocket socket;
    public  UDPClient() throws  Exception {
    socket = new DatagramSocket();
    socket.setSoTimeout(2000);
    }

    //Salje poruku na server
    public void sendMessage(User u, String message) throws Exception{
        String requestMsg = "msg" +"#"+ u.getUsername() + "#" + message;
        byte[] buffer = requestMsg.getBytes();
        DatagramPacket packet;
        packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("localhost"), 2017);
        socket.send(packet);
    }

    //Ucitava poruke sa servera
    public void getMessages( User u) throws Exception{
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date = null;
        if(Messages.instance.getMsgList().size() != 0)
            date = Messages.instance.getMsgList().get(Messages.instance.getMsgList().size()-1).getDate();
        else
            date = Date.from(Instant.now().minusSeconds(86400));
        String dateString = df.format(date);
        String requestMsg = "get" + "#" + u.getUsername() + "#" + dateString;
        byte[] buffer = requestMsg.getBytes();
        DatagramPacket packet;
        packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("localhost"), 2017);
        socket.send(packet);

        byte[] recvBuffer = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(recvBuffer,recvBuffer.length);

        socket.receive(receivePacket);
        String responseString  = new String(recvBuffer).trim();
        if(responseString.equalsIgnoreCase("EMPTY")) return;
        String[] tokens = responseString.split("#");
        Message rcvMessage = new Message(new User(tokens[0]),df.parse(tokens[1]),tokens[2]);
        Messages.instance.getMsgList().add(rcvMessage);
    }

    //Salje serveru zahtev za registraciju
    public boolean sendRegRequest(User u) throws IOException {
        String username = u.getUsername();
        String password = u.getPassword();
        String requestMsg = "reg"+"#"+username + "#" + password;
        byte[] buffer = requestMsg.getBytes();
        DatagramPacket packet;
        packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("localhost"), 2017);
        socket.send(packet);
        byte[] recvBuffer = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(recvBuffer,recvBuffer.length);

        socket.receive(receivePacket);
        String responseString  = new String(recvBuffer).trim();
        System.out.println("RESPONSE RECEIVED");
        return responseString.equals("OK");

    }

    //Salje serveru zahtev za log-in
    public boolean sendLogInRequest(User u) throws IOException {
        String username = u.getUsername();
        String password = u.getPassword();
        String requestMsg = "log"+"#"+username + "#" + password;
        byte[] buffer = requestMsg.getBytes();
        DatagramPacket packet;
        packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("localhost"), 2017);
        socket.send(packet);
        byte[] recvBuffer = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(recvBuffer,recvBuffer.length);

            socket.receive(receivePacket);
            String responseString  = new String(recvBuffer).trim();
            System.out.println("RESPONSE RECEIVED");
            return responseString.equals("OK");
    }

    public static void main(String[] args){
        try {
            instance = new UDPClient();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
