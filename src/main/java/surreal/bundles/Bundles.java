package surreal.bundles;

import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.Side;
import surreal.bundles.client.ClientProxy;
import surreal.bundles.client.TooltipEvent;
import surreal.bundles.config.ConfigHandler;
import surreal.bundles.items.ItemBundle;
import surreal.bundles.recipes.RecipeBundleColoring;

@SuppressWarnings("unused")
@Mod(modid = Bundles.MODID, name = "Bundles", version = Tags.VERSION, guiFactory = "surreal.bundles.config.ConfigGuiFactory", dependencies = "required-after:mixinbooter@[4.2,);after:mousetweaks@[3.0,)")
public class Bundles {

    public static final String MODID = "bundles";

    public static ItemBundle BUNDLE = registerItem("bundle", new ItemBundle());

    @Mod.EventHandler
    public void construction(FMLConstructionEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(ConfigHandler.class);

        if (FMLLaunchHandler.side() == Side.CLIENT) {
            MinecraftForge.EVENT_BUS.register(new ClientProxy());
            MinecraftForge.EVENT_BUS.register(new TooltipEvent());
        }
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ConfigHandler.initialize(event);
    }

    // Registry
    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(BUNDLE);
    }

    @SubscribeEvent
    public void registerRecipe(RegistryEvent.Register<IRecipe> event) {
        ItemStack string = new ItemStack(Items.STRING);
        ItemStack hide = new ItemStack(Items.RABBIT_HIDE);

        GameRegistry.addShapedRecipe(BUNDLE.getRegistryName(), null, new ItemStack(BUNDLE), "ABA", "B B", "BBB", 'A', string, 'B', hide);

        if (!ConfigHandler.allowCustomColors) {
            for (int i = 0; i < 16; i++) {
                EnumDyeColor color = EnumDyeColor.byDyeDamage(i);
                ItemStack stack = new ItemStack(BUNDLE);
                ItemBundle.setColor(stack, color);
                GameRegistry.addShapelessRecipe(new ResourceLocation(BUNDLE.getRegistryName() + "_" + color.getName()), null, stack, Ingredient.fromItem(BUNDLE), Ingredient.fromStacks(new ItemStack(Items.DYE, 1, color.getDyeDamage())));
            }
        } else event.getRegistry().register(new RecipeBundleColoring().setRegistryName(MODID, "bundle_coloring"));
    }

    @SubscribeEvent
    public void registerSound(RegistryEvent.Register<SoundEvent> event) {
        ModSounds.init();
    }

    private static <T extends Item> T registerItem(String name, T item) {
        item.setRegistryName(MODID, name).setTranslationKey(MODID + "." + name);
        return item;
    }
}
