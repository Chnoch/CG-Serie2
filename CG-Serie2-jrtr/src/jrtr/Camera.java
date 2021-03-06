package jrtr;

import javax.vecmath.*;

/**
 * Stores the specification of a virtual camera. You will extend this class to
 * construct a 4x4 camera matrix, i.e., the world-to- camera transform from
 * intuitive parameters.
 * 
 * A scene manager (see {@link SceneManagerInterface},
 * {@link SimpleSceneManager}) stores a camera.
 */
public class Camera {

	private Matrix4f cameraMatrix;
	private Vector3f centerOfProjection, lookAtPoint, upVector;

	/**
	 * Construct a camera with a default camera matrix. The camera matrix
	 * corresponds to the world-to-camera transform. This default matrix places
	 * the camera at (0,0,10) in world space, facing towards the origin (0,0,0)
	 * of world space, i.e., towards the negative z-axis.
	 */
	public Camera() {
		centerOfProjection = new Vector3f(0, 0, 10);
		lookAtPoint = new Vector3f(0, 0, 0);
		upVector = new Vector3f(0, 1, 0);
		cameraMatrix = new Matrix4f();
		this.updateCamera();
	}

	/**
	 * Return the camera matrix, i.e., the world-to-camera transform. For
	 * example, this is used by the renderer.
	 * 
	 * @return the 4x4 world-to-camera transform matrix
	 */
	public Matrix4f getCameraMatrix() {
		return cameraMatrix;
	}

	public Vector3f getCenterOfProjection() {
		return centerOfProjection;
	}

	public void setCenterOfProjection(Vector3f centerOfProjection) {
		this.centerOfProjection = centerOfProjection;
		this.updateCamera();
	}

	public Vector3f getLookAtPoint() {
		return lookAtPoint;
	}

	public void setLookAtPoint(Vector3f lookAtPoint) {
		this.lookAtPoint = lookAtPoint;
		this.updateCamera();
	}

	public Vector3f getUpVector() {
		return upVector;
	}

	public void setUpVector(Vector3f upVector) {
		this.upVector = upVector;
		this.updateCamera();
	}

	private void updateCamera() {
		Vector3f x = new Vector3f();
		Vector3f y = new Vector3f();
		Vector3f z = new Vector3f();
		Vector3f temp = new Vector3f(this.centerOfProjection);

		temp.sub(this.lookAtPoint);
		temp.normalize();
		z.set(temp);

		temp.set(this.upVector);
		temp.cross(temp, z);
		temp.normalize();
		x.set(temp);

		y.cross(z, x);

		Matrix4f newMatrix = new Matrix4f();
		newMatrix.setColumn(0, x.getX(), x.getY(), x.getZ(), 0);
		newMatrix.setColumn(1, y.getX(), y.getY(), y.getZ(), 0);
		newMatrix.setColumn(2, z.getX(), z.getY(), z.getZ(), 0);
		newMatrix.setColumn(3, centerOfProjection.getX(), centerOfProjection
				.getY(), centerOfProjection.getZ(), 1);
		newMatrix.invert();
		this.cameraMatrix.set(newMatrix);
	}
}
