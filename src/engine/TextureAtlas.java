package engine;
import static org.lwjgl.opengl.GL11.glGetFloatv;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.stb.STBImage.*;

import java.io.InputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.system.MemoryStack;

public class TextureAtlas {
	
	public static class BlockTexture {
		int topRow;
		int topCol;
		
		int sideRow;
		int sideCol;
		
		int bottomRow;
		int bottomCol;
		public BlockTexture(int topRow, int topCol, int sideRow, int sideCol, int bottomRow, int bottomCol) {
			this.topRow = topRow;
			this.topCol = topCol;
			
			this.sideRow = sideRow;
			this.sideCol = sideCol;
			
			this.bottomRow = bottomRow;
			this.bottomCol = bottomCol;
		}
	}
	
	public static BlockTexture TEX_GRASS = new BlockTexture(0, 0, 0, 1, 1, 0);
	public static BlockTexture TEX_DIRT = new BlockTexture(1, 0, 1, 0, 1, 0);
	public static BlockTexture TEX_STONE = new BlockTexture(1, 1, 1, 1, 1, 1);
	
	private int width = 0;
	private int height = 0;
	private int textureId = -1;
	private int rows = 0;
	private int cols = 0;
	private float spriteWidth = 0;
	private float spriteHeight = 0;
	private float pixelWidth = 0;
	private float pixelHeight = 0;
	
	public TextureAtlas(String filePath) {
		ByteBuffer buf = null;

		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer widthBuf = stack.mallocInt(1);
			IntBuffer heightBuf = stack.mallocInt(1);
			IntBuffer channelsBuf =stack.mallocInt(1);
			
			
			InputStream i = getClass().getClassLoader().getResourceAsStream(filePath);
			byte[] rawData = i.readAllBytes();
			ByteBuffer imageBuffer = BufferUtils.createByteBuffer(rawData.length);
			for(byte b: rawData) {
				imageBuffer.put(b);
			}
			
			imageBuffer.flip();
			
			buf = stbi_load_from_memory(imageBuffer, widthBuf, heightBuf, channelsBuf, 4);
			
			width = widthBuf.get();
			height = heightBuf.get();
		}catch(Exception e) {
			e.printStackTrace();
			return;
		}
		textureId = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, textureId);
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
		//glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		
		FloatBuffer largest_supported_anisotropy = BufferUtils.createFloatBuffer(1);
		glGetFloatv(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, largest_supported_anisotropy);
		glTexParameterf(GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, largest_supported_anisotropy.get());
		
		glGenerateMipmap(GL_TEXTURE_2D);
		
		rows = height / 34;
		cols = width / 34;
		
		spriteWidth = 32f / width;
		spriteHeight = 32f / width;
		
		pixelWidth = 1.0f / width;
		pixelHeight = 1.0f / height;
	}
	
	public float getBaseX(int texCol) {
		return (pixelWidth * 3 * texCol) + pixelWidth + (spriteWidth * texCol);
	}
	
	public float getBaseY(int texRow) {
		return pixelHeight * 3 * texRow + pixelHeight + spriteHeight * texRow;
	}
	
	
	public float getSpriteWidth() {

		return spriteWidth;
	}
	
	public float getSpriteHeight() {
		return spriteHeight;
	}
}
