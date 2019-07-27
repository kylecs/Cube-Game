package engine;

import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class VAO {
	private int vaoId;
	
	public VAO() {
		vaoId = glGenVertexArrays();
	}
	
	public void bind() {
		glBindVertexArray(vaoId);
	}
	
}
