package com.pavelsuhko.matchthree;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class MenuScreen extends ScreenAdapter {

    private final MatchThree game;

    public MenuScreen(MatchThree game) {
        this.game = game;
    }

    @Override
    public void show() {
        GameWindow window = game.getWindow();

        Table table = new Table();
        window.add(table);
        Image clouds = new Image(game.getTexture().findRegion("clouds"));
        clouds.setFillParent(true);
        clouds.addAction(Actions.forever(Actions.sequence(Actions.moveBy(20, 0, 3f), Actions.moveBy(-20, 0, 3f))));

        AnimatedImage gemAnimation = new AnimatedImage
                (new Animation<>(0.05f, game.getTexture().findRegions("gem")));
        gemAnimation.setSize(200,200);
        gemAnimation.setPosition(450, 180, Align.center);

        Group backSplash = new Group();
        backSplash.addActor(clouds);
        backSplash.addActor(gemAnimation);
        table.add(backSplash).prefSize(900,360).padBottom(30);
        table.row();

        TextButton playBtn = new TextButton("Play", game.getSkin());
        table.defaults().space(15.0f).width(playBtn.getWidth()*4).height(playBtn.getHeight()*2);
        playBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playClick();
                window.removeActor(table);
                game.setScreen(new GameScreen(game));
            }
        });
        table.add(playBtn);
        table.row();

        TextButton recordsBtn = new TextButton("Records", game.getSkin());
        recordsBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playClick();
                window.removeActor(table);
                game.setScreen(new RecordsScreen(game));
            }
        });
        table.add(recordsBtn);
        table.row();

        TextButton exitBtn = new TextButton("Exit", game.getSkin());
        exitBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playClick();
                Gdx.app.exit();
            }
        });
        table.add(exitBtn);
    }

}