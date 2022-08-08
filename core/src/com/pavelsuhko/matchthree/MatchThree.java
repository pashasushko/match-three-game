package com.pavelsuhko.matchthree;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class MatchThree extends Game {

	private Skin skin;
	private Sound click;
	private Stage stage;
	private GameWindow window;
	private TextureAtlas textureAtlas;
	private FileHandle recordsFile;

	@Override
	public void create () {
		skin = new Skin(Gdx.files.internal("skin/OS Eight.json"));
		textureAtlas = new TextureAtlas("texture/Textures.atlas");
		click = Gdx.audio.newSound(Gdx.files.internal("sound/menu_click.ogg"));
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		window = new GameWindow("", skin);
		window.setMovable(stage);
		window.setFillParent(true);
		stage.addActor(window);
		recordsFile = Gdx.files.local("Records");
		setScreen(new MenuScreen(this));
	}

	public void playClick() {
		click.play(0.2f, 3f,0);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public Skin getSkin() {
		return skin;
	}
	public GameWindow getWindow() {
		return window;
	}
	public TextureAtlas getTexture() { return  textureAtlas; }
	public FileHandle getRecordsFile() {
		return recordsFile;
	}

	@Override
	public void render() {
		super.render();
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}

	@Override
	public void dispose() {
		super.dispose();
		textureAtlas.dispose();
		skin.dispose();
		stage.dispose();
		click.dispose();
	}

}
