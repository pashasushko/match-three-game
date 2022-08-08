package com.pavelsuhko.matchthree;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class Field extends Table implements Disposable {

    private int score = 0;
    private static final int RANK = 8;
    private final Array<TextureAtlas.AtlasRegion> entities;
    private final Array<Tile> activeTiles = new Array<>(64);
    private final Sound click = Gdx.audio.newSound(Gdx.files.internal("sound/touch_glass.ogg"));
    private final Sound swapWrong = Gdx.audio.newSound(Gdx.files.internal("sound/swap_wrong.ogg"));
    private final Sound swapSuccess = Gdx.audio.newSound(Gdx.files.internal("sound/swap_success.ogg"));

    private final ClickListener clickListener = new ClickListener() {
        Tile firstClick;
        int count = 0;

        final Action afterSwap = new Action() {
            @Override
            public boolean act(float delta) {
                findMatches();
                return true;
            }
        };

        final Action afterClick = new Action() {
            @Override
            public boolean act(float delta) {
                for (Tile tile : activeTiles) {
                    tile.setTouchable(Touchable.enabled);
                }
                return true;
            }
        };

        @Override
        public void clicked(InputEvent event, float x, float y) {
            click.play(0.3f, 3f, 0);
            Tile target = (Tile) event.getTarget();

            if (firstClick != null) {
                target.clearActions();
                firstClick.clearActions();

                int tileIndex1 = activeTiles.indexOf(firstClick, true);
                int tileIndex2 = activeTiles.indexOf(target, true);

                activeTiles.swap(tileIndex1, tileIndex2);
                if ((tileIndex1 == tileIndex2 - 1 || tileIndex1 == tileIndex2 + 1 ||
                        tileIndex1 == tileIndex2 + RANK || tileIndex1 == tileIndex2 - RANK) && hasMatches()) {
                    swapActor(tileIndex1, tileIndex2);
                    target.addAction(moveTo(firstClick.getX(), firstClick.getY(), 0.2f));
                    firstClick.addAction(sequence(moveTo(target.getX(), target.getY(), 0.2f), afterSwap));
                } else {
                    swapWrong.play(0.2f, 1f, 0);
                    activeTiles.swap(tileIndex1, tileIndex2);
                    target.addAction(sequence(moveTo(firstClick.getX(), firstClick.getY(), 0.1f),
                            moveTo(target.getX(), target.getY(), 0.1f)));
                    firstClick.addAction(sequence(moveTo(target.getX(), target.getY(), 0.1f),
                            moveTo(firstClick.getX(), firstClick.getY(), 0.1f), afterSwap));
                }
            }

            count++;
            firstClick = target;

            if (count == 2) {
                firstClick = null;
                count = 0;
            } else {
                for (Tile tile : activeTiles) {
                    tile.setTouchable(Touchable.disabled);
                }
                target.setOrigin(Align.center);
                target.addAction(sequence(parallel(moveTo(target.getX(), target.getY() + 10, 0.125f, Interpolation.swingOut),
                        Actions.scaleBy(0.2f, 0.2f, 0.125f)),
                        parallel(Actions.scaleBy(-0.2f, -0.2f, 0.125f),
                                moveTo(target.getX(), target.getY(), 0.125f, Interpolation.swingIn)), afterClick));
            }
        }
    };

    public int getScore() {
        return score;
    }

    public Field(TextureRegion background, Array<TextureAtlas.AtlasRegion> sprites) {
        defaults().width(80).height(80);
        setBounds(0, 0, 640, 640);
        setBackground(new TextureRegionDrawable(background));
        entities = sprites;
        for (int i = 0; i < RANK * RANK; i++) {
            Tile tile = new Tile();
            tile.addListener(clickListener);
            int num = MathUtils.random(0, 6);
            tile.init(this.entities.get(num), num);
            activeTiles.add(tile);
            if (i % RANK == 0)
                row();
            add(tile);
        }
        findMatches();
    }

    private void swap(int index1, int index2) {
        activeTiles.swap(index1, index2);
        swapActor(index1, index2);
    }

    private void update() {
        clearChildren();
        int i = 0;
        for (Tile tile : activeTiles) {
            if (tile.type == -1) {
                score++;
                int num = MathUtils.random(0, 6);
                tile.init(entities.get(num), num);
                tile.addAction(sequence(fadeIn(0.25f)));
            }
            if (i % RANK == 0)
                row();
            add(tile);
            i++;
        }
    }

    private void moveDown() {
        for (int i = RANK - 1; i >= 0; i--)
            for (int j = 0; j < RANK; j++)
                if (activeTiles.get(j + i * RANK).type == -1)
                    for (int n = i; n >= 0; n--)
                        if (activeTiles.get(j + n * RANK).type != -1) {
                            swap(j + n * RANK, j + i * RANK);
                            break;
                        }

    }

    // I KNOW IT'S BAD. I WAS ON DEADLINE
    private void findMatches() {
        //checking rows
        int matches;
        int colorToMatch;
        boolean hasMatch = false;
        for (int i = 0; i < RANK; i++) {
            colorToMatch = activeTiles.get(i * RANK).type;
            matches = 1;
            for (int j = 1; j < RANK; j++) {
                if (activeTiles.get(j + i * RANK).type == colorToMatch) {
                    matches++;
                } else {
                    colorToMatch = activeTiles.get(j + i * RANK).type;
                    if (matches >= 3) {
                        hasMatch = true;
                        for (int j2 = j - 1; j2 >= j - matches; j2--) {
                            activeTiles.get(j2 + i * RANK).type = -1;
                        }
                    }
                    matches = 1;
                }
            }
            if (matches >= 3) {
                hasMatch = true;
                for (int j = RANK - 1; j >= RANK - matches; j--) {
                    activeTiles.get(j + i * RANK).type = -1;
                }
            }
        }
        //checking columns
        for (int j = 0; j < RANK; j++) {
            colorToMatch = activeTiles.get(j).type;
            matches = 1;
            for (int i = 1; i < RANK; i++) {
                if (activeTiles.get(j + i * RANK).type == colorToMatch) {
                    matches++;
                } else {
                    colorToMatch = activeTiles.get(j + i * RANK).type;
                    if (matches >= 3) {
                        hasMatch = true;
                        for (int i2 = i - 1; i2 >= i - matches; i2--) {
                            activeTiles.get(j + i2 * RANK).type = -1;
                        }
                    }
                    matches = 1;
                }
            }
            if (matches >= 3) {
                hasMatch = true;
                for (int i = RANK - 1; i >= RANK - matches; i--) {
                    activeTiles.get(j + i * RANK).type = -1;
                }
            }
        }
        if (hasMatch) {
            swapSuccess.play(0.2f, 1f, 0);
            int count = 1;
            for (Tile tile : activeTiles.select((tile) -> tile.type == -1)) {
                if (count % 3 == 0)
                    tile.addAction(sequence(fadeOut(0.25f), afterMatch));
                else
                    tile.addAction(fadeOut(0.25f));
                count++;
            }
        }
    }

    private final Action afterMatch = new Action() {
        @Override
        public boolean act(float delta) {
            moveDown();
            update();
            findMatches();
            return true;
        }
    };

    private boolean hasMatches() {
        int matches;
        int colorToMatch;
        boolean hasMatch = false;
        for (int i = 0; i < RANK; i++) {
            colorToMatch = activeTiles.get(i * RANK).type;
            matches = 1;
            for (int j = 1; j < RANK; j++) {
                if (activeTiles.get(j + i * RANK).type == colorToMatch) {
                    matches++;
                } else {
                    colorToMatch = activeTiles.get(j + i * RANK).type;
                    if (matches >= 3) {
                        hasMatch = true;
                    }
                    matches = 1;
                }
            }
            if (matches >= 3) {
                hasMatch = true;
            }
        }
        for (int j = 0; j < RANK; j++) {
            colorToMatch = activeTiles.get(j).type;
            matches = 1;
            for (int i = 1; i < RANK; i++) {
                if (activeTiles.get(j + i * RANK).type == colorToMatch) {
                    matches++;
                } else {
                    colorToMatch = activeTiles.get(j + i * RANK).type;
                    if (matches >= 3) {
                        hasMatch = true;
                    }
                    matches = 1;
                }
            }
            if (matches >= 3) {
                hasMatch = true;
            }
        }
        return hasMatch;
    }

    @Override
    public void dispose() {
        click.dispose();
        swapWrong.dispose();
        swapSuccess.dispose();
    }
}
