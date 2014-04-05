package RTPSERVER.Server;

import RTPSERVER.Stream.Stream;
import RTPSERVER.Stream.Viewer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Created by christopherhuang on 4/4/14.
 */
public class ForwardingRunnable implements Runnable {
    private Stream stream;

    public ForwardingRunnable(Stream stream){
        this.stream = stream;
    }

    @Override
    public void run(){
        DatagramSocket socket = null;
        DatagramPacket RTPpacket = null;

        try{
            socket = new DatagramSocket(stream.RTPPORT);
            RTPpacket = new DatagramPacket(new byte[512], 512);

            socket.setSoTimeout(15000);
            socket.receive(RTPpacket);

            //here should deal with the synchronization
            synchronized (this.stream){
                for (Viewer viewer : stream.viewers){
                    DatagramPacket forward = new DatagramPacket(RTPpacket.getData(),
                            RTPpacket.getLength(), viewer.getAddress(), viewer.getPort());
                    socket.send(forward);
                }
            }
        }
        catch (IOException ex){
            System.out.println(ex);
        }
    }
}
