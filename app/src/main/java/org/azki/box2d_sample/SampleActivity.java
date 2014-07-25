package org.azki.box2d_sample;


import android.hardware.SensorManager;
import android.util.DisplayMetrics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.bitmap.AssetBitmapTexture;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.adt.color.Color;

import java.io.IOException;

/**
 * Created by 성준 on 2014-07-25.
 */
public class SampleActivity extends SimpleBaseGameActivity implements IAccelerationListener, IOnSceneTouchListener, IOnAreaTouchListener {
    // ===========================================================
    // Fields
    // ===========================================================
    private int cameraWidth = 360;
    private int cameraHeight = 240;
    private ITexture mBoxFaceTexture;
    private TiledTextureRegion mBoxFaceTextureRegion;
    private ITexture mCircleFaceTexture;
    private TiledTextureRegion mCircleFaceTextureRegion;


    private int mSpriteCount = 0;

    private PhysicsWorld mPhysicsWorld;

    private float mGravityX;
    private float mGravityY;

    private Scene mScene;


    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    @Override
    public EngineOptions onCreateEngineOptions() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        cameraWidth = displayMetrics.widthPixels;
        cameraHeight = displayMetrics.heightPixels;

        final Camera camera = new Camera(0, 0, cameraWidth, cameraHeight);

        return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(cameraWidth, cameraHeight), camera);
    }

    @Override
    public void onCreateResources() throws IOException {
        this.mBoxFaceTexture = new AssetBitmapTexture(this.getTextureManager(), this.getAssets(), "gfx/face_box_tiled.png", TextureOptions.BILINEAR);
        this.mBoxFaceTextureRegion = TextureRegionFactory.extractTiledFromTexture(this.mBoxFaceTexture, 2, 1);
        this.mBoxFaceTexture.load();

        this.mCircleFaceTexture = new AssetBitmapTexture(this.getTextureManager(), this.getAssets(), "gfx/face_circle_tiled.png", TextureOptions.BILINEAR);
        this.mCircleFaceTextureRegion = TextureRegionFactory.extractTiledFromTexture(this.mCircleFaceTexture, 2, 1);
        this.mCircleFaceTexture.load();
    }

    @Override
    public Scene onCreateScene() {
        this.mEngine.registerUpdateHandler(new FPSLogger());

        this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), false);

        this.mScene = new Scene();
        this.mScene.getBackground().setColor(Color.BLACK);
        this.mScene.setOnSceneTouchListener(this);

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
        final AnimatedSprite face = (AnimatedSprite) pTouchArea;
        this.jumpFace(face);
        return true;
    }

    @Override
    public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
        if (this.mPhysicsWorld != null) {
            AnimatedSprite animatedSprite = this.addFace(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
            this.jumpFace(animatedSprite);
            return true;
        }
        return false;
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
    }

    // ===========================================================
    // Methods
    // ===========================================================

    private AnimatedSprite addFace(final float pX, final float pY) {
        this.mSpriteCount++;

        final AnimatedSprite animatedSprite;
        final Body body;

        final FixtureDef objectFixtureDef = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);

        if (this.mSpriteCount % 2 == 1) {
            animatedSprite = new AnimatedSprite(pX, pY, this.mBoxFaceTextureRegion, this.getVertexBufferObjectManager());
            body = PhysicsFactory.createBoxBody(this.mPhysicsWorld, animatedSprite, BodyDef.BodyType.DynamicBody, objectFixtureDef);
        } else {
            animatedSprite = new AnimatedSprite(pX, pY, this.mCircleFaceTextureRegion, this.getVertexBufferObjectManager());
            body = PhysicsFactory.createCircleBody(this.mPhysicsWorld, animatedSprite, BodyDef.BodyType.DynamicBody, objectFixtureDef);
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

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
