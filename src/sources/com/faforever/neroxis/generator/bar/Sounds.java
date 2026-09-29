package com.faforever.neroxis.generator.bar;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/Sounds.class */
final class Sounds {
    private static volatile long lastPlayed;

    private Sounds() {
    }

    static void message(int volume) {
        play(new double[]{659.25d, 880.0d}, 0.13d, volume);
    }

    static void attention(int volume) {
        play(new double[]{783.99d, 987.77d, 1174.66d}, 0.11d, volume);
    }

    private static void play(double[] notes, double seconds, int volume) {
        long now = System.currentTimeMillis();
        if (volume <= 0 || now - lastPlayed < 700) {
            return;
        }
        lastPlayed = now;
        Thread player = new Thread(() -> {
            int perNote = (int) (((double) 44100.0f) * seconds);
            int tail = (int) (((double) 44100.0f) * 0.35d);
            byte[] data = new byte[((perNote * notes.length) + tail) * 2];
            double gain = (0.3d * ((double) volume)) / 100.0d;
            for (int note = 0; note < notes.length; note++) {
                int length = perNote + tail;
                for (int i = 0; i < length; i++) {
                    int index = (note * perNote) + i;
                    if ((index * 2) + 1 >= data.length) {
                        break;
                    }
                    double time = i / 44100.0f;
                    double envelope = Math.min(1.0d, ((double) i) / (((double) 44100.0f) * 0.004d)) * Math.exp((-time) * 9.0d);
                    double sample = (Math.sin(6.283185307179586d * notes[note] * time) + (0.35d * Math.sin(12.566370614359172d * notes[note] * time))) * envelope * gain;
                    int mixed = Math.max(-32768, Math.min(32767, ((short) ((data[(index * 2) + 1] << 8) | (data[index * 2] & 255))) + ((int) (sample * 32767.0d))));
                    data[index * 2] = (byte) mixed;
                    data[(index * 2) + 1] = (byte) (mixed >> 8);
                }
            }
            try {
                Clip clip = AudioSystem.getClip();
                clip.open(new AudioFormat(44100.0f, 16, 1, true, false), data, 0, data.length);
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();
                    }
                });
                clip.start();
            } catch (LineUnavailableException | RuntimeException e) {
            }
        }, "sound");
        player.setDaemon(true);
        player.start();
    }
}
