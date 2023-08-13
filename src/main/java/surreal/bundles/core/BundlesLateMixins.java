package surreal.bundles.core;

import net.minecraftforge.fml.common.Loader;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.Collections;
import java.util.List;

public class BundlesLateMixins implements ILateMixinLoader {

    @Override
    public List<String> getMixinConfigs() {
        return Collections.singletonList("mixins.bundles.mt.json");
    }

    @Override
    public boolean shouldMixinConfigQueue(String mixinConfig) {
        return Loader.isModLoaded("mousetweaks");
    }
}
