package ui;

import static org.lwjgl.opengl.GL33.*;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;

import engine.Shader;
import engine.VAO;
import engine.VBO;
import misc.ChunkVertex;

public class Hud {
	private Shader uiShader;
	private VAO vao;
	private VBO vbo;
	private List<Vector2f> verts;
	public Hud(int windowWidth, int windowHeight) {
		uiShader = new Shader("shaders/ui.vert", "shaders/ui.frag");
		vao = new VAO();
		vao.bind();
		
		vbo = new VBO();
		vbo.bind();
		
		glEnableVertexAttribArray(1);
		glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		
		verts = new ArrayList<Vector2f>();
		
		float width = 10f / windowWidth;
		float height = 10f / windowHeight;
		
		Vector2f c1 = new Vector2f(-width, -height); //bottom left
		Vector2f c2 = new Vector2f(-width, height); //top left
		Vector2f c3 = new Vector2f(width, -height); //bottom right
		Vector2f c4 = new Vector2f(width, height); //top right
		
		verts.add(c2);
		verts.add(c1);
		verts.add(c4);
		
		verts.add(c4);
		verts.add(c1);
		verts.add(c3);

		
		FloatBuffer buffer = BufferUtils.createFloatBuffer(verts.size() * 2);
		for(Vector2f v: verts) {
			buffer.put(v.x).put(v.y);
		}
		buffer.flip();
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);

	}
	
	public void render() {
		uiShader.use();
		vao.bind();
		vbo.bind();
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glDrawArrays(GL_TRIANGLES, 0, verts.size());
	}
}
