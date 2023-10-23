package surreal.bundles.client;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import surreal.bundles.Bundles;
import surreal.bundles.items.ItemBundle;

@SideOnly(Side.CLIENT)
public class ClientProxy {

    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(Bundles.BUNDLE, 0, new ModelResourceLocation(new ResourceLocation(Bundles.MODID, "bundle"), "inventory"));
    }

    @SubscribeEvent
    public void registerItemColors(ColorHandlerEvent.Item event) {
        event.getItemColors().registerItemColorHandler(ClientProxy.BUNDLE_COLOR, Bundles.BUNDLE);
    }

    public static IItemColor BUNDLE_COLOR = (stack, tintIndex) -> {
        if (tintIndex == 0) {
            return ItemBundle.getColor(stack);
        }

        return 0xFFFFFF;
    };
}
