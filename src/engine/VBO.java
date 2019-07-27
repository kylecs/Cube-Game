package engine;

import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL33.*;

public class VBO {
private int vboId;
	
	public VBO() {
		vboId = glGenBuffers();
	}
	
	public void bind() {
		glBindBuffer(GL_ARRAY_BUFFER, vboId);
	}
}
