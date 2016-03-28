package de.klonkclan.simplegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;

public class Enemies
{
    private int amountOfEnemies = 300;
    private int mapWidth;
    private int mapHeight;

    private int enemieSpeed = 100;
    private int enemieWidth = 32;
    private int enemieHeight = 32;

    Array<Rectangle> enemies;

    public Enemies(int mapW, int mapH)
    {
        mapHeight = mapH;
        mapWidth = mapW;
        enemies = new Array<Rectangle>();
        spawnEnemies();
    }

    private void spawnEnemies()
    {
        for(int i = 0; i < amountOfEnemies; i++)
        {
            int y = MathUtils.random(0, mapHeight);
            int x = MathUtils.random(0, mapWidth);
            Rectangle enemie = new Rectangle(x, y, 32, 32);
            enemies.add(enemie);
        }
    }

    public void gameLoop()
    {
        moveEnemies();
    }

    private void moveEnemies()
    {
        Iterator<Rectangle> iter = enemies.iterator();
        while(iter.hasNext())
        {
            int direction = MathUtils.random(0,3);
            Rectangle enemie = iter.next();
            switch (direction)
            {
                case 0: //up
                    enemie.y += enemieSpeed * Gdx.graphics.getDeltaTime();
                    if (enemie.y + enemieHeight > mapHeight)
                    {
                        enemie.y -= (enemieSpeed * 2) * Gdx.graphics.getDeltaTime();
                    }
                    break;

                case 1: //left
                    enemie.x -= enemieSpeed * Gdx.graphics.getDeltaTime();
                    if (enemie.x < 0)
                    {
                        enemie.x -= (enemieSpeed * 2) * Gdx.graphics.getDeltaTime();
                    }
                    break;

                case 2: //down
                    enemie.y -= enemieSpeed * Gdx.graphics.getDeltaTime();
                    if (enemie.y < 0)
                    {
                        enemie.y += (enemieSpeed * 2) * Gdx.graphics.getDeltaTime();
                    }
                    break;

                case 3: //right
                    enemie.x += enemieSpeed * Gdx.graphics.getDeltaTime();
                    if (enemie.x + enemieWidth > mapHeight)
                    {
                        enemie.x -= (enemieSpeed * 2) * Gdx.graphics.getDeltaTime();
                    }
                    break;
            }
        }
    }

    public Array<Rectangle> getRectangle()
    {
        return enemies;
    }
}
