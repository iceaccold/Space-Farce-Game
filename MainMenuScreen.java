package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class MainMenuScreen implements Screen {
    private MySpaceFarceGame game;
    private int score;
    private int lives;
    OrthographicCamera camera;
    SpriteBatch batch;
    BitmapFont font;

    public MainMenuScreen(MySpaceFarceGame game, int score) {
        this.score = score;
        this.game = game;
        this.lives = 5;

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
        font.draw(batch, "Welcome to Space Farce! ", 70, 420);
        font.draw(batch, "Touch the screen or", 85, 370);
        font.draw(batch, "Press enter to start!", 85, 320);
        batch.end();

        if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isTouched()) {
            game.setScreen(new LevelOneScreen(game, score, lives));
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
