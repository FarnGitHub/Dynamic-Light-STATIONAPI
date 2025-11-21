package farn.dynamicLight.other.config;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.TranslationStorage;

import java.awt.*;

public class ConfigurationScreen extends Screen {
    Screen parent;
    public ConfigurationScreen(Screen par) {
        parent = par;
    }

    @Override
    public void render(int x, int y, float renderPartialTicks) {
        this.renderBackground();
        super.render(x, y, renderPartialTicks);
        this.drawCenteredTextWithShadow(this.textRenderer, TranslationStorage.getInstance().get("dynamic_light.cfg_title"), this.width / 2, 20, 0xFFFFFF);
    }

    @Override
    public void init() {
        TranslationStorage translator = TranslationStorage.getInstance();
        this.buttons.add(new ButtonWidget(0, this.width / 2 - 100, this.height - 188, translator.get("dynamic_light.open_cfg")));
        this.buttons.add(new ButtonWidget(1, this.width / 2 - 100,  this.height - 152, translator.get("dynamic_light.reset_cfg")));
        this.buttons.add(new ButtonWidget(2, this.width / 2 - 100, this.height - 28, translator.get("gui.done")));
    }

    @Override
    protected void buttonClicked(ButtonWidget guibutton) {
        if(guibutton.id == 0) {
            try {
                if(Config.settingsFile.exists()) Desktop.getDesktop().open(Config.settingsFile);
            } catch (Exception e) {
            }
        } else {
            if(guibutton.id == 1) {
                Config.writeDefaultSettingFile();
            }
            this.minecraft.setScreen(parent);
        }
    }

    public void removed() {
        super.removed();
        Config.initializeSettingsFile(true);
        this.minecraft.worldRenderer.reload();
    }
}
