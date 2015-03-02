package ua.hneu.languagetrainer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;

public class TextToVoiceMediaPlayer {
	MediaPlayer mediaPlayer;
    SoundPool soundPool = new SoundPool(1,1,1);
    public static int handle1;

	public TextToVoiceMediaPlayer() {
		mediaPlayer = new MediaPlayer();
		mediaPlayer.reset();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
	}

	public void loadAndPlay(String s, final float volume, final float speed) {
		try {
            //a stub
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            String s1 = URLEncoder.encode(s, "UTF-8");
            String url = "http://translate.google.com/translate_tts?ie=UTF-8&tl=ja&q="
                    + s1;
           /* mediaPlayer.reset();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setDataSource(url);
			mediaPlayer.prepare();
			mediaPlayer.start();*/
            try {
                URL url1 = new URL(url);
                URLConnection conexion = url1.openConnection();
                conexion.connect();
                // download the file
                InputStream input = new BufferedInputStream(url1.openStream());
                File file = new File(App.context.getFilesDir(), "temp.mp3");
                OutputStream output = new FileOutputStream(file);

                if (file.exists())
                {
                    file.delete();
                }
                file.createNewFile();

                FileOutputStream outStream = new FileOutputStream(file);
                byte[] buff = new byte[5 * 1024];

                int len;
                while ((len = input.read(buff)) != -1)
                {
                    outStream.write(buff, 0, len);
                }

                outStream.flush();
                outStream.close();
                input.close();

                Log.d("file ", file.getPath());
                Log.d("file ", file.length()+"");

            } catch (Exception e) {
                e.printStackTrace();
            }

           handle1 = soundPool.load(App.context.getFilesDir() +"/temp.mp3",1);
            soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                public void onLoadComplete(SoundPool soundPool, int sampleId,int status) {
                    Log.d("setOnLoadCompleteListener","loaded");
                    soundPool.play(handle1,volume,volume,1,0,speed);
                }
            });
		} catch (IllegalArgumentException e) {
			mediaPlayer.reset();
			e.printStackTrace();
		} catch (IllegalStateException e) {
			mediaPlayer.reset();
			Log.e("Text to voice media player", "Internet connection is lmited");
		} catch (IOException e) {
			e.printStackTrace();
		}
 	}

    public void pause() {
        soundPool.pause(1);
    }

    public void resume() {
        soundPool.resume(1);
    }

    public void stop() {
        soundPool.stop(1);
	}


}
