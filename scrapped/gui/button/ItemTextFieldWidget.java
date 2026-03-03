package farn.dynamicLight.config.gui.button;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;;
import net.modificationstation.stationapi.api.util.Identifier;

public class ItemTextFieldWidget extends TextFieldWidget implements TestInterface<Identifier> {
    public ItemTextFieldWidget(Screen parent, TextRenderer textRenderer, int x, int y, int width, int height, String text) {
        super(parent, textRenderer, x, y, width, height, text);
    }

    @Override
    public Identifier parseValue() {
        return Identifier.of(getText());
    }
}
