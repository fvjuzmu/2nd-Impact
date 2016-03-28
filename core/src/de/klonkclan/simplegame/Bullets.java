package de.klonkclan.simplegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class Bullets
{
    private final TextureMapObject bulletTextureMapObject;
    private long lastBulletTime;
    private Array<Bullet> bullets;

    private int bulletSpeed = 300;
    private int bulleLiveTime = 12000;
    MapLayer mapLayer;
    private AssetManager assetManager;

    public Bullets(AssetManager assManager ,MapLayer bulletsLayer, TextureMapObject bulletMapObject)
    {
        assetManager = assManager;
        mapLayer = bulletsLayer;
        bulletTextureMapObject = bulletMapObject;
        bullets = new Array<Bullet>();
    }

    /*public Array<Rectangle> get()
    {
        Array<Rectangle> bulletRectangles = new Array<Rectangle>();
        Iterator<Bullet> iter = bullets.iterator();
        while (iter.hasNext())
        {
            Bullet bullet = iter.next();
            bulletRectangles.add(bullet.rectangle);
        }
        return bulletRectangles;
    }*/

    public Array<Bullet> get()
    {
        return bullets;
    }

    public boolean spawnBullet(TextureRegion textureRegion, float tankOrientation, float x, float y)
    {
        if(TimeUtils.millis() - lastBulletTime > 100)
        {
            Gdx.app.log("shoot", "here it comes");

            bullets.add(new Bullet(mapLayer, textureRegion, x, y, tankOrientation, bulletSpeed, bulleLiveTime));
            lastBulletTime = TimeUtils.millis();
            return true;
        }

        return false;
    }

    public void gameLoop()
    {
        if(bullets != null)
        {
            Iterator<Bullet> iter = bullets.iterator();
            while (iter.hasNext())
            {
                Bullet bullet = iter.next();

                if(!bullet.isAlive())
                {
                    iter.remove();
                    spawnDust(bullet.getRectangle().getX(), bullet.getRectangle().getY());
                    Gdx.app.log("Bullet", "removed at " + bullet.getRectangle().x + "x" +  bullet.getRectangle().y);
                    return;
                }

                bullet.moveBullet();
            }
        }
    }

    private void spawnDust(float x, float y)
    {
        //TextureAtlas 
        TextureAtlas atlas = assetManager.get("smoke/smoke.atlas", TextureAtlas.class);
        TextureAtlas.AtlasRegion region = atlas.findRegion("imagename");

        Animation walkAnimation = new Animation(0.025f, region);
        walkAnimation.setPlayMode(Animation.PlayMode.NORMAL);
        float stateTime = Gdx.graphics.getDeltaTime();           // #15
        TextureRegion currentFrame = walkAnimation.getKeyFrame(stateTime, true);
        TextureMapObject textureMapObject = new TextureMapObject(currentFrame);
        textureMapObject.setX(x);
        textureMapObject.setY(y);
        //TODO magic draw sprite
    }

    public void dispose()
    {
        bullets.clear();
    }
}


