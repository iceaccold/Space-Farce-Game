package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class DiedScreen implements Screen {
    private MySpaceFarceGame game;
    private int score;
    OrthographicCamera camera;
    SpriteBatch batch;
    BitmapFont font;

    DiedScreen(MySpaceFarceGame game, int score) {
        this.score = score;
        this.game = game;

        batch = new SpriteBatch();
        font = new BitmapFont();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 320, 640);
    }
    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0.2f, 0, 1);

        batch.setProjectionMatrix(camera.combined);
        camera.update();

        batch.begin();
        font.draw(batch, "!!Sorry You Died !! ", 90, 370);
        font.draw(batch, "Thanks for playing Space Farce! ", 60, 320);
        font.draw(batch, "Your score was " + score + "!", 85, 2800);
        font.draw(batch, "Touch the screen or", 85, 240);//
        font.draw(batch, "Press enter to exit.", 85, 210);
        batch.end();

        if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.justTouched()) {
            Gdx.app.exit();
            dispose();
        }
    }
    @Override
    public void resize(int width, int height) {}
    @Override
    public void show() {}
    @Override
    public void hide() {}
    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
