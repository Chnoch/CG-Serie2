import jrtr.*;
import javax.swing.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import javax.vecmath.*;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Implements a simple application that opens a 3D rendering window and 
 * shows a rotating cube.
 */
public class simple
{	
	static RenderPanel renderPanel;
	static RenderContext renderContext;
	static SimpleSceneManager sceneManager;
	static Shape shape;
	static float angle;

	/**
	 * An extension of {@link GLRenderPanel} or {@link SWRenderPanel} to 
	 * provide a call-back function for initialization. 
	 */ 
	public final static class SimpleRenderPanel extends GLRenderPanel
	{
		/**
		 * Initialization call-back. We initialize our renderer here.
		 * 
		 * @param r	the render context that is associated with this render panel
		 */
		public void init(RenderContext r)
		{
			renderContext = r;
			renderContext.setSceneManager(sceneManager);
	
			// Register a timer task
		    Timer timer = new Timer();
		    angle = 0.01f;
		    timer.scheduleAtFixedRate(new AnimationTask(), 0, 10);
		}
	}

	/**
	 * A timer task that generates an animation. This task triggers
	 * the redrawing of the 3D scene every time it is executed.
	 */
	public static class AnimationTask extends TimerTask
	{
		public void run()
		{
//			// Update transformation
//    		Matrix4f t = shape.getTransformation();
//    		Matrix4f rotX = new Matrix4f();
//    		rotX.rotX(angle);
//    		Matrix4f rotY = new Matrix4f();
//    		rotY.rotY(angle);
////    		t.mul(rotX);
//    		t.mul(rotY);
//    		shape.setTransformation(t);
//    		
//    		// Trigger redrawing of the render window
//    		renderPanel.getCanvas().repaint(); 
		}
	}

	/**
	 * A mouse listener for the main window of this application. This can be
	 * used to process mouse events.
	 */
	public static class SimpleMouseListener implements MouseListener
	{
    	public void mousePressed(MouseEvent e) {}
    	public void mouseReleased(MouseEvent e) {}
    	public void mouseEntered(MouseEvent e) {}
    	public void mouseExited(MouseEvent e) {}
    	public void mouseClicked(MouseEvent e) {}
	}
	
	/**
	 * The main function opens a 3D rendering window, constructs a simple 3D
	 * scene, and starts a timer task to generate an animation.
	 */
	public static void main(String[] args)
	{		
		int n=3;
		int num = (int) (Math.pow(2, n)-1);
		float fractal[] = new float[num*num*3];
		
		
		Random ran = new Random();
		// init dots of fractal
		int x=0;
		int y=0;
		int count = 0;
		for (int i=0; i<num*3; i+=num) {
			for (int j=0;j<num*3; j+=num) {
				fractal[count++]=x;
				fractal[count++]=y;
				fractal[count++]=0;
				y++;
			}
			x++;
			y=0;
		}
		
		// create triangles 
		int indices[] = new int[(2*num-2)*(num-1)*3];
		
		int m=0;
		for (int i=0;i<indices.length;i+=6) {
			indices[i]=m;
			indices[i+1]=m+1;
			indices[i+2]=m+3;
			
			indices[i+3]=m+1;
			indices[i+4]=m+4;
			indices[i+5]=m+3;
			if (m==num-2) {
			    m+=2;
			} else {
			    m++;
			}
		}
		
		
		
		
		
				// The vertex colors
		float c[] = new float[fractal.length];
		
		boolean black=true;
		for (int i=0;i<c.length-1;i+=3) {
			if (black) {
				c[i] = 0;
				c[i+1]=0;
				c[i+2]=0;
			} else {
				c[i] = 1;
				c[i+1]=1;
				c[i+2]=1;
			}
			black = !black;
			
		}

		// Construct a data structure that stores the vertices, their
		// attributes, and the triangle mesh connectivity
		VertexData vertexData = new VertexData(fractal.length/3);
		vertexData.addElement(c, VertexData.Semantic.COLOR, 3);
		vertexData.addElement(fractal, VertexData.Semantic.POSITION, 3);

		vertexData.addIndices(indices);
				
		// Make a scene manager and add the object
		sceneManager = new SimpleSceneManager();
		shape = new Shape(vertexData);
		sceneManager.addShape(shape);

		// Make a render panel. The init function of the renderPanel
		// (see above) will be called back for initialization.
		renderPanel = new SimpleRenderPanel();
		
		// Make the main window of this application and add the renderer to it
		JFrame jframe = new JFrame("simple");
		jframe.setSize(500, 500);
		jframe.setLocationRelativeTo(null); // center of screen
		jframe.getContentPane().add(renderPanel.getCanvas());// put the canvas into a JFrame window

		// Add a mouse listener
	    jframe.addMouseListener(new SimpleMouseListener());
		   	    	    
	    jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    jframe.setVisible(true); // show window
	}
}
