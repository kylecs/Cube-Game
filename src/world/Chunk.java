package world;

import com.flowpowered.noise.module.source.Perlin;

import engine.ChunkMesh;

public class Chunk {
	
	public static final int CHUNK_WIDTH = 16;
	public static final int CHUNK_HEIGHT = 128;
	
	Block[][][] blocks = new Block[CHUNK_WIDTH][CHUNK_HEIGHT][CHUNK_WIDTH];
	
	private int baseX, baseZ;
	private ChunkMesh mesh;
	
	public Chunk(int baseX, int baseZ) {
		this.baseX = baseX;
		this.baseZ = baseZ;
		this.mesh = new ChunkMesh(this);
		generate();
	}
	
	public void buildMesh() {
		mesh.buildMesh();
	}
	
	public ChunkMesh getMesh() {
		return mesh;
	}
	
	public int getBaseX() {
		return baseX;
	}
	
	public int getBaseZ() {
		return baseZ;
	}
	
	private void generate() {
		Perlin perlin = new Perlin();
		perlin.setPersistence(0.714);
		perlin.setOctaveCount(8);
		perlin.setSeed(93957398);
		
		for(int x = 0; x < CHUNK_WIDTH; x++) {
			for(int z = 0; z < CHUNK_WIDTH; z++) {
				double multiplier = 50;
				double divider = 1500;
				double offset = 4;
				
				double in1 = baseX + x;
				double in2 = baseZ + z;
				double f = offset + Math.abs(multiplier * perlin.getValue(in1 / divider, 1, in2 / divider));
				//System.out.println(f);

				for(int y = 0; y < CHUNK_HEIGHT; y++) {
					if(y < f) {
						setBlock(x, y, z, new Block(Block.BlockType.STONE));
					}else if(y < f + 1) {
						setBlock(x, y, z, new Block(Block.BlockType.DIRT));
					}else if(y < f + 2) {
						setBlock(x, y, z, new Block(Block.BlockType.GRASS));
					}else {
						setBlock(x, y, z, new Block(Block.BlockType.AIR));
					}
				}
			}
		}
	}
	
	
	//relative coordinates
	public void setBlock(int x, int y, int z, Block b) {
		blocks[x][y][z] = b;
	}
	
	public Block getBlock(int x, int y, int z) {
		return blocks[x][y][z];
	}
	
}
