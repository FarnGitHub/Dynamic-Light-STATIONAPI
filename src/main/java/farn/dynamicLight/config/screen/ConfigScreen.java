package farn.dynamicLight.config.screen;

import farn.dynamicLight.config.ItemLightInfo;
import farn.dynamicLight.config.ItemLightInfoLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.resource.language.TranslationStorage;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class ConfigScreen extends Screen{
    protected Screen parent;
    private ItemLightInfoEntry entryList;

    public ConfigScreen(Screen parent) {
        this.parent = parent;
    }

    public void init() {
        TranslationStorage translator = TranslationStorage.getInstance();
        //noinspection unchecked
        this.buttons.add(new ButtonWidget(0, this.width / 2 - 75, this.height - 48, 150, 20, translator.get("gui.done")));
        this.entryList = new ItemLightInfoEntry();
        this.entryList.registerButtons(this.buttons, 7, 8);
    }

    protected void buttonClicked(ButtonWidget button) {
        if (button.active) {
            if (button.id == 0)
                this.minecraft.setScreen(this.parent);
            else
                this.entryList.buttonClicked(button);
        }
    }

    public void render(int mouseX, int mouseY, float delta) {
        this.entryList.render(mouseX, mouseY, delta);
        this.drawCenteredTextWithShadow(this.textRenderer, TranslationStorage.getInstance().get("dynamic.light.configuration"), this.width / 2, 16, 16777215);
        super.render(mouseX, mouseY, delta);
    }

    public void removed() {
        ItemLightInfoLoader.readConfig();
        super.removed();
    }

    private class ItemLightInfoEntry extends EntryListWidget {
        private final List<ItemLightInfo> datas;

        public ItemLightInfoEntry() {
            super(Minecraft.INSTANCE, ConfigScreen.this.width, ConfigScreen.this.height, 8, ConfigScreen.this.height - 55 + 4, 24);
            this.datas = new ArrayList<>(ItemLightInfoLoader.id2info.values());
        }

        @Override
        protected int getEntryCount() {
            return ItemLightInfoLoader.id2info.size();
        }

        @Override
        protected void entryClicked(int index, boolean doubleClick) {
            ItemLightInfo test = datas.get(index);
            test.enabled = !test.enabled;
        }

        @Override
        protected boolean isSelectedEntry(int index) {
            return false;
        }

        @Override
        protected void renderBackground() {
            ConfigScreen.this.renderBackground();
        }

        @Override
        protected void renderEntry(int index, int x, int y, int i, Tessellator tessellator) {
            ItemLightInfo test = datas.get(index);
            String lmo = test.itemNames;
            ConfigScreen.this.textRenderer.draw(lmo, x, y + (itemHeight / 4), 14737632);
            this.renderButton(ConfigScreen.this.width / 2 + 80, y + (itemHeight / 4) - 5, test);
        }

        private void renderButton(int x, int y, ItemLightInfo data) {
            GL11.glBindTexture(3553, Minecraft.INSTANCE.textureManager.getTextureId("/gui/gui.png"));
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            ConfigScreen.this.drawTexture(x, y, 0, 46 + 20, 30 / 2, 20);
            ConfigScreen.this.drawTexture(x + 30 / 2, y, 200 - 30 / 2, 46 + 20, 30 / 2, 20);
            ConfigScreen.this.drawCenteredTextWithShadow(Minecraft.INSTANCE.textRenderer, data.enabled ? "ON" : "OFF", x + 30 / 2, y + (20 - 8) / 2, 14737632);
        }
    }
}
