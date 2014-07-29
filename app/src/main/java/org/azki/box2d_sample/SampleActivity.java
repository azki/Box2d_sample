package org.azki.box2d_sample;

import android.hardware.SensorManager;
import android.util.DisplayMetrics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.IEntity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.shape.IShape;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.debugdraw.DebugRenderer;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.bitmap.AssetBitmapTexture;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.adt.color.Color;

import java.io.IOException;

import static org.andengine.extension.physics.box2d.util.constants.PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;

public class SampleActivity extends SimpleBaseGameActivity implements IAccelerationListener, IOnSceneTouchListener, IOnAreaTouchListener {

    // ===========================================================
    // Constants
    // ===========================================================
    private static final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);

    private TiledTextureRegion mBoxFaceTextureRegion;
    private TiledTextureRegion mCircleFaceTextureRegion;
    private TiledTextureRegion mTriangleFaceTextureRegion;
    private TiledTextureRegion mHexagonFaceTextureRegion;

    private int mTouchCount = 0;

    private PhysicsWorld mPhysicsWorld;
    private MouseJoint mMouseJointActive;
    private Body mGroundBody;

    private float mGravityX;
    private float mGravityY;

    private Scene mScene;
    private int cameraWidth;
    private int cameraHeight;

    @Override
    public EngineOptions onCreateEngineOptions() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int cameraZoom = 2;
        cameraWidth = displayMetrics.widthPixels / cameraZoom;
        cameraHeight = displayMetrics.heightPixels / cameraZoom;
        final Camera camera = new Camera(0, 0, cameraWidth, cameraHeight);

        return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(cameraWidth, cameraHeight), camera);
    }

    @Override
    public void onCreateResources() throws IOException {
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

        AssetBitmapTexture boxFaceTexture = new AssetBitmapTexture(this.getTextureManager(), this.getAssets(), "gfx/face_box_tiled.png", TextureOptions.BILINEAR);
        this.mBoxFaceTextureRegion = TextureRegionFactory.extractTiledFromTexture(boxFaceTexture, 2, 1);
        boxFaceTexture.load();

        AssetBitmapTexture circleFaceTexture = new AssetBitmapTexture(this.getTextureManager(), this.getAssets(), "gfx/face_circle_tiled.png", TextureOptions.BILINEAR);
        this.mCircleFaceTextureRegion = TextureRegionFactory.extractTiledFromTexture(circleFaceTexture, 2, 1);
        circleFaceTexture.load();

        AssetBitmapTexture triangleFaceTexture = new AssetBitmapTexture(this.getTextureManager(), this.getAssets(), "gfx/face_triangle_tiled.png", TextureOptions.BILINEAR);
        this.mTriangleFaceTextureRegion = TextureRegionFactory.extractTiledFromTexture(triangleFaceTexture, 2, 1);
        triangleFaceTexture.load();

        AssetBitmapTexture hexagonFaceTexture = new AssetBitmapTexture(this.getTextureManager(), this.getAssets(), "gfx/face_hexagon_tiled.png", TextureOptions.BILINEAR);
        this.mHexagonFaceTextureRegion = TextureRegionFactory.extractTiledFromTexture(hexagonFaceTexture, 2, 1);
        hexagonFaceTexture.load();
    }

    @Override
    public Scene onCreateScene() {
        this.mEngine.registerUpdateHandler(new FPSLogger());
        this.mEngine.enableVibrator(this);

        this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), false);
        this.mGroundBody = this.mPhysicsWorld.createBody(new BodyDef());

        this.mScene = new Scene();
        this.mScene.getBackground().setColor(Color.BLACK);
        this.mScene.setOnSceneTouchListener(this);

        DebugRenderer debug = new DebugRenderer(mPhysicsWorld, getVertexBufferObjectManager());
        this.mScene.attachChild(debug);

        final VertexBufferObjectManager vertexBufferObjectManager = this.getVertexBufferObjectManager();
        final Rectangle ground = new Rectangle(cameraWidth / 2, 1, cameraWidth, 2, vertexBufferObjectManager);
        final Rectangle roof = new Rectangle(cameraWidth / 2, cameraHeight - 1, cameraWidth, 2, vertexBufferObjectManager);
        final Rectangle left = new Rectangle(1, cameraHeight / 2, 1, cameraHeight, vertexBufferObjectManager);
        final Rectangle right = new Rectangle(cameraWidth - 1, cameraHeight / 2, 2, cameraHeight, vertexBufferObjectManager);

        final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
        PhysicsFactory.createBoxBody(this.mPhysicsWorld, ground, BodyDef.BodyType.StaticBody, wallFixtureDef);
        PhysicsFactory.createBoxBody(this.mPhysicsWorld, roof, BodyDef.BodyType.StaticBody, wallFixtureDef);
        PhysicsFactory.createBoxBody(this.mPhysicsWorld, left, BodyDef.BodyType.StaticBody, wallFixtureDef);
        PhysicsFactory.createBoxBody(this.mPhysicsWorld, right, BodyDef.BodyType.StaticBody, wallFixtureDef);

        this.mScene.attachChild(ground);
        this.mScene.attachChild(roof);
        this.mScene.attachChild(left);
        this.mScene.attachChild(right);

        this.mScene.registerUpdateHandler(this.mPhysicsWorld);

        this.mScene.setOnAreaTouchListener(this);

        return this.mScene;
    }

    @Override
    public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final ITouchArea pTouchArea, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
        if (pSceneTouchEvent.isActionDown()) {
            final IEntity entity = (IEntity) pTouchArea;
            /*
             * If we have a active MouseJoint, we are just moving it around
			 * instead of creating a second one.
			 */
            if (this.mMouseJointActive == null) {
                this.mEngine.vibrate(20);
                this.mMouseJointActive = this.createMouseJoint(entity, pTouchAreaLocalX, pTouchAreaLocalY);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
        if (this.mPhysicsWorld != null) {
            switch (pSceneTouchEvent.getAction()) {
                case TouchEvent.ACTION_DOWN:
                    this.mTouchCount++;

                    this.addFace(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
                    return true;
                case TouchEvent.ACTION_MOVE:
                    if (this.mMouseJointActive != null) {
                        final Vector2 vec = Vector2Pool.obtain(pSceneTouchEvent.getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, pSceneTouchEvent.getY() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
                        this.mMouseJointActive.setTarget(vec);
                        Vector2Pool.recycle(vec);
                    } else {
                        this.addFace(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
                    }
                    return true;
                case TouchEvent.ACTION_UP:
                    stopMouseJoint();
                    return true;
            }
        }
        return false;
    }

    private void stopMouseJoint() {
        if (this.mMouseJointActive != null) {
            this.mPhysicsWorld.destroyJoint(this.mMouseJointActive);
            this.mMouseJointActive = null;
        }
    }

    @Override
    public void onAccelerationAccuracyChanged(final AccelerationData pAccelerationData) {
    }

    @Override
    public void onAccelerationChanged(final AccelerationData pAccelerationData) {
        this.mGravityX = pAccelerationData.getX();
        this.mGravityY = pAccelerationData.getY();

        final Vector2 gravity = Vector2Pool.obtain(this.mGravityX, this.mGravityY);
        this.mPhysicsWorld.setGravity(gravity);
        Vector2Pool.recycle(gravity);
    }

    @Override
    public void onResumeGame() {
        super.onResumeGame();

        this.enableAccelerationSensor(this);
    }

    @Override
    public void onPauseGame() {
        super.onPauseGame();

        this.disableAccelerationSensor();
        stopMouseJoint();
    }

    // ===========================================================
    // Methods
    // ===========================================================

    public MouseJoint createMouseJoint(final IEntity pFace, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
        final Body body = (Body) pFace.getUserData();
        final MouseJointDef mouseJointDef = new MouseJointDef();

        final Vector2 localPoint = Vector2Pool.obtain((pTouchAreaLocalX - pFace.getWidth() * 0.5f) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, (pTouchAreaLocalY - pFace.getHeight() * 0.5f) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
        this.mGroundBody.setTransform(localPoint, 0);

        mouseJointDef.bodyA = this.mGroundBody;
        mouseJointDef.bodyB = body;
        mouseJointDef.dampingRatio = 0.95f;
        mouseJointDef.frequencyHz = 30;
        mouseJointDef.maxForce = (200.0f * body.getMass());
        mouseJointDef.collideConnected = true;

        mouseJointDef.target.set(body.getWorldPoint(localPoint));
        Vector2Pool.recycle(localPoint);

        return (MouseJoint) this.mPhysicsWorld.createJoint(mouseJointDef);
    }

    private AnimatedSprite addFace(final float pX, final float pY) {
        final AnimatedSprite animatedSprite;
        final Body body;

        if (this.mTouchCount % 4 == 1) {
            animatedSprite = new AnimatedSprite(pX, pY, this.mBoxFaceTextureRegion, this.getVertexBufferObjectManager());
            body = PhysicsFactory.createBoxBody(this.mPhysicsWorld, animatedSprite, BodyType.DynamicBody, FIXTURE_DEF);
        } else if (this.mTouchCount % 4 == 2) {
            animatedSprite = new AnimatedSprite(pX, pY, this.mCircleFaceTextureRegion, this.getVertexBufferObjectManager());
            body = PhysicsFactory.createCircleBody(this.mPhysicsWorld, animatedSprite, BodyType.DynamicBody, FIXTURE_DEF);
        } else if (this.mTouchCount % 4 == 3) {
            animatedSprite = new AnimatedSprite(pX, pY, this.mHexagonFaceTextureRegion, this.getVertexBufferObjectManager());
            body = createHexagonBody(this.mPhysicsWorld, animatedSprite, BodyType.DynamicBody, FIXTURE_DEF);
        } else {
            animatedSprite = new AnimatedSprite(pX, pY, this.mTriangleFaceTextureRegion, this.getVertexBufferObjectManager());
            body = createTriangleBody(this.mPhysicsWorld, animatedSprite, BodyType.DynamicBody, FIXTURE_DEF);
        }

        this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(animatedSprite, body, true, true));

        animatedSprite.animate(new long[]{200, 200}, 0, 1, true);
        animatedSprite.setUserData(body);
        this.mScene.registerTouchArea(animatedSprite);
        this.mScene.attachChild(animatedSprite);
        return animatedSprite;
    }

    private void jumpFace(final AnimatedSprite face) {
        final Body faceBody = (Body) face.getUserData();

        final Vector2 velocity = Vector2Pool.obtain(this.mGravityX * -50, this.mGravityY * -50);
        faceBody.setLinearVelocity(velocity);
        Vector2Pool.recycle(velocity);
    }

    /**
     * Creates a {@link Body} based on a {@link IShape} in the form of a triangle:
     * <pre>
     *  /\
     * /__\
     * </pre>
     */
    private static Body createTriangleBody(final PhysicsWorld pPhysicsWorld, final IShape pAreaShape, final BodyType pBodyType, final FixtureDef pFixtureDef) {
        /* Remember that the vertices are relative to the center-coordinates of the Shape. */
        final float halfWidth = pAreaShape.getWidthScaled() * 0.5f / PIXEL_TO_METER_RATIO_DEFAULT;
        final float halfHeight = pAreaShape.getHeightScaled() * 0.5f / PIXEL_TO_METER_RATIO_DEFAULT;

        final float centerX = 0;
        final float top = -halfHeight;
        final float bottom = halfHeight;
        final float left = -halfHeight;
        final float right = halfWidth;

        // NOTE: 좌표가 왜 상하 반전으로 적용되는 건지 잘 모르겠음 ㅡ.ㅡ.. 아오...
        final Vector2[] vertices = {
                new Vector2(left, top),
                new Vector2(right, top),
                new Vector2(centerX, bottom)
        };

        return PhysicsFactory.createPolygonBody(pPhysicsWorld, pAreaShape, vertices, pBodyType, pFixtureDef);
    }

    /**
     * Creates a {@link Body} based on a {@link IShape} in the form of a hexagon:
     * <pre>
     *  /\
     * /  \
     * |  |
     * |  |
     * \  /
     *  \/
     * </pre>
     */
    private static Body createHexagonBody(final PhysicsWorld pPhysicsWorld, final IShape pAreaShape, final BodyType pBodyType, final FixtureDef pFixtureDef) {
        /* Remember that the vertices are relative to the center-coordinates of the Shape. */
        final float halfWidth = pAreaShape.getWidthScaled() * 0.5f / PIXEL_TO_METER_RATIO_DEFAULT;
        final float halfHeight = pAreaShape.getHeightScaled() * 0.5f / PIXEL_TO_METER_RATIO_DEFAULT;

		/* The top and bottom vertex of the hexagon are on the bottom and top of hexagon-sprite. */
        final float top = -halfHeight;
        final float bottom = halfHeight;

        final float centerX = 0;

		/* The left and right vertices of the heaxgon are not on the edge of the hexagon-sprite, so we need to inset them a little. */
        final float left = -halfWidth + 2.5f / PIXEL_TO_METER_RATIO_DEFAULT;
        final float right = halfWidth - 2.5f / PIXEL_TO_METER_RATIO_DEFAULT;
        final float higher = top + 8.25f / PIXEL_TO_METER_RATIO_DEFAULT;
        final float lower = bottom - 8.25f / PIXEL_TO_METER_RATIO_DEFAULT;

        final Vector2[] vertices = {
                new Vector2(centerX, top),
                new Vector2(right, higher),
                new Vector2(right, lower),
                new Vector2(centerX, bottom),
                new Vector2(left, lower),
                new Vector2(left, higher)
        };

        return PhysicsFactory.createPolygonBody(pPhysicsWorld, pAreaShape, vertices, pBodyType, pFixtureDef);
    }
}
