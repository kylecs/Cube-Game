package world;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.joml.Vector2i;

import engine.Game;
import misc.Util;

public class World {
	
	private Map<Vector2i, Chunk> chunkMap;
	
	public World() {
		chunkMap = new ConcurrentHashMap<Vector2i, Chunk>();
	}
	
	public Map<Vector2i, Chunk> getChunkMap() {
		return chunkMap;
	}
	
	public boolean hasChunk(int baseX, int baseZ) {
		return chunkMap.containsKey(new Vector2i(baseX, baseZ));
	}
	
	public Block getBlock(int x, int y, int z) {
		
		if(y >= Chunk.CHUNK_HEIGHT || y < 0)
			return new Block(Block.BlockType.AIR);
		
		int baseX = Util.getBaseCoord(x);
		int baseZ = Util.getBaseCoord(z);
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
	
	public void setBlock(int x, int y, int z, Block b) {
		int baseX = Util.getBaseCoord(x);
		int baseZ = Util.getBaseCoord(z);
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
		Game.get().getChunkMeshRenderer().remesh(base);
		System.out.println("Setting BLOCK! " + chunk.getBaseX() + " " + chunk.getBaseZ());
		
				
		if(relativeX == 0) {
			
			System.out.println("adj remesh");
			Game.get().getChunkMeshRenderer().remesh(new Vector2i(base.x - Chunk.CHUNK_WIDTH, base.y));

		}
		
		if(relativeX == Chunk.CHUNK_WIDTH - 1) {
			
			System.out.println("adj remesh");
			Game.get().getChunkMeshRenderer().remesh(new Vector2i(base.x + Chunk.CHUNK_WIDTH, base.y));


		}
		
		if(relativeZ == 0) {
			
			System.out.println("adj remesh");
			Game.get().getChunkMeshRenderer().remesh(new Vector2i(base.x, base.y - Chunk.CHUNK_WIDTH));

		}
		
		if(relativeZ == Chunk.CHUNK_WIDTH - 1) {
			
			System.out.println("adj remesh");
			Game.get().getChunkMeshRenderer().remesh(new Vector2i(base.x, base.y + Chunk.CHUNK_WIDTH));


		}
	}

}
