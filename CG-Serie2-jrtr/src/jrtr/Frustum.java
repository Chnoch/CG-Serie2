package jrtr;

import javax.vecmath.Matrix4f;

/**
 * Stores the specification of a viewing frustum, or a viewing
 * volume. The viewing frustum is represented by a 4x4 projection
 * matrix. You will extend this class to construct the projection 
 * matrix from intuitive parameters.
 * <p>
 * A scene manager (see {@link SceneManagerInterface}, {@link SimpleSceneManager}) 
 * stores a frustum.
 */
public class Frustum {

	private Matrix4f projectionMatrix;
	private float nearPlane, farPlane, aspectRatio, vertFOV;
	

    /**
	 * Construct a default viewing frustum. The frustum is given by a 
	 * default 4x4 projection matrix.
	 */
	public Frustum()
	{
		projectionMatrix = new Matrix4f();
		float f[] = {2.f, 0.f, 0.f, 0.f, 
					 0.f, 2.f, 0.f, 0.f,
				     0.f, 0.f, -1.02f, -2.02f,
				     0.f, 0.f, -1.f, 0.f};
		projectionMatrix.set(f);
		// Aspect Ratio is 1 on init
		this.aspectRatio = 1;
		// some calculations done on the formula
		this.vertFOV = 0.3927298f;
		// some calcs as well
		this.nearPlane = 1;
		this.farPlane = 101;
	}
	
	/**
	 * Return the 4x4 projection matrix, which is used for example by 
	 * the renderer.
	 * 
	 * @return the 4x4 projection matrix
	 */
	public Matrix4f getProjectionMatrix()
	{
		return projectionMatrix;
	}
	
	public float getNearPlane() {
	    return nearPlane;
	}
	
	public void setNearPlane(float nearPlane) {
	    this.nearPlane = nearPlane;
	    this.setPlanes();
	}
	
	public float getFarPlane() {
	    return farPlane;
	}
	
	
	public void setFarPlane(float farPlane) {
	    this.farPlane = farPlane;
	    this.setPlanes();
	}

	private void setPlanes() {
	    this.projectionMatrix.setM32((nearPlane+farPlane)/(nearPlane-farPlane));
	    this.projectionMatrix.setM33((2*nearPlane*farPlane)/(nearPlane-farPlane));
	}
	
	public float getAspectRatio() {
	    return aspectRatio;
	}
	
	public void setAspectRatio(float aspectRatio) {
	    this.aspectRatio = aspectRatio;
	    this.setAspectAndFOV();
	}
	
	public float getVertFOV() {
	    return vertFOV;
	}
	
	public void setVertFOV(float vertFOV) {
	    this.vertFOV = vertFOV;
	    this.setAspectAndFOV();
	}
	
	private void setAspectAndFOV() {
	    this.projectionMatrix.setM00((float) (1/(aspectRatio*Math.tan(vertFOV/2))));
        this.projectionMatrix.setM11((float) (1/ Math.tan(vertFOV/2)));
	}
}
