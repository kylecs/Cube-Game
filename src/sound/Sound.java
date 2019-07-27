package sound;

import static org.lwjgl.openal.AL10.*;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.stb.STBVorbis;
import org.lwjgl.system.MemoryStack;

public class Sound {
	private int sourcePointer = -1;
	private int channels = -1;
	private int sampleRate = -1;
	private int format = -1;
	private int bufferPointer = -1;
	
	public Sound(String fileName) {
		
		MemoryStack.stackPush();
		IntBuffer channelsBuffer = MemoryStack.stackMallocInt(1);
		MemoryStack.stackPush();
		IntBuffer sampleRateBuffer = MemoryStack.stackMallocInt(1);

		
		ShortBuffer rawAudioBuffer = STBVorbis.stb_vorbis_decode_filename(fileName, channelsBuffer, sampleRateBuffer);

		channels = channelsBuffer.get();
		sampleRate = sampleRateBuffer.get();

		MemoryStack.stackPop();
		MemoryStack.stackPop();
		
		if(channels == 1) {
		    format = AL_FORMAT_MONO16;
		} else if(channels == 2) {
		    format = AL_FORMAT_STEREO16;
		}

		bufferPointer = alGenBuffers();

		alBufferData(bufferPointer, format, rawAudioBuffer, sampleRate);

		sourcePointer = alGenSources();

		alSourcei(sourcePointer, AL_BUFFER, bufferPointer);
	}
	
	public void play() {
		alSourcePlay(sourcePointer);
	}
	
	public void pause() {
		alSourcePause(sourcePointer);
	}
	
	public void setVolume(float volume) {
		alSourcef(sourcePointer, AL_GAIN, volume);
	}
}
