package surreal.bundles.config;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import surreal.bundles.Bundles;

import java.util.ArrayList;
import java.util.List;

public class ConfigGui extends GuiConfig {

    public ConfigGui(GuiScreen parentScreen) {
        super(parentScreen, getElements(), Bundles.MODID, "Bundles", false, false, GuiConfig.getAbridgedConfigPath(ConfigHandler.configuration().toString()));
    }

    private static List<IConfigElement> getElements() {
        Configuration config = ConfigHandler.configuration();
        List<IConfigElement> list = new ArrayList<>();

        list.add(new ConfigElement(config.getCategory("server side")));
        list.add(new ConfigElement(config.getCategory("client side")));

        return list;
    }
}
