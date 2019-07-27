package input;

import org.lwjgl.glfw.GLFWKeyCallback;
import static org.lwjgl.glfw.GLFW.*;

public class KeyHandler extends GLFWKeyCallback{

	private boolean isWPresed = false;
	private boolean isSPresed = false;
	private boolean isAPresed = false;
	private boolean isDPresed = false;
	private boolean isUpPressed = false;
	private boolean isDownPressed = false;
	private boolean isLeftPressed = false;
	private boolean isRightPressed = false;
	private boolean isSpacePressed = false;
	private boolean isShiftPressed = false;
	private boolean isEscapePressed = false;
	
	@Override
	public void invoke(long window, int key, int scancode, int action, int mods) {
		
		if(action == GLFW_PRESS) {
			switch(key) {
			case GLFW_KEY_W:
				isWPresed = true;
				break;
			case GLFW_KEY_S:
				isSPresed = true;
				break;
			case GLFW_KEY_A:
				isAPresed = true;
				break;
			case GLFW_KEY_D:
				isDPresed = true;
				break;
			case GLFW_KEY_UP:
				isUpPressed = true;
				break;
			case GLFW_KEY_DOWN:
				isDownPressed = true;
				break;
			case GLFW_KEY_LEFT:
				isLeftPressed = true;
				break;
			case GLFW_KEY_RIGHT:
				isRightPressed = true;
				break;
			case GLFW_KEY_SPACE:
				isSpacePressed = true;
				break;
			case GLFW_KEY_LEFT_SHIFT:
				isShiftPressed = true;
				break;
			case GLFW_KEY_ESCAPE:
				isEscapePressed = true;
				break;
			}	
		}
		
		if(action == GLFW_RELEASE) {
			switch(key) {
			case GLFW_KEY_W:
				isWPresed = false;
				break;
			case GLFW_KEY_S:
				isSPresed = false;
				break;
			case GLFW_KEY_A:
				isAPresed = false;
				break;
			case GLFW_KEY_D:
				isDPresed = false;
				break;
			case GLFW_KEY_UP:
				isUpPressed = false;
				break;
			case GLFW_KEY_DOWN:
				isDownPressed = false;
				break;
			case GLFW_KEY_LEFT:
				isLeftPressed = false;
				break;
			case GLFW_KEY_RIGHT:
				isRightPressed = false;
				break;
			case GLFW_KEY_SPACE:
				isSpacePressed = false;
				break;
			case GLFW_KEY_LEFT_SHIFT:
				isShiftPressed = false;
				break;
			case GLFW_KEY_ESCAPE:
				isEscapePressed = false;
				break;
			}	
		}
	}
	
	public boolean wPressed() {
		return isWPresed;
	}
	
	public boolean sPressed() {
		return isSPresed;
	}
	
	public boolean aPressed() {
		return isAPresed;
	}
	
	public boolean dPressed() {
		return isDPresed;
	}
	
	public boolean upPressed() {
		return isUpPressed;
	}
	
	public boolean downPressed() {
		return isDownPressed;
	}
	
	public boolean leftPressed() {
		return isLeftPressed;
	}
	
	public boolean rightPressed() {
		return isRightPressed;
	}
	
	public boolean spacePressed() {
		return isSpacePressed;
	}
	
	public boolean shiftPressed() {
		return isShiftPressed;
	}
	
	public boolean escapePressed() {
		return isEscapePressed;
	}
	
	
}
