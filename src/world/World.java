package world;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.joml.Vector2i;

import engine.ChunkMesh;
import game.Player;
import misc.Settings;

public class World {
	
	private int xOff = -Settings.RENDER_DIST;
	private int zOff = -Settings.RENDER_DIST;
	
	public void addNearChunk(Player p) {
		int curBaseX = getBaseCoord((int) p.position.x);
		int curBaseZ = getBaseCoord((int) p.position.z);
		
		xOff++;
		if(xOff == Settings.RENDER_DIST) {
			xOff = -Settings.RENDER_DIST;;
			zOff++;
		}
		if(zOff == Settings.RENDER_DIST) {
			zOff = -Settings.RENDER_DIST;
		}
				
		int baseX = curBaseX + (xOff * Chunk.CHUNK_WIDTH);
		int baseZ = curBaseZ + (zOff * Chunk.CHUNK_WIDTH);
		addChunk(baseX, baseZ);

	}
	
	Map<Vector2i, Chunk> chunkMap;
	public World() {
		chunkMap = new ConcurrentHashMap<Vector2i, Chunk>();
	}
	
	public boolean addChunk(int baseX, int baseZ) {
		Vector2i base = new Vector2i(baseX, baseZ);
		
		if(chunkMap.containsKey(base)) return false;
		
		Chunk c = new Chunk(baseX, baseZ);
		chunkMap.put(base, c);
		c.buildMesh();
		c.getMesh().bufferData();
		return true;
	}
	
	public void render() {
		for(Map.Entry<Vector2i, Chunk> entry: chunkMap.entrySet()) {
			ChunkMesh mesh = entry.getValue().getMesh();
			if(mesh != null) {
				mesh.render();
			}
		}
	}
	
	public Block getBlock(int x, int y, int z) {
		
		if(y >= Chunk.CHUNK_HEIGHT || y < 0)
			return new Block(Block.BlockType.AIR);
		
		int baseX = getBaseCoord(x);
		int baseZ = getBaseCoord(z);
		if(baseX > x) {
			System.out.println("GETBLOCK ERROR ");
		}
		if(baseZ > z) {
			System.out.println("getblock error!");
		}
		Vector2i base = new Vector2i(baseX, baseZ);
		if(!chunkMap.containsKey(base)) {
			return new Block(Block.BlockType.AIR);
		}
		Chunk chunk = chunkMap.get(base);
				
		return chunk.getBlock(x - baseX, y, z - baseZ);
		
	}
	
	public void updateChunkMesh(Vector2i base) {
		if(!chunkMap.containsKey(base)) return;
		Chunk c = chunkMap.get(base);
		if(c == null) return;
		c.buildMesh();
		c.getMesh().bufferData();
	}
	
	public void setBlock(int x, int y, int z, Block b) {
		int baseX = getBaseCoord(x);
		int baseZ = getBaseCoord(z);
		if(baseX > x) {
			System.out.println("SETBLOCK ERROR ");
		}
		if(baseZ > z) {
			System.out.println("setblock error!");
		}
		Vector2i base = new Vector2i(baseX, baseZ);
		if(!chunkMap.containsKey(base)) {
			return;
		}
		
		int relativeX = x - baseX;
		int relativeZ = z - baseZ;
		
		Chunk chunk = chunkMap.get(base);
		if(chunk == null) return;
		chunk.setBlock(relativeX, y, relativeZ, b);
		System.out.println("Setting BLOCK! " + chunk.getBaseX() + " " + chunk.getBaseZ());
		
		updateChunkMesh(base);
				
		if(relativeX == 0) {
			updateChunkMesh(new Vector2i(base.x - Chunk.CHUNK_WIDTH, base.y));
			System.out.println("adj remesh");
		}
		
		if(relativeX == Chunk.CHUNK_WIDTH - 1) {
			updateChunkMesh(new Vector2i(base.x + Chunk.CHUNK_WIDTH, base.y));
			System.out.println("adj remesh");

		}
		
		if(relativeZ == 0) {
			updateChunkMesh(new Vector2i(base.x, base.y - Chunk.CHUNK_WIDTH));
			System.out.println("adj remesh");

		}
		
		if(relativeZ == Chunk.CHUNK_WIDTH - 1) {
			updateChunkMesh(new Vector2i(base.x, base.y + Chunk.CHUNK_WIDTH));
			System.out.println("adj remesh");

		}
	}
	
	
	private int getBaseCoord(int x) {
		int base;
		if(x >= 0) {
			base =  x - (x % Chunk.CHUNK_WIDTH);
		}else {
			if(x % Chunk.CHUNK_WIDTH == 0) return x;
			base =  x - (Chunk.CHUNK_WIDTH - (-x % Chunk.CHUNK_WIDTH));
		}		
		return base;
		
	}
}
