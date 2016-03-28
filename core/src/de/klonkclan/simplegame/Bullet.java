package de.klonkclan.simplegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.math.Rectangle;

public class Bullet
{
    private float bulletDiagonalSpeed;

    private Rectangle rectangle;
    private float orientation;
    private int liveTime;
    private int livedTime;
    private float speed;

    private float bulletDiagonalMod;

    private boolean isAlive;

    private MapLayer mapLayer;

    TextureMapObject textureMapObject;


    public Bullet(MapLayer layer, TextureRegion textureRegion, float x, float y, float tankOrientation, int bulletSpeed, int bulletLiveTime)
    {
        mapLayer = layer;
        rectangle = new Rectangle();
        rectangle.width = 5;
        rectangle.height = 20;
        rectangle.x = x - rectangle.getWidth() / 2f;
        rectangle.y = y - rectangle.getHeight() / 2f;

        liveTime = bulletLiveTime;
        livedTime = 0;
        isAlive = true;

        speed = bulletSpeed;
        bulletDiagonalMod = 0.75f;
        bulletDiagonalSpeed = speed * bulletDiagonalMod;

        orientation = tankOrientation;

        //spawn the bullet
        textureMapObject = new TextureMapObject(textureRegion);
        textureMapObject.setX(rectangle.x);
        textureMapObject.setY(rectangle.y);
        mapLayer.getObjects().add(textureMapObject);
    }

    public void moveBullet()
    {
        moveToNewPosition();
        checkIfStillAlive();
    }

    private void checkIfStillAlive()
    {
        if(livedTime > liveTime)
        {
            mapLayer.getObjects().remove(textureMapObject);
            isAlive = false;
        }
    }

    private void moveToNewPosition()
    {
        if(orientation == 0) //oben
        {
            rectangle.y += speed * Gdx.graphics.getDeltaTime();
            livedTime += speed;
        }
        else if(orientation == 45) //LO
        {
            rectangle.x -= bulletDiagonalSpeed * Gdx.graphics.getDeltaTime();
            rectangle.y += bulletDiagonalSpeed * Gdx.graphics.getDeltaTime();
            livedTime += bulletDiagonalSpeed;
        }
        else if(orientation == 90) //links
        {
            rectangle.x -= speed * Gdx.graphics.getDeltaTime();
            livedTime += speed;
        }
        else if(orientation == 135) //LU
        {
            rectangle.x -= bulletDiagonalSpeed * Gdx.graphics.getDeltaTime();
            rectangle.y -= bulletDiagonalSpeed * Gdx.graphics.getDeltaTime();
            livedTime += bulletDiagonalSpeed;
        }
        else if(orientation == 180) //unten
        {
            rectangle.y -= speed * Gdx.graphics.getDeltaTime();
            livedTime += speed;
        }
        else if(orientation == 225) //RU
        {
            rectangle.x += bulletDiagonalSpeed * Gdx.graphics.getDeltaTime();
            rectangle.y -= bulletDiagonalSpeed * Gdx.graphics.getDeltaTime();
            livedTime += bulletDiagonalSpeed;
        }
        else if(orientation == 270) //rechts
        {
            rectangle.x += speed * Gdx.graphics.getDeltaTime();
            livedTime += speed;
        }
        else if(orientation == 315) //RO
        {
            rectangle.x += bulletDiagonalSpeed * Gdx.graphics.getDeltaTime();
            rectangle.y += bulletDiagonalSpeed * Gdx.graphics.getDeltaTime();
            livedTime += bulletDiagonalSpeed;
        }

        textureMapObject.setRotation(orientation);
        textureMapObject.setX(rectangle.x);
        textureMapObject.setY(rectangle.y);
        Gdx.app.log("Spwan bullet", textureMapObject.getX() + "x" + textureMapObject.getY() + "and orientation " + textureMapObject.getRotation() + "(should be " + orientation + ")");
    }


    public Rectangle getRectangle()
    {
        return rectangle;
    }

    public float getOrientation()
    {
        return orientation;
    }

    public boolean isAlive()
    {
        return isAlive;
    }
}
