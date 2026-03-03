package farn.dynamicLight.config.gui.button;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;

;

public class IntegerTextFieldWidget extends TextFieldWidget implements TestInterface<Integer> {
    public IntegerTextFieldWidget(Screen parent, TextRenderer textRenderer, int x, int y, int width, int height, String text) {
        super(parent, textRenderer, x, y, width, height, text);
    }

    @Override
    public Integer parseValue() {
        try {
            return Integer.parseInt(getText());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
