package org.andengine.extension.debugdraw.primitives;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.primitive.DrawMode;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.primitive.Mesh;
import org.andengine.opengl.util.GLState;
import org.andengine.opengl.vbo.DrawType;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * 
 * @author Rodrigo Castro
 * @since 22:54:17 - 30.01.2012
 */
public class PolyLine extends Mesh {
	// ===========================================================
	// Constants
	// ===========================================================
	
	// ===========================================================
	// Fields
	// ===========================================================
	
	private float mLineWidth;

	// ===========================================================
	// Constructors
	// ===========================================================
	
	/**
	 * Uses a default {@link org.andengine.entity.primitive.vbo.HighPerformanceMeshVertexBufferObject} in {@link org.andengine.opengl.vbo.DrawType#STATIC} with the {@link org.andengine.opengl.vbo.attribute.VertexBufferObjectAttribute}s: {@link org.andengine.entity.primitive.Mesh#VERTEXBUFFEROBJECTATTRIBUTES_DEFAULT}.
	 */
	public PolyLine(final float pX, final float pY, final float[] pVertexX, final float[] pVertexY, final VertexBufferObjectManager pVertexBufferObjectManager) {
		this(pX, pY, pVertexX, pVertexY, Line.LINE_WIDTH_DEFAULT, pVertexBufferObjectManager);
	}
	
	/**
	 * Uses a default {@link org.andengine.entity.primitive.vbo.HighPerformanceMeshVertexBufferObject} in {@link org.andengine.opengl.vbo.DrawType#STATIC} with the {@link org.andengine.opengl.vbo.attribute.VertexBufferObjectAttribute}s: {@link org.andengine.entity.primitive.Mesh#VERTEXBUFFEROBJECTATTRIBUTES_DEFAULT}.
	 */
	public PolyLine(final float pX, final float pY, final float[] pVertexX, final float[] pVertexY, final float pLineWidth, final VertexBufferObjectManager pVertexBufferObjectManager) {
		this(pX, pY, pVertexX, pVertexY, pLineWidth, pVertexBufferObjectManager, DrawMode.LINE_LOOP);
	}
	
	/**
	 * Uses a default {@link org.andengine.entity.primitive.vbo.HighPerformanceMeshVertexBufferObject} in {@link org.andengine.opengl.vbo.DrawType#STATIC} with the {@link org.andengine.opengl.vbo.attribute.VertexBufferObjectAttribute}s: {@link org.andengine.entity.primitive.Mesh#VERTEXBUFFEROBJECTATTRIBUTES_DEFAULT}.
	 */
	public PolyLine(final float pX, final float pY, final float[] pVertexX, final float[] pVertexY, final float pLineWidth, final VertexBufferObjectManager pVertexBufferObjectManager, DrawMode pDrawMode) {
		this(pX, pY, pVertexX, pVertexY, pLineWidth, pVertexBufferObjectManager, pDrawMode, DrawType.STATIC);
	}


	/**
	 * Uses a default {@link org.andengine.entity.primitive.vbo.HighPerformanceMeshVertexBufferObject} with the {@link org.andengine.opengl.vbo.attribute.VertexBufferObjectAttribute}s: {@link org.andengine.entity.primitive.Mesh#VERTEXBUFFEROBJECTATTRIBUTES_DEFAULT}.
	 */
	public PolyLine(final float pX, final float pY, final float[] pVertexX, final float[] pVertexY, final float pLineWidth, final VertexBufferObjectManager pVertexBufferObjectManager, DrawMode pDrawMode, final DrawType pDrawType) {
		this(pX, pY, buildVertexList(pVertexX, pVertexY), pLineWidth, pVertexBufferObjectManager, pDrawMode, pDrawType);
	}
	
	/**
	 * Uses a default {@link org.andengine.entity.primitive.vbo.HighPerformanceMeshVertexBufferObject} with the {@link org.andengine.opengl.vbo.attribute.VertexBufferObjectAttribute}s: {@link org.andengine.entity.primitive.Mesh#VERTEXBUFFEROBJECTATTRIBUTES_DEFAULT}.
	 */
	public PolyLine(final float pX, final float pY, final float[] pBufferData, final float pLineWidth, final VertexBufferObjectManager pVertexBufferObjectManager, DrawMode pDrawMode, final DrawType pDrawType) {
		super(pX, pY, pBufferData, pBufferData.length / VERTEX_SIZE, pDrawMode, pVertexBufferObjectManager, pDrawType);
		
		mLineWidth = pLineWidth;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	
	public float getLineWidth()
	{
		return mLineWidth;
	}
	
	public void setLineWidth( float pLineWidth )
	{
		mLineWidth = pLineWidth;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	
	@Override
	protected void preDraw(final GLState pGLState, final Camera pCamera) {
		super.preDraw(pGLState, pCamera);

		pGLState.lineWidth(this.mLineWidth);

	}

	// ===========================================================
	// Methods
	// ===========================================================
	
	/**
	 * 
	 * @param pVertexX
	 * @param pVertexY
	 * @return 	true if vertices were correctly updated
	 * 			false otherwise
	 */
	public void updateVertices( float[] pVertexX, float[] pVertexY )
	{
		assert( pVertexX.length == pVertexY.length );
		updateVertexList(pVertexX, pVertexY, getBufferData());
		onUpdateVertices();
	}

	protected static float[] buildVertexList(float[] pVertexX, float[] pVertexY) {
		assert (pVertexX.length == pVertexY.length);

		float[] bufferData = new float[VERTEX_SIZE * pVertexX.length];
		updateVertexList(pVertexX, pVertexY, bufferData);
		return bufferData;
	}

	protected static void updateVertexList(float[] pVertexX, float[] pVertexY,
			float[] pBufferData) {
		for (int i = 0; i < pVertexX.length; i++) {
			pBufferData[(i * Mesh.VERTEX_SIZE) + Mesh.VERTEX_INDEX_X] = pVertexX[i];
			pBufferData[(i * Mesh.VERTEX_SIZE) + Mesh.VERTEX_INDEX_Y] = pVertexY[i];
		}
	}
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
