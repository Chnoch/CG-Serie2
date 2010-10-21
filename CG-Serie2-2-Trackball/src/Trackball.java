import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.vecmath.*;

import jrtr.Shape;

/** A trackball. Allows interactive rotation of 3d views or objects */

public class Trackball extends MouseAdapter implements MouseMotionListener {
    private final float trackballSize;
    private int prevX = 0;
    private int prevY = 0;
    private int startX = 0;
    private int startY = 0;
    private Quat4f curQuat = buildQuaternion(0.0f, 0.0f, 0.0f, 0.0f);

    private Quat4f lastQuat = curQuat;
    private boolean spin = false;
    private Shape shape;
    private Component component;

    public Trackball(Shape shape) {
        trackballSize = 0.8f;
        this.shape = shape;
    }

    /** specify component virtual trackball is in */
    public void listen(Component component) {
        this.component = component;
        this.component.addMouseListener(this);
        this.component.addMouseMotionListener(this);
    }

    /** return rotation Matrix representing current rotation of trackball */
    public Matrix4f getRotMatrix() {
        Matrix4f rotMat = buildMatrix(curQuat);
        if (spin) {
            curQuat.add(lastQuat);
        }
        return rotMat;
    }

    // deal with Mouse events
    final static int EPS2 = 5; // only spin if mouse moved this far

    public void mouseReleased(MouseEvent evt) {
        int dx = startX - evt.getX();
        int dy = startY - evt.getY();
        spin = (dx * dx + dy * dy > EPS2);
    }

    public void mousePressed(MouseEvent evt) {
        startX = prevX = evt.getX();
        startY = prevY = evt.getY();
        spin = false;
    }

    public void mouseMoved(MouseEvent evt) {
    }

    public void mouseDragged(MouseEvent evt) {
        int aWidth = evt.getComponent().getSize().width;
        int aHeight = evt.getComponent().getSize().height;
        int currX = evt.getX();
        int currY = evt.getY();

        lastQuat = buildQuaternion((float) (2.0f * prevX - aWidth)
                / (float) aWidth, (float) (aHeight - 2.0f * prevY)
                / (float) aHeight, (float) (2.0f * currX - aWidth)
                / (float) aWidth, (float) (aHeight - 2.0f * currY)
                / (float) aHeight);
        curQuat.add(lastQuat);
        prevX = currX;
        prevY = currY;

        updateTransformation();
    }

    private void updateTransformation() {
        Matrix4f t = shape.getTransformation();
        Matrix4f temp = this.getRotMatrix();
        temp.mul(t);
        shape.setTransformation(temp);
        this.component.repaint();
    }

    public Quat4f buildQuaternion(float p1x, float p1y, float p2x, float p2y) {
        Vector3f a = new Vector3f(); // Axis of rotation
        float phi; // rotation angle
        Vector3f p1 = new Vector3f();
        Vector3f p2 = new Vector3f();
        Vector3f d = new Vector3f();
        float t;

        if (p1x == p2x && p1y == p2y) {
            /* Zero rotation */
            float[] q = { 0.0f, 0.0f, 0.0f, 1.0f };
            return new Quat4f(q);
        }

       
        p1.set(p1x, p1y, projectToSphere(trackballSize, p1x, p1y));
        p2.set(p2x, p2y, projectToSphere(trackballSize, p2x, p2y));

       
        a.cross(p1, p2);

        d.sub(p1, p2);
        t = d.length() / (2.0f * trackballSize);

       
        if (t > 1.0)
            t = 1.0f;
        if (t < -1.0)
            t = -1.0f;
        phi = 2.0f * (float) Math.asin(t);

        return axisToQuat(a, phi);
    }

 
    public Quat4f axisToQuat(Vector3f axis, float theta) {
        Quat4f axisVec = new Quat4f();
        Vector4f temp = new Vector4f(axis);
        temp.setW((float) Math.cos(theta / 2.f));

        axisVec.set(temp);

        axisVec.normalize();
        axisVec.scale((float) Math.sin(theta / 2.0));
        return axisVec;
    }

    public float projectToSphere(float r, float x, float y) {
        float z;
        float d = (float) Math.sqrt(x * x + y * y);
        if (d < r * 0.70710678118654752440f) { /* Inside sphere */
            z = (float) Math.sqrt(r * r - d * d);
        } else { /* On hyperbola */
            float t = r / 1.41421356237309504880f;
            z = t * t / d;
        }
        return z;
    }


    public Matrix4f buildMatrix(Quat4f q) {
        float[] mat = new float[16];

        float x = q.getX();
        float y = q.getY();
        float z = q.getZ();
        float w = q.getW();

        float xx = x * x;
        float xy = x * y;
        float xz = x * z;
        float xw = x * w;

        float yy = y * y;
        float yz = y * z;
        float yw = y * w;

        float zz = z * z;
        float zw = z * w;

        mat[0] = 1 - 2 * (yy + zz);
        mat[1] = 2 * (xy - zw);
        mat[2] = 2 * (xz + yw);

        mat[4] = 2 * (xy + zw);
        mat[5] = 1 - 2 * (xx + zz);
        mat[6] = 2 * (yz - xw);

        mat[8] = 2 * (xz - yw);
        mat[9] = 2 * (yz + xw);
        mat[10] = 1 - 2 * (xx + yy);

        mat[3] = mat[7] = mat[11] = mat[12] = mat[13] = mat[14] = 0;
        mat[15] = 1;

        return new Matrix4f(mat);
    }
}
