# Jukebox
A fast, unopinionated method to play music from your FTC Robot Controller.

# How to Use
To use Jukebox, just drag-and-drop into the Teamcode folder.
Here's some example code:

```
import org.hermitsocialclub.ftc.utils.Jukebox;

public class YourOpMode extends LinearOpMode {

	public void runOpMode() {
	
		// Play a short clip using the built-in FTC SDK.
		// Place a .wav file in the res/raw/ in Android Studio.
		Jukebox.playSoundFromSDK(hardwareMap, "frolic");
		
		// Play a full-on music song using Android's API.
		// Place a .wav file **on the phone** in /storage/emulated/0/JukeboxExternalStorage/
		// You might have to create the folder if it doesn't already exist.
		Jukebox.playSoundFromAndroid(hardwareMap.appContext, "rickroll");
		
		waitForStart();
		while(opModeActive()) {sleep(1);}
		
		// To stop the music, use the destroy method.
		// If you don't, the music will keep on playing after the OpMode is stopped :)
		Jukebox.destroy();
		
	}
}
```