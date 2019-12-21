package engine;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.joml.Vector2i;

import world.Chunk;
import world.World;

public class ChunkMeshRenderer {
	
	private boolean adjacentLoaded(World w, Vector2i base) {
		return w.hasChunk(base.x + Chunk.CHUNK_WIDTH, base.y) &&
				w.hasChunk(base.x - Chunk.CHUNK_WIDTH, base.y) &&
				w.hasChunk(base.x, base.y + Chunk.CHUNK_WIDTH) &&
				w.hasChunk(base.x, base.y-  Chunk.CHUNK_WIDTH);
	}
	
	private class ChunkMeshGenerator extends Thread{
		public void run() {
			World w = Game.get().getWorld();
			while(!shouldStop) {
				for(Map.Entry<Vector2i, Chunk> entry: w.getChunkMap().entrySet()) {
					if(entry.getValue().isEmpty()) continue;
					if(entry.getValue().hasInitialMesh() || !adjacentLoaded(w, entry.getKey())) continue;
					entry.getValue().setHasInitialMesh(true);
					//otherwise we can add the mesh
					ChunkMesh cm = new ChunkMesh(entry.getValue());
					meshes.put(entry.getKey(), cm);
				}
			}
		}
	}
	
	private Map<Vector2i, ChunkMesh> meshes;
	private ChunkMeshGenerator generator;
	
	private boolean shouldStop = false;
	
	public void remesh(Vector2i base) {
		ChunkMesh c = meshes.get(base);
		if(c == null) return;
		c.buildMesh();
		c.bufferData();
	}
	
	public ChunkMeshRenderer() {
		meshes = new ConcurrentHashMap<Vector2i, ChunkMesh>();
		generator = new ChunkMeshGenerator();
		generator.start();
	}
	
	public void shutdown() {
		shouldStop = true;
	}
	
	public void render() {
		
		for(Map.Entry<Vector2i, ChunkMesh> entry: meshes.entrySet()) {
			ChunkMesh mesh = entry.getValue();
			if(!mesh.isInitialized()) {
				mesh.init();
			}
			
			if(!mesh.isBuffered()) {
				mesh.bufferData();
			}
			
			mesh.render();
		}
	}
}
