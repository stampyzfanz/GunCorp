package github.elmartino4.guncorp.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import github.elmartino4.guncorp.GameData;

public class AreaMenu extends AbstractMenu {
    public ContextMenuData contextMenuData = new ContextMenuData();
    int x, y;
    public static final Color BORDER = new Color(0.08F, 0.08F, 0.08F, 1);
    public static final Color INNER = new Color(0.3F, 0.3F, 0.3F, 1);

    BitmapFont font;
    FreeTypeFontGenerator generator;
    FreeTypeFontGenerator.FreeTypeFontParameter parameter;

    public AreaMenu(GameData gameData) {
        super(gameData);
    }

    @Override
    public void create() {
        generator = new FreeTypeFontGenerator(Gdx.files.internal("ShareTechMono-Regular.ttf"));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 24;

        font = generator.generateFont(parameter);
    }

    @Override
    public void render() {
        super.data.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        super.data.shapeRenderer.rect(x, y, contextMenuData.getSize()[0], contextMenuData.getSize()[1],
                INNER, INNER, INNER, INNER);
        super.data.shapeRenderer.rect(x + 2, y + 2, contextMenuData.getSize()[0] - 4,
                contextMenuData.getSize()[1] - 4,
                BORDER, BORDER, BORDER, BORDER);

        super.data.shapeRenderer.end();

        super.data.batch.begin();

        int posY = y + contextMenuData.getSize()[1];
        for (ContextMenuData.ContextSubSection section : contextMenuData.data) {
            font.draw(super.data.batch, section.text, x + 7, posY - 7);
            posY -= contextMenuData.height;
        }

        super.data.batch.end();
    }

    @Override
    public void show() {
        int x = Gdx.input.getX();
        int y = Gdx.graphics.getHeight() - Gdx.input.getY();

        this.x = x - ((x > Gdx.graphics.getWidth() * 0.8) ? contextMenuData.getSize()[0] : 0);
        this.y = y - ((y > Gdx.graphics.getHeight() * 0.8) ? contextMenuData.getSize()[1] : 0);
    }

    @Override
    public void dispose() {
        font.dispose();
        generator.dispose();
    }
}
