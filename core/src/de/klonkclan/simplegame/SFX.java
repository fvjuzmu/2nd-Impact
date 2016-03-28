package de.klonkclan.simplegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SoundLoader;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.TimeUtils;

public class SFX
{
    private AssetManager sounds;

    private Sound battleSound;
    private Sound tankShoot;
    private long lastBattleSound;

    public SFX()
    {
        sounds = new AssetManager();

        loadAssets();

        battleSound = sounds.get("sounds/battleSound.ogg"); //TODO add this to a pool
        tankShoot = sounds.get("sounds/tankShoot.ogg", Sound.class);
    }

    private void loadAssets()
    {
        sounds.load("sounds/battleSound.ogg", Sound.class);
        sounds.load("sounds/tankShoot.ogg", Sound.class);
        sounds.finishLoading();

    }

    public void playAmbientBattleSound()
    {
        //dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.ogg"));
        //rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.ogg"));
        if(TimeUtils.millis() - lastBattleSound > 120000)
        {
            battleSound.play();
            lastBattleSound = TimeUtils.millis();
        }
    }

    public void playTankShoot()
    {
        tankShoot.play();
    }

    public void dispose()
    {
        battleSound.dispose();
        tankShoot.dispose();
    }
}
