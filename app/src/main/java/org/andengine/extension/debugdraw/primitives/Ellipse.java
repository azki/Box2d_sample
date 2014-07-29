package org.andengine.extension.debugdraw.primitives;

import org.andengine.entity.primitive.DrawMode;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.primitive.Mesh;
import org.andengine.opengl.vbo.DrawType;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * 
 * @author Rodrigo Castro
 * @since 16:47:01 - 31.01.2012
 */
public class Ellipse extends PolyLine {
	// ===========================================================
	// Constants
	// ===========================================================
	
	static final int LOW_RESOLUTION = 15;
	static final int MEDIUM_RESOLUTION = 30;
	static final int HIGH_RESOLUTION = 50;
	static final int DEFAULT_RESOLUTION = HIGH_RESOLUTION;
	
	// ===========================================================
	// Fields
	// ===========================================================

	protected final int mResolution;
	
	// ===========================================================
	// Constructors
	// ===========================================================
	
	/**
	 * Uses a default {@link org.andengine.entity.primitive.vbo.HighPerformanceMeshVertexBufferObject} in {@link org.andengine.opengl.vbo.DrawType#STATIC} with the {@link org.andengine.opengl.vbo.attribute.VertexBufferObjectAttribute}s: {@link org.andengine.entity.primitive.Mesh#VERTEXBUFFEROBJECTATTRIBUTES_DEFAULT}.
	 */
	public Ellipse(final float pX, final float pY, final float pRadiusA, final float pRadiusB, final VertexBufferObjectManager pVertexBufferObjectManager) {
		this(pX, pY, pRadiusA, pRadiusB, Line.LINE_WIDTH_DEFAULT, pVertexBufferObjectManager);
	}
	
	/**
	 * Uses a default {@link org.andengine.entity.primitive.vbo.HighPerformanceMeshVertexBufferObject} in {@link org.andengine.opengl.vbo.DrawType#STATIC} with the {@link org.andengine.opengl.vbo.attribute.VertexBufferObjectAttribute}s: {@link org.andengine.entity.primitive.Mesh#VERTEXBUFFEROBJECTATTRIBUTES_DEFAULT}.
	 */
	public Ellipse(final float pX, final float pY, final float pRadiusA, final float pRadiusB, final float pLineWidth, final VertexBufferObjectManager pVertexBufferObjectManager) {
		this(pX, pY, pRadiusA, pRadiusB, pLineWidth, DEFAULT_RESOLUTION, pVertexBufferObjectManager);
	}
	
	/**
	 * Uses a default {@link org.andengine.entity.primitive.vbo.HighPerformanceMeshVertexBufferObject} in {@link org.andengine.opengl.vbo.DrawType#STATIC} with the {@link org.andengine.opengl.vbo.attribute.VertexBufferObjectAttribute}s: {@link org.andengine.entity.primitive.Mesh#VERTEXBUFFEROBJECTATTRIBUTES_DEFAULT}.
	 */
	public Ellipse(final float pX, final float pY, final float pRadiusA, final float pRadiusB, final float pLineWidth, final int pResolution, final VertexBufferObjectManager pVertexBufferObjectManager) {
		this(pX, pY, pRadiusA, pRadiusB, pLineWidth, pResolution, pVertexBufferObjectManager, DrawMode.LINE_LOOP);
	}
	
	public Ellipse(final float pX, final float pY, final float pRadiusA, final float pRadiusB, final float pLineWidth, final int pResolution, final VertexBufferObjectManager pVertexBufferObjectManager, DrawMode pDrawMode) {
		this(pX, pY, pRadiusA, pRadiusB, pLineWidth, pResolution, pVertexBufferObjectManager, pDrawMode, DrawType.STATIC);
	}


	/**
	 * Uses a default {@link org.andengine.entity.primitive.vbo.HighPerformanceMeshVertexBufferObject} with the {@link org.andengine.opengl.vbo.attribute.VertexBufferObjectAttribute}s: {@link org.andengine.entity.primitive.Rectangle#VERTEXBUFFEROBJECTATTRIBUTES_DEFAULT}.
	 */
	public Ellipse(final float pX, final float pY, final float pRadiusA, final float pRadiusB, final float pLineWidth, final int pResolution, final VertexBufferObjectManager pVertexBufferObjectManager, DrawMode pDrawMode, final DrawType pDrawType) {
		super(pX, pY, buildEllipseVertices(pRadiusA, pRadiusB, pResolution), pLineWidth, pVertexBufferObjectManager, pDrawMode, pDrawType);
		
		mResolution = pResolution;
	}
	
	// ===========================================================
	// Getter & Setter
	// ===========================================================

	/**
	 * 
	 * @param pRadiusA
	 * @param pRadiusB
	 * @return 	true if vertices were correctly updated
	 * 			false otherwise
	 */
	/*
	public boolean setRadius( float pRadiusA, float pRadiusB )
	{
		return this.updateVertices(buildEllipseVertices(pRadiusA, pRadiusB, mResolution));
	}
	*/
	
	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	private static float[] buildEllipseVertices(float pRadiusA, float pRadiusB, int pResolution) {

		float[] vertices = new float[VERTEX_SIZE * pResolution];
		
		for( int i = 0; i < pResolution; i++)
		{
			double theta = 2. * Math.PI * (double)i / (double) pResolution;
			float x = (float) ( (double)pRadiusA * Math.cos( theta ));
			float y = (float) ( (double)pRadiusB * Math.sin( theta ));
			
			vertices[(i * Mesh.VERTEX_SIZE) + Mesh.VERTEX_INDEX_X] = x;
			vertices[(i * Mesh.VERTEX_SIZE) + Mesh.VERTEX_INDEX_Y] = y;
		}
				
		return vertices;
	}
	
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}