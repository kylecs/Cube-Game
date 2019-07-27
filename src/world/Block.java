package world;

public class Block {
	public enum BlockType {
		AIR,
		GRASS,
		DIRT,
		STONE
	}
	
	private BlockType type;
	
	public Block(BlockType type) {
		this.type = type;
	}
	
	public BlockType getType() {
		return type;
	}
	
	public boolean isAir() {
		return type == BlockType.AIR;
	}
}
