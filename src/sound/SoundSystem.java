package sound;

import java.net.URL;
import java.util.ArrayList;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;

/**
 *
 * @author Oliver Cai
 * @author Ian Dardik
 * @author Scott DellaTorre
 * @author Xurong Liu
 * @author Erik Scott
 * @author Ammar Zafar
 */
public class SoundSystem {

    public static final Sound ASSAULT_RIFLE = new Sound("M16.wav", 3000, 200);
    public static final String BATTLE_BACKGROUND_1 = "Contingency.ogg";
    public static final String BATTLE_BACKGROUND_2 = "Credits.ogg";
    public static final String BATTLE_BACKGROUND_3 = "Espionage.ogg";
    public static final String BATTLE_BACKGROUND_4 = "Gulag.ogg";
    public static final String BATTLE_BACKGROUND_5 = "Metro Chase.ogg";
    public static final String BATTLE_BACKGROUND_6 = "Point Blank.mp3";
    public static final Sound BULLET_DROP = new Sound("bulletdrop.wav", 0, 500);
    public static final Sound DROP_CLIP = new Sound("dropclip.wav", 0, 500);
    public static final Sound DRY_FIRE = new Sound("dryfire.wav", 0, 100);
    public static final Sound LOAD_CLIP = new Sound("loadclip.wav", 0, 500);
    public static final Sound WILHELM_SCREAM = new Sound("wilhelm.wav", 0, 1000);
    private MusicThread musicThread;
    private StopThread stopThread;

    public void playBackgroundMusic(double volume) {
        musicThread = new MusicThread(volume);
        musicThread.start();
    }

    public void playSound(Sound sound, int distance) {
        try {
            if (stopThread != null) {
                stopThread.kill();
            }
            sound.play(distance);
            stopThread = new StopThread(sound);
            stopThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopBackgroundMusic() {
        if (musicThread != null) {
            musicThread.kill();
        }
    }

    public static class Sound {

        private AudioInputStream audioStream;
        private Clip clip;
        private final int duration;
        private final int startFrame;

        public Sound(String soundName, int startFrame, int duration) {
            this.startFrame = startFrame;
            this.duration = duration;
            try {
                URL soundURL = SoundSystem.class.getResource("resources/" + soundName);
                audioStream = AudioSystem.getAudioInputStream(soundURL);
                clip = AudioSystem.getClip();
                clip.open(audioStream);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void play(int distance) {
            try {
                FloatControl panControl = (FloatControl) clip.getControl(FloatControl.Type.PAN);
                float pan = distance / 1000.0f;
                if (pan < -1) {
                    pan = -1;
                } else if (pan > 1) {
                    pan = 1;
                }
                panControl.setValue(pan);
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float gain = -25 - Math.abs(distance / 10);
                if (gain < -80f) {
                    gain = -80f;
                }
                gainControl.setValue(gain);
                clip.setFramePosition(startFrame);
                clip.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class MusicThread extends Thread {

        private ArrayList<String> musicClips = new ArrayList<String>();
        private boolean run = true;
        private final double volume;

        private MusicThread(double volume) {
            setDaemon(true);
            this.volume = volume;
        }

        private void kill() {
            run = false;
            interrupt();
        }

        @Override
        public void run() {
            while (run) {
                musicClips.add(BATTLE_BACKGROUND_1);
                musicClips.add(BATTLE_BACKGROUND_2);
                musicClips.add(BATTLE_BACKGROUND_3);
                musicClips.add(BATTLE_BACKGROUND_4);
                musicClips.add(BATTLE_BACKGROUND_5);
                musicClips.add(BATTLE_BACKGROUND_6);
                while (run && !musicClips.isEmpty()) {
                    try {
                        URL soundURL = SoundSystem.class.getResource("resources/" + musicClips.remove((int) (Math.random() * musicClips.size())));
                        AudioInputStream in = AudioSystem.getAudioInputStream(soundURL);
                        AudioFormat baseFormat = in.getFormat();
                        AudioFormat decodedFormat = new AudioFormat(
                                AudioFormat.Encoding.PCM_SIGNED,
                                baseFormat.getSampleRate(),
                                16,
                                baseFormat.getChannels(),
                                baseFormat.getChannels() * 2,
                                baseFormat.getSampleRate(),
                                false);
                        AudioInputStream audioStream = AudioSystem.getAudioInputStream(decodedFormat, in);
                        DataLine.Info info = new DataLine.Info(SourceDataLine.class, decodedFormat);
                        SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
                        if (line != null) {
                            line.open(decodedFormat);
                            FloatControl gainControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
                            gainControl.setValue(65 * (float) volume - 60);
                            byte[] data = new byte[4096];

                            line.start();

                            int nBytesRead;
                            while (run && (nBytesRead = audioStream.read(data, 0, data.length)) != -1) {
                                line.write(data, 0, nBytesRead);
                            }

                            line.drain();
                            line.stop();
                            line.close();
                            audioStream.close();
                        }

                    } catch (Exception e) {
                        if (run) {
                            //e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private class StopThread extends Thread {

        private final Sound sound;

        private StopThread(Sound sound) {
            this.sound = sound;
        }

        private void kill() {
            interrupt();
            try {
                join();
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                Thread.sleep(sound.duration);
            } catch (InterruptedException ie) {
                //ignore
            }
            sound.clip.stop();
        }
    }
}
