package engine;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL33.*;

import org.joml.Vector3i;
import org.lwjgl.BufferUtils;

import engine.TextureAtlas.BlockTexture;
import misc.Settings;
import misc.ChunkVertex;
import world.Block;
import world.Chunk;
import world.World;

public class ChunkMesh {
	private VAO vao;
	private VBO vbo;
	private List<ChunkVertex> verts;
	Chunk chunk;
	
	private boolean initialized = false;
	private boolean buffered = false;
	
	public ChunkMesh(Chunk chunk) {
		this.chunk = chunk;
		verts = new ArrayList<ChunkVertex>();
		buildMesh();
		
	}
	
	public boolean isInitialized() {
		return initialized;
	}
	
	public boolean isBuffered() {
		return buffered;
	}
	
	public void init() {
		vao = new VAO();
		vao.bind();
	
		vbo = new VBO();
		vbo.bind();
		
		glEnableVertexAttribArray(1);
		glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
		
		glEnableVertexAttribArray(2);
		glVertexAttribPointer(2, 2, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
		
		glEnableVertexAttribArray(3);
		glVertexAttribPointer(3, 1, GL_FLOAT, false, 6 * Float.BYTES, 5 * Float.BYTES);
		initialized = true;
	}
	
	public void render() {
		vao.bind();
		vbo.bind();
		glDrawArrays(GL_TRIANGLES, 0, verts.size());
	}
	
	public void bufferData() {
		//System.out.println("Buffering mesh! " + chunk.getBaseX() + " " + chunk.getBaseZ());
		vao.bind();
		vbo.bind();
		
		//build buffer
		FloatBuffer buffer = BufferUtils.createFloatBuffer(verts.size() * 6);
		for(ChunkVertex v: verts) {
			buffer.put(v.x).put(v.y).put(v.z).put(v.texX).put(v.texY).put(v.light);
		}
		buffer.flip();
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
		buffered = true;
	}
	
	
	private void addVert(float x, float y, float z, float texX, float texY, float light) {
		verts.add(new ChunkVertex(x, y, z, texX, texY, light));
	}
	
	public void addFace(Vector3i position, float[] rawVerts, int texRow, int texCol, float light) {
		TextureAtlas atlas = Game.get().getTextureAtlas();
		float baseX = atlas.getBaseX(texCol);
		float baseY = atlas.getBaseY(texRow);
		for(int i = 0; i < 30; i+= 5) {
			addVert(
				position.x + rawVerts[i],			//x
				position.y + rawVerts[i + 1],		//y
				position.z + rawVerts[i + 2],		//z
				
				baseX + atlas.getSpriteWidth() * rawVerts[i + 3],		
				baseY + atlas.getSpriteHeight() * rawVerts[i + 4],
				light
			);
			
		}
		
	}
	
	public static BlockTexture getTexture(Block.BlockType type) {
		if(type == Block.BlockType.DIRT) {
			return TextureAtlas.TEX_DIRT;
		}else if(type == Block.BlockType.GRASS) {
			return TextureAtlas.TEX_GRASS;
		}else if(type == Block.BlockType.STONE) {
			return TextureAtlas.TEX_STONE;
		}
		
		return TextureAtlas.TEX_STONE;
	}
	
	public void buildMesh() {
		long start = System.nanoTime();
		//System.out.println("Building mesh! " + chunk.getBaseX() + " " + chunk.getBaseZ());
		verts.clear();
		World w = Game.get().getWorld();
		int baseX = chunk.getBaseX();
		int baseZ = chunk.getBaseZ();
		
		
		for(int x = 0; x < Chunk.CHUNK_WIDTH; x++) {
			for(int y = 0; y < Chunk.CHUNK_HEIGHT; y++) {
				for(int z = 0; z < Chunk.CHUNK_WIDTH; z++) {
					Block b = chunk.getBlock(x, y, z);
					Block.BlockType type = b.getType();
					
					if(type == Block.BlockType.AIR) continue;
					
					BlockTexture tex = getTexture(type);
					
					Vector3i position = new Vector3i(baseX + x, y, baseZ + z);
					
					
					if(w.getBlock(x + baseX, y, z + baseZ - 1).getType() == Block.BlockType.AIR)
						addFace(position, FRONT_VERTS, tex.sideRow, tex.sideCol, Settings.SIDE_LIGHT);
					
					if(w.getBlock(x + baseX, y, z + baseZ + 1).getType() == Block.BlockType.AIR)
						addFace(position, BACK_VERTS, tex.sideRow, tex.sideCol, Settings.SIDE_LIGHT);
					
					if(w.getBlock(x + baseX - 1, y, z + baseZ).getType() == Block.BlockType.AIR)
						addFace(position, SIDE1_VERTS, tex.sideRow, tex.sideCol, Settings.SIDE_LIGHT);
					
					if(w.getBlock(x + baseX + 1, y, z + baseZ).getType() == Block.BlockType.AIR)
						addFace(position, SIDE2_VERTS, tex.sideRow, tex.sideCol, Settings.SIDE_LIGHT);
					
					if(w.getBlock(x + baseX, y + 1, z + baseZ).getType() == Block.BlockType.AIR)
						addFace(position, TOP_VERTS, tex.topRow, tex.topCol, Settings.TOP_LIGHT);
					
					if(w.getBlock(x + baseX, y - 1, z + baseZ).getType() == Block.BlockType.AIR)
						addFace(position, BOTTOM_VERTS, tex.bottomRow, tex.bottomCol, Settings.BOTTOM_LIGHT);
					
				}
			}
		}
		long end = System.nanoTime();
		double diff = ((double) end - start) / 1000000;
		//System.out.println("Build mesh time: " + diff);

	}
	
	public static final float FRONT_VERTS[] = {
			0, 0, 0, 1, 1,
			0, 1, 0, 1, 0,
			1, 0, 0, 0, 1,

			1, 1, 0, 0, 0,
			1, 0, 0, 0, 1,
			0, 1, 0, 1, 0,
	};
	public static final float BACK_VERTS[] = {
			0, 0, 1, 0, 1,
			1, 0, 1, 1, 1,
			0, 1, 1, 0, 0,

			1, 1, 1, 1, 0,
			0, 1, 1, 0, 0,
			1, 0, 1, 1, 1,
	};
	
	public static final float SIDE1_VERTS[] = {
			0, 0, 1, 1, 1,
			0, 1, 1, 1, 0,
			0, 0, 0, 0, 1,

			0, 0, 0, 0, 1,
			0, 1, 1, 1, 0,
			0, 1, 0, 0, 0,
	};
	
	public static final float SIDE2_VERTS[] = {
			1, 0, 1, 0, 1,
			1, 0, 0, 1, 1,
			1, 1, 1, 0, 0,

			1, 0, 0, 1, 1,
			1, 1, 0, 1, 0,
			1, 1, 1, 0, 0,
	};
	
	public static final float TOP_VERTS[] = {
			0, 1, 0, 1, 1,
			1, 1, 1, 0, 0,
			1, 1, 0, 0, 1,

			1, 1, 1, 0, 0,
			0, 1, 0, 1, 1,
			0, 1, 1, 1, 0,
	};
	
	public static final float BOTTOM_VERTS[] = {
			0, 0, 0, 0, 1,
			1, 0, 0, 0, 0,
			1, 0, 1, 1, 0,

			1, 0, 1, 1, 0,
			0, 0, 1, 1, 1,
			0, 0, 0, 0, 1,
	};
	
	
}
