package concentratequest.ui.swing;

import java.util.Random;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * Plays brown noise (a deep, steady rumble that helps many people focus)
 * on a background thread. AutoCloseable, so try-with-resources stops it.
 */
public final class BrownNoise implements AutoCloseable {
    private static final float SAMPLE_RATE = 44_100f;
    private static final int BUFFER_SAMPLES = 2_048;
    private static final double VOLUME = 0.25;

    private final SourceDataLine line;
    private final Thread player;
    private volatile boolean playing = true;

    private BrownNoise(SourceDataLine line) {
        this.line = line;
        // (anonymous inner class: the Runnable the thread runs)
        this.player = new Thread(new Runnable() {
            @Override
            public void run() {
                play();
            }
        }, "brown-noise");
        this.player.setDaemon(true);
    }

    /**
     * Starts the noise if enabled and if the computer has a sound output.
     * Returns null otherwise: try-with-resources accepts a null resource.
     */
    public static BrownNoise startIf(boolean enabled) {
        if (!enabled) {
            return null;
        }
        try {
            AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
            SourceDataLine line = AudioSystem.getSourceDataLine(format);
            line.open(format);
            line.start();
            BrownNoise noise = new BrownNoise(line);
            noise.player.start();
            return noise;
        } catch (LineUnavailableException | IllegalArgumentException e) {
            System.out.println("(No sound output available, the quest continues in silence.)");
            return null;
        }
    }

    /** Brown noise is white noise integrated over time: each sample drifts from the last one. */
    private void play() {
        Random random = new Random();
        byte[] buffer = new byte[BUFFER_SAMPLES * 2];
        double last = 0;
        while (playing) {
            for (int i = 0; i < BUFFER_SAMPLES; i++) {
                double white = random.nextDouble() * 2 - 1;
                last = (last + 0.02 * white) / 1.02;
                short sample = (short) (last * 3.5 * VOLUME * Short.MAX_VALUE);
                buffer[2 * i] = (byte) sample;              // little-endian: low byte first
                buffer[2 * i + 1] = (byte) (sample >> 8);
            }
            line.write(buffer, 0, buffer.length);
        }
    }

    @Override
    public void close() {
        playing = false;
        line.stop();
        line.close();
    }
}
