package farn.dynamicLight.config.screen;

import farn.dynamicLight.config.DynamicLightLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.resource.language.TranslationStorage;

public class ConfigScreen extends Screen{
    protected Screen parent;
    private ItemEntryList entryList;

    public ConfigScreen(Screen parent) {
        super();
        this.parent = parent;
    }

    public void init() {
        TranslationStorage var1 = TranslationStorage.getInstance();
        this.buttons.add(new OptionButtonWidget(0, this.width / 2 - 75, this.height - 48, var1.get("gui.done")));
        this.entryList = new ItemEntryList(this);
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
        this.drawCenteredTextWithShadow(this.textRenderer, "Dynamic Light Configuration", this.width / 2, 16, 16777215);
        super.render(mouseX, mouseY, delta);
    }

    public void removed() {
        DynamicLightLoader.readConfig();
        super.removed();
    }
}
