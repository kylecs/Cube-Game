package sound;

import static org.lwjgl.openal.AL10.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.system.MemoryStack;

public class Sound {
	private int sourcePointer = -1;
	private int channels = -1;
	private int sampleRate = -1;
	private int format = -1;
	private int bufferPointer = -1;
	
	public Sound(String filePath) {
		
		MemoryStack.stackPush();
		IntBuffer channelsBuffer = MemoryStack.stackMallocInt(1);
		MemoryStack.stackPush();
		IntBuffer sampleRateBuffer = MemoryStack.stackMallocInt(1);

		InputStream i = getClass().getClassLoader().getResourceAsStream(filePath);
		byte[] rawData;
		try {
			rawData = i.readAllBytes();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		ByteBuffer fileBuff = BufferUtils.createByteBuffer(rawData.length);
		for(byte b: rawData) {
			fileBuff.put(b);
		}
		
		fileBuff.flip();
		
		//ShortBuffer rawAudioBuffer = STBVorbis.stb_vorbis_decode_filename(fileName, channelsBuffer, sampleRateBuffer);
		ShortBuffer rawAudioBuffer = STBVorbis.stb_vorbis_decode_memory(fileBuff, channelsBuffer, sampleRateBuffer);

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
	
	public void setLooping(boolean looping) {
		alSourcei(sourcePointer, AL_LOOPING, looping ? 1 : 0);
	}
}
