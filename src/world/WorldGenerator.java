package world;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.joml.Vector2i;

import engine.Game;
import misc.Settings;
import misc.Util;

public class WorldGenerator {

	private class WorldGenerationThread extends Thread {
		public void run() {
			World w = Game.get().getWorld();
			while(!shouldStop) {
				int baseX = Util.getBaseCoord((int)Game.get().getPlayer().position.x);
				int baseZ = Util.getBaseCoord((int)Game.get().getPlayer().position.z);
				for(int curRenderDist = 1; curRenderDist < Settings.RENDER_DIST; curRenderDist++) {
					for(int x = -curRenderDist; x < curRenderDist; x++) {
						for(int z = -curRenderDist; z < curRenderDist; z++) {
							Vector2i base = new Vector2i(baseX + x * Chunk.CHUNK_WIDTH, baseZ + z * Chunk.CHUNK_WIDTH);
							if(!w.getChunkMap().containsKey(base)) {
								Chunk c = new Chunk(base.x, base.y);
								w.getChunkMap().put(base, c);
							}
						}
					}
				}
				
			}
			
		}
	}
	
	private boolean shouldStop = false;
	private WorldGenerationThread generator;
	
	public WorldGenerator() {
		generator = new WorldGenerationThread();
		generator.start();
	}
	
	public void shutdown() {
		shouldStop = true;
	}
}
