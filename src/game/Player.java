package game;

import org.joml.Vector3f;

import engine.Camera;
import engine.Game;
import input.KeyHandler;
import input.MouseHandler;
import misc.Settings;
import misc.Util;
import world.Block;
import world.World;

public class Player {
	public Vector3f position;
	public Vector3f rotation;
	private Camera camera;
	private double verticalVel = 0;
	private boolean movementEnabled = false;
	boolean allowJump = true;
	
	public Player() {
		this.position = new Vector3f(0.5f, 40, 0.5f);
		this.rotation = new Vector3f(0, 180, 0);
		camera = new Camera(position, rotation);

	}
	
	public Camera getCamera() {
		return camera;
	}
	
	private void updateRotation(MouseHandler mouseHandler) {
		rotation.x += mouseHandler.getDeltaY() * Settings.SENSITIVITY;
		rotation.y += mouseHandler.getDeltaX() * Settings.SENSITIVITY;
		
		if(rotation.x > 90) {
			rotation.x = 90;
		}
		
		if(rotation.x < -90) {
			rotation.x = -90;
		}
	}
	
	private void updateCamera() {
		camera.position.set(position);
		
		camera.rotation.set(rotation);
		}
	
	private Vector3f getAbsolutePositionDelta(KeyHandler handler, double deltaTime, boolean onGround) {
		//get relative position delta
		Vector3f relativeDelta = new Vector3f(0, 0, 0);
		
		float modifier = 1.0f;
		
		if(handler.shiftPressed()) {
			modifier = 1.5f;
		}
		
		if(handler.wPressed()) {
			relativeDelta.z = -Settings.MOVE_SPEED;
		}else if(handler.sPressed()) {
			relativeDelta.z = Settings.MOVE_SPEED;
		}
		
		if(handler.aPressed()) {
			relativeDelta.x = -Settings.MOVE_SPEED;
		}else if(handler.dPressed()) {
			relativeDelta.x = Settings.MOVE_SPEED;
		}
		
		Vector3f absoluteDelta = new Vector3f(0, 0, 0);
		
		//forward movement
		absoluteDelta.x += (float) (Math.cos(Math.toRadians(rotation.y + 90)) * relativeDelta.z * deltaTime);
		absoluteDelta.z += (float) (Math.sin(Math.toRadians(rotation.y + 90)) * relativeDelta.z * deltaTime);
		
		//strafe movement
		absoluteDelta.x += Math.cos(Math.toRadians(rotation.y)) * relativeDelta.x * deltaTime;
		absoluteDelta.z += Math.sin(Math.toRadians(rotation.y)) * relativeDelta.x * deltaTime;	
		
		absoluteDelta.mul(modifier);
		
		return absoluteDelta;
	}
	
	public void breakLookingAt() {
		Vector3f ray = Util.viewRayDelta(position, rotation, 0.002f);
		Vector3f curPos = new Vector3f(position);
		for(int step = 0; step < 2000; step++) {
			curPos.sub(ray);
			int x = getAdjustedCoord(curPos.x);
			int z = getAdjustedCoord(curPos.z);
			Block b = Game.get().getWorld().getBlock(x, (int)curPos.y, z);
			if(!b.isAir()) {
				System.out.println("Breaking block");
				Game.get().getSoundManager().playBlockBreak();
				Game.get().getWorld().setBlock(x, (int) curPos.y, z, new Block(Block.BlockType.AIR));
				return;
			}
		}
		System.out.println("No block found!");
		
	}
	
	public int getAdjustedCoord(float x) {
		int coord;
		if(x >= 0) {
			coord = (int)x;
		}else {
			coord =(int)x - 1;
		}
		return coord;
	}
	
	public void update(KeyHandler keyHandler, MouseHandler mouseHandler, double deltaTime) {
	
		
		updateRotation(mouseHandler);
		
		World world = Game.get().getWorld();
		
		boolean onGround = false;
		
		int xPos = getAdjustedCoord(position.x);
		int zPos = getAdjustedCoord(position.z);
		
		Block below = world.getBlock(xPos, (int)(position.y - 1.7f), zPos);
		Block above = world.getBlock(xPos, (int)(position.y + 0.5f), zPos);
		
		boolean blockedAbove = !above.isAir();

		if(below.getType() != Block.BlockType.AIR) {
			onGround = true;
			verticalVel = 0;
		}
		
		//check if there are blocks inhibiting the 4 directions
		Block blockPlusZ1 = world.getBlock(xPos, (int)(position.y - 1.5f), zPos + 1);
		Block blockPlusZ2 = world.getBlock(xPos, (int)(position.y - 0.5f), zPos + 1);
		boolean blockPlusZ = !blockPlusZ1.isAir() || !blockPlusZ2.isAir();

		Block blockMinusZ1 = world.getBlock(xPos, (int)(position.y - 1.5f), zPos - 1);
		Block blockMinusZ2 = world.getBlock(xPos, (int)(position.y - 0.5f), zPos - 1);
		boolean blockMinusZ = !blockMinusZ1.isAir() || !blockMinusZ2.isAir();
		
		Block blockPlusX1 = world.getBlock(xPos + 1, (int)(position.y - 1.5f), zPos);
		Block blockPlusX2 = world.getBlock(xPos + 1, (int)(position.y - 0.5f), zPos);
		boolean blockPlusX = !blockPlusX1.isAir() || !blockPlusX2.isAir();

		Block blockMinusX1 = world.getBlock(xPos - 1, (int)(position.y - 1.5f), zPos);
		Block blockMinusX2 = world.getBlock(xPos - 1, (int)(position.y - 0.5f), zPos);
		boolean blockMinusX = !blockMinusX1.isAir() || !blockMinusX2.isAir();
		
		float intraZ;
		if(position.z >= 0) {
			intraZ = position.z - ((int) position.z);
		}else {
			intraZ = 1 - (((int) position.z) - position.z);
		}
		
		float intraX;
		if(position.x >= 0) {
			intraX = position.x - ((int) position.x);
		}else {
			intraX = 1 - (((int) position.x) - position.x);
		}
		
		Vector3f absoluteDelta = getAbsolutePositionDelta(keyHandler, deltaTime, onGround);
		
		if(absoluteDelta.x >= 0) {
			if(!blockPlusX || intraX + absoluteDelta.x < 0.70f) {
				position.x += absoluteDelta.x;
			}
		}else {
			if(!blockMinusX || intraX + absoluteDelta.x > 0.30f) {
				position.x += absoluteDelta.x;
			}
		}
		
		if(absoluteDelta.z >= 0) {
			if(!blockPlusZ || intraZ + absoluteDelta.z < 0.70f) {
				position.z += absoluteDelta.z;
			}
		}else {
			if(!blockMinusZ || intraZ + absoluteDelta.z > 0.30f) {
				position.z += absoluteDelta.z;
			}
		}
		
		if(keyHandler.spacePressed() && allowJump && onGround) {
			this.verticalVel += Settings.JUMP_SPEED;
			allowJump = false;
		}
		if(!keyHandler.spacePressed()) {
			allowJump = true;
		}
		
		if(onGround && verticalVel < 0.001f) {
			this.verticalVel = 0;
		}else {
			if(this.verticalVel > -Settings.FALL_MAX) {
				this.verticalVel -= Settings.FALL_ACCEL * deltaTime;
			}
		}
		
		if(!blockedAbove || this.verticalVel < 0)
			this.position.y += this.verticalVel * deltaTime;
		
		updateCamera();
	}
}
