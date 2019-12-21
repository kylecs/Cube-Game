package sound;

import static org.lwjgl.openal.AL11.*;
import static org.lwjgl.openal.ALC11.*;
import org.lwjgl.stb.STBVorbis;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.system.MemoryStack;

public class SoundManager {
	
	Sound blockBreak;
	Sound song;
	
	public SoundManager() {
		String defaultDevice = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
		long device = alcOpenDevice(defaultDevice);
		
		int[] attributes = {0};
		long context = alcCreateContext(device, attributes);
		alcMakeContextCurrent(context);
		
		ALCCapabilities alcCapabilities = ALC.createCapabilities(device);
		ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);
		
		blockBreak = new Sound("rock_impact.ogg");
		song = new Sound("song1.ogg");
		song.play();
		song.setVolume(0.05f);
		song.setLooping(true);
		
		
	}
	
	public void playBlockBreak() {
		blockBreak.play();
	}
	
	
}
