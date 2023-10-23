package surreal.bundles;

import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import surreal.bundles.utils.HashStrategies;

import java.util.Set;

@Config(modid = Bundles.MODID)
public class ModConfig {

    @Config.Name("Bundle Size")
    @Config.Comment("Amounts of items bundle can get")
    @Config.RangeInt(min = 1)
    public static int bundleLimit = 64;

    @Config.Name("Bundle Levels")
    @Config.Comment("Amount of texture changes.")
    @Config.RangeInt(min = 0)
    public static int bundleLevels = 1;

    @Config.Name("Custom Colors")
    @Config.Comment("Allows mixing colors like leather armor")
    public static boolean allowCustomColors = false;

    @Config.Name("Is Blacklist")
    @Config.Comment("Is item list blacklist or not")
    public static boolean isBlackList = true;

    @Config.Name("Allow Storage Items")
    @Config.Comment("Allow items to be added to bundles like shulker boxes, forestry backpacks etc.")
    public static boolean allowStorageItems = false;

    @Config.Name("Allow Tools")
    @Config.Comment("Allow Tools to be added to bundles.")
    public static boolean allowTools = true;

    @Config.Name("Item List")
    @Config.Comment("List of items that should be allowed to be added to bundle or not. If isBlacklist is true this will act as blacklist and whitelist if that's false.\nYou don't need to specify mod id if item is from Minecraft. Metadata and NBT is optional.\n<item_id>@<metadata>#<nbt> e.g. minecraft:dirt, dye@15, thermalfoundation:material@19, mod:examplewithtag@15#{integer:2}")
    public static String[] itemList = new String[] {};

    @Config.Name("Respect Items Max Stack Size")
    @Config.Comment("Respect maximum stack sizes of items")
    public static boolean respectStackSize = true;

    public static Set<ItemStack> getItemList() {
        if (itemList.length > 0) {
            Set<ItemStack> ret = new ObjectOpenCustomHashSet<>(HashStrategies.ITEMSTACK_STRATEGY);
            String metaIndex = "@";
            String tagIndex = "#";

            Logger logger = LogManager.getLogger(Bundles.MODID);

            int index = 1;
            for (String str : itemList) {
                str = str.trim();

                Item item;
                int meta = 0;
                NBTTagCompound tag = null;

                if (str.contains(metaIndex)) {
                    String[] itemAndMeta = str.split(metaIndex);

                    if (str.contains(tagIndex)) {
                        String[] metaAndTag = itemAndMeta[1].split(tagIndex);

                        meta = Integer.parseInt(itemAndMeta[0]);

                        try {
                            tag = JsonToNBT.getTagFromJson(metaAndTag[1]);
                        } catch (NBTException e) {
                            logger.error("Tag of item in config list at {} is set wrong, please fix it immediately!", index);
                        }
                    } else {
                        meta = Integer.parseInt(itemAndMeta[1]);
                    }

                    str = itemAndMeta[0];
                }

                item = Item.getByNameOrId(str);

                ItemStack stack;

                if (item == null || item == Items.AIR) {
                    logger.error("ID of item in config item list at {} is set wrong, please fix it immediately!", index);
                    stack = ItemStack.EMPTY;
                } else stack = new ItemStack(item, 1, meta);

                if (!stack.isEmpty()) {
                    stack.setTagCompound(tag);
                    ret.add(stack);
                }

                index++;
            }

            return ret;
        } else
            return new ObjectOpenHashSet<>();
    }
}
