package org.hermitsocialclub.ftc.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.qualcomm.ftccommon.FtcEventLoop;
import com.qualcomm.ftccommon.FtcEventLoopHandler;
import com.qualcomm.ftccommon.SoundPlayer;
import com.qualcomm.robotcore.eventloop.EventLoop;
import com.qualcomm.robotcore.eventloop.EventLoopManager;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.robocol.RobocolDatagram;
import com.qualcomm.robotcore.robocol.TelemetryMessage;
import com.qualcomm.robotcore.robot.RobotState;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.internal.network.NetworkConnectionHandler;
import org.firstinspires.ftc.robotcore.internal.opmode.OpModeManagerImpl;
import org.firstinspires.ftc.robotcore.internal.opmode.TelemetryImpl;
import org.firstinspires.ftc.robotcore.internal.opmode.TelemetryInternal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <h1>Jukebox</h1>
 * A fast, unopinionated method for legally playing music from your FTC Robot Controller.
 *
 * @author Hermit Social Club
 * @version 1.3
 */
public class Jukebox {

    private static ConcurrentHashMap<String, MediaPlayer> musics = new ConcurrentHashMap<String, MediaPlayer>();

    public static void playSoundFromSDK(HardwareMap hm, String name) {
        try {
            SoundPlayer.getInstance().startPlaying(
                    hm.appContext,
                    hm.appContext.getResources().getIdentifier(name, "raw", hm.appContext.getPackageName())
            );
        } catch (Exception e) {

        }
    }

    public static void playSoundFromAndroid(Context c, String filename) {
        playSoundFromAndroid(c, filename, false);
    }

    public static void playSoundFromAndroid(final Context c,  final String filename, boolean repeat) {
        try {
            if(musics.contains(filename)) {
                musics.get(filename).stop();
                musics.remove(filename);
			}
			MediaPlayer mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

			//Attempt to play the file from /storage/emulated/0/JukeboxExternalStorage if it exists
			//Otherwise play from TeamCode/res/raw
			File path = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "JukeboxExternalStorage" + File.separator + filename + ".wav");
			if (path.exists()) {
				mediaPlayer.setDataSource(path.getPath());
			} else {
				mediaPlayer.setDataSource(c, Uri.parse("android.resource://" + c.getPackageName() + "/raw/" + filename));
			}
			mediaPlayer.setVolume(1.0F, 1.0F);
			mediaPlayer.prepare();
			mediaPlayer.setLooping(repeat);
			mediaPlayer.start();
			musics.put(filename, mediaPlayer);
        } catch (IOException e) {
            setTelemetryWarning(e.toString());
        }
    }

    public static void setTelemetryWarning(String s) {
        RobotLog.setGlobalWarningMessage(s);
        try {
            TelemetryMessage telemetry = new TelemetryMessage();
            telemetry.setTag(EventLoopManager.SYSTEM_WARNING_KEY);
            telemetry.addData(EventLoopManager.SYSTEM_WARNING_KEY, s);
            NetworkConnectionHandler.getInstance().sendDatagram(new RobocolDatagram(telemetry.toByteArrayForTransmission()));
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public static void destroy() {
        for (MediaPlayer mp : musics.values()) {
            mp.stop();
            musics.remove(mp);
        }
    }

}
