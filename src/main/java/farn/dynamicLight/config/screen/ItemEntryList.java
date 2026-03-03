package farn.dynamicLight.config.screen;

import farn.dynamicLight.cache.ItemLightData;
import farn.dynamicLight.world.Dispatcher;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.render.Tessellator;
import org.lwjgl.opengl.GL11;

public class ItemEntryList extends EntryListWidget {
    ObjectArrayList<ItemLightData> items = new ObjectArrayList<>();
    Screen screen;

    public ItemEntryList(Screen screen) {
        super(Minecraft.INSTANCE, screen.width, screen.height, 8, screen.height - 55 + 4, 24);
        this.screen = screen;
        for(Int2ObjectMap.Entry<ItemLightData> dataEntry: Dispatcher.lightdataMap.int2ObjectEntrySet()) {
            items.add(dataEntry.getValue());
        }
    }

    @Override
    protected int getEntryCount() {
        return items.size();
    }

    @Override
    protected void entryClicked(int index, boolean doubleClick) {
        ItemLightData test = items.get(index);
        test.enabled = !test.enabled;
    }

    @Override
    protected boolean isSelectedEntry(int index) {
        return false;
    }

    @Override
    protected void renderBackground() {
        screen.renderBackground();
    }

    @Override
    protected void renderEntry(int index, int x, int y, int i, Tessellator tessellator) {
        ItemLightData test = items.get(index);
        String lmo = test.itemNames;
        Minecraft.INSTANCE.textRenderer.draw(lmo, x, y + (itemHeight / 4), 14737632);
        this.renderButton(screen.width / 2 + 80, y + (itemHeight / 4) - 5, 30, 20, test);
    }

    private void renderButton(int x, int y, int width, int height,ItemLightData data) {
        GL11.glBindTexture(3553, Minecraft.INSTANCE.textureManager.getTextureId("/gui/gui.png"));
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        screen.drawTexture(x, y, 0, 46 + 20, width / 2, height);
        screen.drawTexture(x + width / 2, y, 200 - width / 2, 46 + 20, width / 2, height);
        screen.drawCenteredTextWithShadow(Minecraft.INSTANCE.textRenderer, data.enabled ? "ON" : "OFF", x + width / 2, y + (height - 8) / 2, 14737632);
    }
}
