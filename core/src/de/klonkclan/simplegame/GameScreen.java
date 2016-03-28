package de.klonkclan.simplegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class GameScreen implements Screen, InputProcessor
{
    private final Texture hasiSheet;
    private final TextureRegion[] hasiFrames;
    private final Animation hasiAnimation;
    private final int mapHeight;
    private final int mapWidth;
    private final MapLayer objectLayer;
    private final TextureRegion bulletTextureRegion;
    private TextureMapObject tankSprite;
    private TextureRegion tankTextureRegion;
    private AssetManager assetManager;
    final SecondImpact game;

    private TiledMap tiledMap;
    private TiledMapRenderer tiledMapRenderer;

	private OrthographicCamera camera;

    private int moveSpeed = 2;

    private long lastBulletTime = 0;

    private Bullets bullets;

    public SFX sfx;
    private float tankOrientation;
    private float stateTime;
    private TextureRegion currentFrame;


    private static final int        FRAME_COLS = 8;
    private static final int        FRAME_ROWS = 1;
    private Enemies enemies;

    private TextureMapObject bulletMapObject;

    public GameScreen(final SecondImpact gam)
	{
        this.game = gam;
        sfx = new SFX();


        Gdx.input.setInputProcessor(this);

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        // create the camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, w, h);
        camera.update();

        //map tiles
        assetManager = new AssetManager();
        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        //assetManager.load("maps/desert/desert.tmx", TiledMap.class);
        assetManager.load("maps/wha/wha.tmx", TiledMap.class);
        assetManager.load("Tank_brown.png", Texture.class);
        assetManager.load("bullet.png", Texture.class);
        assetManager.load("hasi.png", Texture.class);
        assetManager.load("smoke/smoke.atlas", TextureAtlas.class);
        assetManager.finishLoading();
        // once the asset manager is done loading
        //tiledMap = assetManager.get("maps/desert/desert.tmx");
        tiledMap = assetManager.get("maps/wha/wha.tmx");
        MapProperties mapProp = tiledMap.getProperties();
        mapWidth = mapProp.get("width",Integer.class) * mapProp.get("tilewidth", Integer.class);
        mapHeight = mapProp.get("height",Integer.class) * mapProp.get("tileheight", Integer.class);
        tiledMapRenderer = new OrthogonalTiledMapRendererWithSprites(tiledMap);
        objectLayer = tiledMap.getLayers().get("objects");

        MapLayer bulletsLayer = tiledMap.getLayers().get("bullets");
        bulletTextureRegion = new TextureRegion(assetManager.get("bullet.png", Texture.class),5,20);
        bulletMapObject = new TextureMapObject(bulletTextureRegion);
        bullets = new Bullets(assetManager, bulletsLayer, bulletMapObject);

        Gdx.app.log("Map size", String.valueOf(mapWidth) + "x" + String.valueOf(mapHeight));

        tankTextureRegion = new TextureRegion(assetManager.get("Tank_brown.png", Texture.class), 32, 32);
        TextureMapObject tankMapObject = new TextureMapObject(tankTextureRegion);
        tankMapObject.setX(w / 2 - tankTextureRegion.getRegionWidth() / 2);
        tankMapObject.setY(h / 2 - tankTextureRegion.getRegionHeight() / 2);
        objectLayer.getObjects().add(tankMapObject);

        //bulletSprite = new Sprite(assetManager.get("bullet.png", Texture.class), 0, 0, 5, 20);


        hasiSheet = assetManager.get("hasi.png", Texture.class);
        TextureRegion[][] tmp = TextureRegion.split(hasiSheet, hasiSheet.getWidth()/FRAME_COLS, hasiSheet.getHeight()/FRAME_ROWS);              // #10
        hasiFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        int index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                hasiFrames[index++] = tmp[i][j];
            }
        }
        hasiAnimation = new Animation(0.08f, hasiFrames);      // #11
        stateTime = 0f;

        enemies = new Enemies(mapWidth, mapHeight);
    }

	@Override
	public void render(float delta)
    {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

		Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        sfx.playAmbientBattleSound();

        camera.update();

        tiledMapRenderer.setView(camera);

        game.batch.setProjectionMatrix(camera.combined);

        stateTime += Gdx.graphics.getDeltaTime();
        currentFrame = hasiAnimation.getKeyFrame(stateTime, true);




        tankSprite = (TextureMapObject)tiledMap.getLayers().get("objects").getObjects().get(0);


        int[] layers = new int[] {0,1,2,3,4};
        tiledMapRenderer.render();

        game.batch.begin();

        handleInput();
        bullets.gameLoop();

        enemies.gameLoop();

        for(Rectangle enemiesRect: enemies.getRectangle())
        {
           game.batch.draw(hasiAnimation.getKeyFrame(stateTime, true), enemiesRect.x, enemiesRect.y);
        }
        //tankSprite.draw(game.batch);
        game.font.draw(game.batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 10, 20);
        game.batch.end();
    }

    private void handleInput()
    {
        if (Gdx.input.isKeyPressed(Input.Keys.PAGE_UP)) {
            camera.rotate(-3, 1, 0, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.PAGE_DOWN)) {
            camera.rotate(3, 1, 0, 0);
        }

        movePlayerCamera();
        setPlayerOrientation();

        if(Gdx.input.isKeyPressed(Input.Keys.SPACE))
        {
            TextureRegion textureRegion = new TextureRegion(assetManager.get("bullet.png", Texture.class),5,20);
            boolean isBulletSpawned = bullets.spawnBullet(textureRegion, tankSprite.getRotation(), tankSprite.getX() + (tankSprite.getTextureRegion().getRegionWidth() / 2f), tankSprite.getY() + (tankSprite.getTextureRegion().getRegionHeight() / 2f));
            if(isBulletSpawned)
            {
                sfx.playTankShoot();
            }
        }

        if(Gdx.input.isKeyPressed(Input.Keys.NUM_1))
            tiledMap.getLayers().get(0).setVisible(!tiledMap.getLayers().get(0).isVisible());
        if(Gdx.input.isKeyPressed(Input.Keys.NUM_2))
            tiledMap.getLayers().get(1).setVisible(!tiledMap.getLayers().get(1).isVisible());


       /* camera.zoom = MathUtils.clamp(camera.zoom, 0.1f, 800/camera.viewportWidth);

        float effectiveViewportWidth = camera.viewportWidth * camera.zoom;
        float effectiveViewportHeight = camera.viewportHeight * camera.zoom;

        camera.position.x = MathUtils.clamp(camera.position.x, effectiveViewportWidth / 2f, 800 - effectiveViewportWidth / 2f);
        camera.position.y = MathUtils.clamp(camera.position.y, effectiveViewportHeight / 2f, 480 - effectiveViewportHeight / 2f);*/
    }

    private void movePlayerCamera()
    {

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT))
        {
            camera.translate(-moveSpeed, 0);
            tankSprite.setX(tankSprite.getX() - moveSpeed);
        }

        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT))
        {
            camera.translate(moveSpeed,0);
            tankSprite.setX(tankSprite.getX() + moveSpeed);
        }

        if(Gdx.input.isKeyPressed(Input.Keys.UP))
        {
            camera.translate(0,moveSpeed);
            tankSprite.setY(tankSprite.getY() + moveSpeed);
        }

        if(Gdx.input.isKeyPressed(Input.Keys.DOWN))
        {
            camera.translate(0,-moveSpeed);
            tankSprite.setY(tankSprite.getY() - moveSpeed);
        }

        float cameraHalfWidth = camera.viewportWidth * .5f;
        float cameraHalfHeight = camera.viewportHeight * .5f;

        camera.position.x = MathUtils.clamp(camera.position.x, cameraHalfWidth, 6400 - cameraHalfWidth);
        camera.position.y = MathUtils.clamp(camera.position.y, cameraHalfHeight, 6400 - cameraHalfHeight);

        //Gdx.app.log("camera.position[x,y]", String.valueOf(camera.position.x) + "x" + String.valueOf(camera.position.y));
    }

    private void setPlayerOrientation()
    {
        if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))
        {
            moveSpeed = 8;
        }
        else
        {
            moveSpeed = 2;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT))
        {
            tankOrientation = 90;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT))
        {
            tankOrientation = 270;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.UP))
        {
            tankOrientation = 0;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.DOWN))
        {
            tankOrientation = 180;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT) && Gdx.input.isKeyPressed(Input.Keys.UP))
        {
            tankOrientation = 45;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT) && Gdx.input.isKeyPressed(Input.Keys.DOWN))
        {
            tankOrientation = 135;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) && Gdx.input.isKeyPressed(Input.Keys.DOWN))
        {
            tankOrientation = 225;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) && Gdx.input.isKeyPressed(Input.Keys.UP))
        {
            tankOrientation = 315;
        }

        tankSprite.setRotation(tankOrientation);
    }


    @Override
    public void dispose()
    {
        tiledMap.dispose();
        assetManager.dispose();
        sfx.dispose();
        bullets.dispose();

    }

    @Override
    public void show()
    {
        //rainMusic.play();
    }

    @Override
    public void resize(int width, int height) {
       /* camera.viewportWidth = 800f;
        camera.viewportHeight = 480f * height/width;
        camera.update();*/
    }

    @Override
    public void pause()
    {

    }

    @Override
    public void resume()
    {

    }

    @Override
    public void hide()
    {

    }

    @Override
    public boolean keyDown(int keycode)
    {
        Gdx.app.log("KeyDown", String.valueOf(keycode));

        if(keycode == Input.Keys.SHIFT_LEFT)
        {

            moveSpeed = 8;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode)
    {
        return false;
    }

    @Override
    public boolean keyTyped(char character)
    {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button)
    {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button)
    {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer)
    {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY)
    {
        return false;
    }

    @Override
    public boolean scrolled(int amount)
    {
        return false;
    }
}

