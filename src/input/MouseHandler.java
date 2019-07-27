package input;

import org.lwjgl.glfw.GLFWCursorPosCallback;

public class MouseHandler extends GLFWCursorPosCallback{
	double prevX;
	double prevY;
	double deltaX;
	double deltaY;
	boolean tracking;
	public MouseHandler() {
		prevX = 0;
		prevY = 0;
		deltaX = 0;
		deltaY = 0;
		tracking = true;
	}

	@Override
	public void invoke(long window, double xpos, double ypos) {
		if(tracking) {
			deltaX += xpos - prevX;
			deltaY += ypos - prevY;
		}
		prevX = xpos;
		prevY = ypos;
	}
	
	public double getDeltaX() {
		double ret = deltaX;
		deltaX = 0;
		return ret;
	}
	
	public void clearDelta() {
		deltaX = 0;
		deltaY = 0;
	}
	
	public double getDeltaY() {
		double ret = deltaY;
		deltaY = 0;
		return ret;
	}

	public void disableTracking() {
		tracking = false;
		deltaX = 0;
		deltaY = 0;
	}
	
	public void enableTracking() {
		tracking = true;
	}
	
	public boolean isTracking() {
		return tracking;
	}
}
