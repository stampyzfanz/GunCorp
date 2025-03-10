package github.elmartino4.guncorp;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import github.elmartino4.guncorp.config.ConfigChangeCallback;
import github.elmartino4.guncorp.config.Keybindings;
import github.elmartino4.guncorp.config.UserConfig;
import github.elmartino4.guncorp.menu.AbstractMenu;
import github.elmartino4.guncorp.menu.AreaMenu;
import github.elmartino4.guncorp.menu.EscapeMenu;
import github.elmartino4.guncorp.menu.MenuData;
import github.elmartino4.guncorp.screen.AbstractScreen;
import github.elmartino4.guncorp.screen.CorpopediaScreen;
import github.elmartino4.guncorp.screen.MapScreen;
import github.elmartino4.guncorp.screen.MyCorpScreen;

import java.io.IOException;

public class GunCorpMain extends ApplicationAdapter {
    public GameData gameData = new GameData(this::onMenuData);

    private int titleTimer = 0;

    public GunCorpMain(ConfigChangeCallback configChangeCallback) {
        this.gameData.menus = new AbstractMenu[] { new EscapeMenu(gameData), new AreaMenu(gameData) };
        this.gameData.screens = new AbstractScreen[] { new MapScreen(gameData), new CorpopediaScreen(gameData),
                new MyCorpScreen(gameData) };
        UserConfig.configChangeCallback = configChangeCallback;
    }

    @Override
    public void create() {
        this.gameData.batch = new SpriteBatch();
        this.gameData.shapeRenderer = new ShapeRenderer();

        // Camera
        this.gameData.camera = new OrthographicCamera();

        // Viewport
        this.gameData.viewport = new ExtendViewport(1500, 800, this.gameData.camera);

        for (AbstractScreen screen : gameData.screens) {
            screen.create();
        }

        for (AbstractMenu menu : gameData.menus) {
            menu.create();
        }

        UserConfig.generate();
        Keybindings.generate();

        try {
            this.gameData.saveFile.begin();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render() {
        if (titleTimer < 0) {
            if (UserConfig.prefs.getBoolean("debug")) {
                Gdx.graphics.setTitle(String.format("GunCorp - %dfps\n", Gdx.graphics.getFramesPerSecond()));
            }
            titleTimer = (int) (Math.min(Gdx.graphics.getFramesPerSecond() / 2F, 1000));
        } else {
            titleTimer--;
        }

        if (Keybindings.isKeyJustPressed("Toggle debug")) {
            UserConfig.prefs.putBoolean("debug", !UserConfig.prefs.getBoolean("debug"));
            UserConfig.prefs.flush();
            if (!UserConfig.prefs.getBoolean("debug")) {
                Gdx.graphics.setTitle("GunCorp");
            } else {
                Gdx.graphics.setTitle(String.format("GunCorp - %dfps\n", Gdx.graphics.getFramesPerSecond()));
            }
        }

        ScreenUtils.clear(1, 0, 0, 1);

        gameData.screens[gameData.getCurrentScreen()].render();

        if (gameData.getCurrentMenu() != -1)
            gameData.menus[gameData.getCurrentMenu()].render();
    }

    @Override
    public void dispose() {
        this.gameData.batch.dispose();
        this.gameData.shapeRenderer.dispose();
        for (AbstractMenu menu : gameData.menus) {
            menu.dispose();
        }

        for (AbstractScreen screen : gameData.screens) {
            screen.dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
        this.gameData.viewport.setMinWorldWidth(width);
        this.gameData.viewport.setMinWorldHeight(height);
        this.gameData.viewport.update(width, height, true);

        this.gameData.batch.setProjectionMatrix(this.gameData.camera.combined);
        this.gameData.shapeRenderer.setProjectionMatrix(this.gameData.camera.combined);

        if (this.gameData.getCurrentMenu() == 1)
            this.gameData.setCurrentMenu(-1);
    }

    public void onMenuData(MenuData data) {
        if (data.equals(MenuData.QUIT))
            Gdx.app.exit();
        if (data.equals(MenuData.PEDIA))
            gameData.setCurrentScreen(1, true);
        if (data.equals(MenuData.MY_CORP))
            gameData.setCurrentScreen(2, true);
    }
}
