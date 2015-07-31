package com.mobvoi.be.joke;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.media.AudioManager;
import android.media.MediaPlayer;


/**
 * @author qli<qli@mobvoi.com>
 * @date 2015-07-15
 */

public class PlayerUtil {
    private static final String TAG = PlayerUtil.class.getSimpleName();

    public static void playAsMp3(InputStream is) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        try {
            // Write InputStream to File.
            File mp3File = File.createTempFile(timestamp, ".mp3");
            FileOutputStream mp3FileOS = new FileOutputStream(mp3File);
            byte buffer[] = new byte[1024];
            int length = 0;
            while ((length = is.read(buffer)) != -1) {
                mp3FileOS.write(buffer, 0, length);
            }
            mp3FileOS.close();

            MediaPlayer mp = new MediaPlayer();
            FileInputStream mp3FileIS = new FileInputStream(mp3File);
            mp.setDataSource(mp3FileIS.getFD());
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mp.prepare();
            mp.start();
        } catch (IOException e) {
            throw new RuntimeException(TAG + e.toString());
        }
    }

}
