import jrtr.*;
import javax.swing.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import javax.vecmath.*;

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
	    int lod = 3;
		int num = (1 << lod)+1;
		float fractal[][][] = new float[num][num][3];
		FractalTerrain height = new FractalTerrain(lod, 0.5f, 10);
		
		// init dots of fractal
		for (int i=0; i<fractal.length; i++) {
			for (int j=0;j<fractal[i].length; j++) {
				fractal[i][j][0]=i;
				fractal[i][j][1]=j;
				fractal[i][j][2]=height.getAltitude(i, j);
			}
		}
		
		float fractalPoints[] = new float[num*num*3];
		int index =0;
		for (int i=0;i<fractal.length;i++) {
		    for (int j=0; j<fractal[i].length;j++) {
		        for (int k=0; k<3; k++){
		            fractalPoints[index] = fractal[i][j][k];
		            index++;
		        }
		    }
		}
		
		
		// create triangles 
		int indicesSquare[][] = new int[2*(num-1)*(num-1)][3];
		
		index = 0;
		for (int i=0;i<num-1;i++) {
		    for (int j=0;j<num-1;j++) {
		        indicesSquare[index][0]=j+i*num;
		        indicesSquare[index][1]=j+1+i*num;
		        indicesSquare[index][2]=j+num+i*num;

		        index++;
		    
		        indicesSquare[index][0]=j +i*num+1;
		        indicesSquare[index][1]=j+num+1 +i*num;
		        indicesSquare[index][2]=j+num +i*num;
		        
		        index++;
		    }
		}
		    
		int indices[] = new int[2*(num-1)*(num-1)*3];
		
		index=0;
		for (int i=0;i<2*(num-1)*(num-1);i++) {
		    for (int j=0;j<3;j++) {
		        indices[index]=indicesSquare[i][j];
		        index++;
		    }
		}
		
		
		float color[] = new float[fractalPoints.length];
		index=0;
		for (int i=0;i<num;i++) {
		    for (int j=0;j<num;j++) {
		        Color3f col = height.getColor(i, j);
		        color[index]=col.getX();
		        color[++index]=col.getY();
		        color[++index]=col.getZ();
		        index++;
		    }
		}
		
		

		// Construct a data structure that stores the vertices, their
		// attributes, and the triangle mesh connectivity
		VertexData vertexData = new VertexData(num*num);
		vertexData.addElement(color, VertexData.Semantic.COLOR, 3);
		vertexData.addElement(fractalPoints, VertexData.Semantic.POSITION, 3);

		vertexData.addIndices(indices);
				
		// Make a scene manager and add the object
		sceneManager = new SimpleSceneManager();
		shape = new Shape(vertexData);
		sceneManager.addShape(shape);
		Camera camera = sceneManager.getCamera();
		Frustum frustum = sceneManager.getFrustum();
		camera.setCenterOfProjection(new Vector3f(-5,20,20));
        camera.setLookAtPoint(new Vector3f(-5,0,0));
        camera.setUpVector(new Vector3f(0,1,0));
        frustum.setAspectRatio(1);
        frustum.setFarPlane(100);
        frustum.setNearPlane(1);
        frustum.setVertFOV(60);
		

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
