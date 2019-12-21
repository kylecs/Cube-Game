package misc;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.ARBShaderObjects;
import engine.Game;
import world.Chunk;

public class Util {
	public static String loadFile(String path) {
		String ret = "";
		try {
			InputStream i  = Game.get().getClass().getClassLoader().getResourceAsStream(path);
			ret = new String(i.readAllBytes());
		}catch(Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public static String getShaderLogInfo(int shaderObj) {
		return ARBShaderObjects.glGetInfoLogARB(shaderObj, ARBShaderObjects.glGetObjectParameteriARB(shaderObj, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB));
	}
	
	public static Vector3f viewRayDelta(Vector3f position, Vector3f rotation, float length) {
		Vector3f ret = new Vector3f(0, 0, length);
		
		ret.rotateX((float)Math.toRadians(-rotation.x));
		ret.rotateY((float)Math.toRadians(-rotation.y));
		
		return ret;
	}
	
	public static Vector2f getPlaneIntersection(Vector3f linePoint1, Vector3f linePoint2, Vector3f planeNormal, Vector2f planeBase) {
		Vector2f intersection = new Vector2f(0, 0);
		
		
		
		return intersection;
	}
	
	public static int getBaseCoord(int x) {
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
