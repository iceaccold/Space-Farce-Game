package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Random;

public class MySpaceFarceGame extends Game {
	private MySpaceFarceGame game;
	private int score = 0;

	SpriteBatch batch;
	BitmapFont font;

	public static Random random = new Random();

	public MySpaceFarceGame() {
		game = this;
	}
	@Override
	public void create() {
		batch = new SpriteBatch();
		// Use LibGDX's default Arial font.
		font = new BitmapFont();
		this.setScreen(new MainMenuScreen(this, score));
	}
	@Override
	public void render() {
		super.render();
	}
	@Override
	public void dispose() {
		batch.dispose();
		font.dispose();
	}
}
