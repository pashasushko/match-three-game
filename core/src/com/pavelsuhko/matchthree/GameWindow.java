package com.pavelsuhko.matchthree;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class GameWindow extends Window {

    public GameWindow(String title, Skin skin) {
        super(title, skin);

        getTitleTable().clearChildren();
        getTitleTable().defaults().space(5.0f);

        Button button = new Button(skin, "close");

        getTitleTable().add(button);

        Image image = new Image(skin, "title-bar-bg");
        image.setScaling(Scaling.stretchX);
        getTitleTable().add(image).growX();

        Label label = new Label("Match Three Game", skin);
        label.setFontScale(1.15f);
        getTitleTable().add(label).padLeft(20.0f).padRight(20.0f);

        image = new Image(skin, "title-bar-bg");
        image.setScaling(Scaling.stretchX);
        getTitleTable().add(image).growX();

        button = new Button(skin, "restore");
        getTitleTable().add(button);

        button = new Button(skin, "minimize");
        getTitleTable().add(button);
    }

    public void setMovable(Stage stage) {
        Vector2 position = new Vector2();
        getTitleTable().addListener(new ClickListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                position.x = stage.stageToScreenCoordinates(new Vector2(x, y)).x;
                position.y = stage.stageToScreenCoordinates(new Vector2(x, y)).y;
                return super.touchDown(event, x, y, pointer, button);
            }
        });
        getTitleTable().addListener(new DragListener(){
            @Override
            public void drag(InputEvent event, float x, float y, int pointer) {
                Lwjgl3Window mainWindow = ((Lwjgl3Graphics) Gdx.graphics).getWindow();

                int thisX = mainWindow.getPositionX();
                int thisY = mainWindow.getPositionY();

                int movedX = (int) ((thisX + stage.stageToScreenCoordinates(new Vector2(x, y)).x) - (thisX + position.x));
                int movedY = (int) ((thisY + stage.stageToScreenCoordinates(new Vector2(x, y)).y) - (thisY + position.y));

                int X = thisX + movedX;
                int Y = thisY + movedY;

                mainWindow.setPosition(X, Y);
            }
        });
    }

}
