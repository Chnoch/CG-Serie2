

package jrtr;

import javax.vecmath.Matrix4f;

/**
 * Stores the specification of a viewing frustum, or a viewing volume. The
 * viewing frustum is represented by a 4x4 projection matrix. You will extend
 * this class to construct the projection matrix from intuitive parameters.
 * <p>
 * A scene manager (see {@link SceneManagerInterface},
 * {@link SimpleSceneManager}) stores a frustum.
 */
public class Frustum {

    private Matrix4f projectionMatrix;
    private float nearPlane, farPlane, aspectRatio, vertFOV;

    /**
     * Construct a default viewing frustum. The frustum is given by a default
     * 4x4 projection matrix.
     */
    public Frustum() {
        projectionMatrix = new Matrix4f();
        // Aspect Ratio is 1 on init
        this.aspectRatio = 1;
        // some calculations done on the formula
        this.vertFOV = 60;
        // some calcs as well
        this.nearPlane = 1;
        this.farPlane = 100;
        this.updateFrustum();
    }

    /**
     * Return the 4x4 projection matrix, which is used for example by the
     * renderer.
     * @return the 4x4 projection matrix
     */
    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public float getNearPlane() {
        return nearPlane;
    }

    public void setNearPlane(float nearPlane) {
        this.nearPlane = nearPlane;
        this.updateFrustum();
    }

    public float getFarPlane() {
        return farPlane;
    }

    public void setFarPlane(float farPlane) {
        this.farPlane = farPlane;
        this.updateFrustum();
    }

    public float getAspectRatio() {
        return aspectRatio;
    }

    public void setAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
        updateFrustum();
    }

    public float getVertFOV() {
        return vertFOV;
    }

    public void setVertFOV(float vertFOV) {
        this.vertFOV = vertFOV;
        updateFrustum();
    }

    private void updateFrustum() {
        final float DEG2RAD = 3.14159265f / 180;
    	float temp = (float) (1 / 
    			(aspectRatio * Math.tan(vertFOV*DEG2RAD / 2)));
        this.projectionMatrix.setM00(temp);
        temp = (float) (1 / Math.tan(vertFOV*DEG2RAD / 2));
        this.projectionMatrix.setM11(temp);
        temp = (nearPlane + farPlane)
        / (nearPlane - farPlane);
        this.projectionMatrix.setM22(temp);
        temp = (2 * nearPlane * farPlane)
        / (nearPlane - farPlane);
        this.projectionMatrix.setM23(temp);
        this.projectionMatrix.setM32(-1);
    }
    
    private void makeFrustum()
    {
        final float DEG2RAD = 3.14159265f / 180;

        float tangent = (float) Math.tan(this.vertFOV/2 * DEG2RAD);   // tangent of half fovY
        float height = this.nearPlane * tangent;          // half height of near plane
        float width = this.farPlane * aspectRatio;      // half width of near plane

        this.projectionMatrix.setM00(2*this.nearPlane/width);
        this.projectionMatrix.setM11(2*this.farPlane/height);
        this.projectionMatrix.setM22(-(this.farPlane+this.nearPlane)/(this.farPlane-this.nearPlane));
        this.projectionMatrix.setM23(-(2*this.farPlane*this.nearPlane)/(this.farPlane-this.nearPlane));
        this.projectionMatrix.setM32(-1);
    }
}
