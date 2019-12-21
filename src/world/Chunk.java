package world;

import com.flowpowered.noise.module.source.Perlin;
//import org.lwjgl.stb.STBPerlin;

import engine.ChunkMesh;
import misc.Settings;

public class Chunk {
	
	public static final int CHUNK_WIDTH = 16;
	public static final int CHUNK_HEIGHT = 128;
	
	Block[][][] blocks = new Block[CHUNK_WIDTH][CHUNK_HEIGHT][CHUNK_WIDTH];
	
	private int baseX, baseZ;
	private boolean empty;
	
	private boolean initialMesh = false;
	
	public Chunk(int baseX, int baseZ) {
		this.baseX = baseX;
		this.baseZ = baseZ;
		this.empty = false;
		generate();
	}
	
	public void setHasInitialMesh(boolean val) {
		initialMesh = val;
	}
	
	public boolean hasInitialMesh() {
		return initialMesh;
	}
	
	public int getBaseX() {
		return baseX;
	}
	
	public int getBaseZ() {
		return baseZ;
	}
	
	public boolean isEmpty() {
		return empty;
	}
	
	private void generate() {
		long start = System.nanoTime();
		Perlin islandMarker = new Perlin();
		islandMarker.setPersistence(0.714);
		islandMarker.setOctaveCount(8);
		islandMarker.setSeed(Settings.SEED);
		
		Perlin heightGenerator = new Perlin();
		heightGenerator.setPersistence(0.714);
		heightGenerator.setOctaveCount(8);
		heightGenerator.setSeed(Settings.SEED * 2);

		boolean hasIsland = false;
		
		double islandVal[][] = new double[CHUNK_WIDTH][CHUNK_WIDTH];
		double islandThreshold = 20f;

		
		for(int x = 0; x < CHUNK_WIDTH; x++) {
			for(int z = 0; z < CHUNK_WIDTH; z++) {
				double multiplier = 50;
				double divider = 4000;
				
				double in1 = baseX + x;
				double in2 = baseZ + z;
				
				double result = Math.abs(multiplier * islandMarker.getValue(in1 / divider, in2 / divider, 0));
				//System.out.println(result);
				boolean island = result > islandThreshold;
				islandVal[x][z] = result;
				if(island) {
					hasIsland = true;
				}
			}
		}
		
		empty = !hasIsland;
		if(empty) {
			return;
		}
		
		for(int x = 0; x < CHUNK_WIDTH; x++) {
			for(int z = 0; z < CHUNK_WIDTH; z++) {
				boolean island = islandVal[x][z] > islandThreshold;
				
				double multiplier = 50;
				double divider = 3500;
				
				
				double in1 = baseX + x;
				double in2 = baseZ + z;
				
				double height = 48 + Math.abs(multiplier * heightGenerator.getValue(in1 / divider, in2 / divider, 0));
				
				//higher means more inland, starts at 0
				double islandInwardness = islandVal[x][z] - islandThreshold;
				
				//double baseLevel = defaultHeight - islandInwardness * 40;
				//System.out.println(baseLevel);
				
				//double height = 50;

				double bottom = height - islandInwardness * 2 - 1.5;
				
				for(int y = 0; y < CHUNK_HEIGHT; y++) {
					if(island&& y >= bottom) {
						if(y < height) {
							blocks[x][y][z] = new Block(Block.BlockType.STONE);
						}else if(y < height + 3) {
							blocks[x][y][z] = new Block(Block.BlockType.DIRT);
						}else if(y < height + 4) {
							blocks[x][y][z] = new Block(Block.BlockType.GRASS);
						}else {
							blocks[x][y][z] = new Block(Block.BlockType.AIR);
						}
					} else {
						blocks[x][y][z] = new Block(Block.BlockType.AIR);
					}
				}
			}
		}
		
		long end = System.nanoTime();
		double diff = ((double) end - start) / 1000000;

		//System.out.println("Chunk Generation Time: " + diff);
	}
	
	
	//relative coordinates
	public void setBlock(int x, int y, int z, Block b) {
		//handle if its now not empty
		blocks[x][y][z] = b;
	}
	
	public Block getBlock(int x, int y, int z) {
		if(empty) {
			return new Block(Block.BlockType.AIR);
		}
		return blocks[x][y][z];
	}
	
}
