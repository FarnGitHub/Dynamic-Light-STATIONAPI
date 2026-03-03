package farn.dynamicLight.config.gui;

import farn.dynamicLight.cache.ItemLightData;
import farn.dynamicLight.config.Config;
import farn.dynamicLight.config.gui.button.TestInterface;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.TranslationStorage;
import org.lwjgl.input.Keyboard;

public class DynamicLightItemScreen extends Screen{
    private Screen parent;
    private final ItemLightData worldName;

    public DynamicLightItemScreen(Screen parent, ItemLightData worldName) {
        super();
        this.parent = parent;
        this.worldName = worldName;
    }

    public void init() {
        TranslationStorage var1 = TranslationStorage.getInstance();
        Keyboard.enableRepeatEvents(true);
        this.buttons.clear();
        this.buttons.add(new ButtonWidget(0, this.width / 2 - 100, this.height / 4 + 96 + 12, var1.get("selectWorld.renameButton")));
        this.buttons.add(new ButtonWidget(1, this.width / 2 - 100, this.height / 4 + 120 + 12, var1.get("gui.cancel")));
        String var4 = Config.itemIdToIdentifier(worldName.id).toString();
        this.itemTextField = new TextFieldWidget(this, this.textRenderer, this.width / 2 - 100, 60, 100, 12, var4);
        this.itemTextField.focused = true;
        this.itemTextField.setMaxLength(32);
    }

    public void removed() {
        Keyboard.enableRepeatEvents(false);
    }

    protected void buttonClicked(ButtonWidget button) {
        if (button.active) {
            if (button.id == 1) {
                this.minecraft.setScreen(this.parent);
            } else if (button.id == 0) {
                this.minecraft.setScreen(this.parent);
            }

        }
    }

    protected void keyPressed(char character, int keyCode) {
        this.itemTextField.keyPressed(character, keyCode);
        ((ButtonWidget)this.buttons.get(0)).active = this.itemTextField.getText().trim().length() > 0;
        if (character == '\r') {
            this.buttonClicked((ButtonWidget)this.buttons.get(0));
        }

    }

    protected void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        this.itemTextField.mouseClicked(mouseX, mouseY, button);
    }

    public void render(int mouseX, int mouseY, float delta) {
        TranslationStorage var4 = TranslationStorage.getInstance();
        this.renderBackground();
        this.drawCenteredTextWithShadow(this.textRenderer, var4.get("selectWorld.renameTitle"), this.width / 2, this.height / 4 - 60 + 20, 16777215);
        this.drawTextWithShadow(this.textRenderer, "Item Identifier", this.width / 2 - 100, 47, 10526880);
        this.itemTextField.render();
        super.render(mouseX, mouseY, delta);
    }
}
