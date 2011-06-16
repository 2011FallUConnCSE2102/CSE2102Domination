package sound;

import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import networking.Message;
import networking.MessageReceiver;
import networking.UDPCommunicator;

/**
 *
 * @author Scott
 */
public class AudioStreamer implements MessageReceiver {

    private final UDPCommunicator communicator = new UDPCommunicator();
    private final byte playerID;
    private SendThread sendThread;
    private SourceDataLine sourceDataLine;
    private TargetDataLine targetDataLine;

    public AudioStreamer( byte playerID) {
              this.playerID = playerID;
    }

    public void kill() {
        sendThread.kill();
        try {
            communicator.disconnect();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void receiveMessage(Message message) {
        byte[] contents = (byte[]) message.getContents();
        if (contents != null && contents.length > 0 && contents[0] != playerID) {
            sourceDataLine.write(contents, 1, contents.length - 1);
        }
    }

    public void start(int receivePort, int sendPort) {
        AudioFormat audioFormat = new AudioFormat(8000f, 16, 1, true, false);
        try {
            targetDataLine = (TargetDataLine) AudioSystem.getLine(
                    new DataLine.Info(TargetDataLine.class, audioFormat));
            targetDataLine.open(audioFormat);
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(
                    new DataLine.Info(SourceDataLine.class, audioFormat));
            sourceDataLine.open(audioFormat);
        } catch (LineUnavailableException lue) {
            lue.printStackTrace();
        }
        targetDataLine.start();
        sourceDataLine.start();
        sendThread = new SendThread();
        sendThread.start();
        try {
            communicator.connect(Message.AUDIO, this, receivePort, sendPort);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private class SendThread extends Thread {

        private byte buffer[] = new byte[1024];
        private boolean run = true;

        private SendThread() {
            super("Audio Streamer Send Thread");
            setDaemon(true);
        }

        private void kill() {
            run = false;
        }

        @Override
        public void run() {
            try {
                while (run) {
                    int count = targetDataLine.read(buffer, 0, buffer.length);
                    if (count > 0) {
                        byte[] temp = new byte[count + 1];
                        temp[0] = playerID;
                        System.arraycopy(buffer, 0, temp, 1, count);
                        try {
                            communicator.sendMessage(temp);
                        } catch (IllegalStateException ise) {
                            //ise.printStackTrace();
                        }
                    }
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
}
