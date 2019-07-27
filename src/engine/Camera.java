package engine;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import game.Player;

public class Camera {
	public float fov = 70;
	public float aspectRatio = 1;
	
	public Vector3f position;
	public Vector3f rotation;
	
	public Camera(Vector3f position, Vector3f rotation) {
		this.position = new Vector3f(position);
		this.rotation = new Vector3f(rotation);
		
	}
		
	Matrix4f projectionViewMatrix() {
		Vector3f negPos = new Vector3f();
		position.negate(negPos);
		Matrix4f m = new Matrix4f()
				.perspective((float) Math.toRadians(fov), aspectRatio, 0.01f, 1000.0f)
				.rotateX((float)Math.toRadians(rotation.x))
				.rotateY((float)Math.toRadians(rotation.y))
				.rotateZ((float)Math.toRadians(rotation.z))
				.translate(negPos);

		return m;
	}
	
}
