package surreal.bundles.items;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import surreal.bundles.Bundles;
import surreal.bundles.ModConfig;
import surreal.bundles.ModSounds;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;

import static surreal.bundles.utils.NBTConstants.ITEMS;
import static surreal.bundles.utils.NBTConstants.ITEM_AMOUNT;

public class ItemBundle extends Item {

    private static final IItemPropertyGetter BUNDLE_PROPERTY = (stack, worldIn, entityIn) -> {
        int amount = getItemAmount(stack);

        if (amount == 0) return 0F;

        float a = (float) amount / ModConfig.bundleLimit;
        float b = 1F / ModConfig.bundleLevels;

        int level = (int) (a / b) + 1;
        return b * level;
    };

    public ItemBundle() {
        setMaxStackSize(1);
        setCreativeTab(CreativeTabs.MISC);
        addPropertyOverride(new ResourceLocation(Bundles.MODID, "filled"), BUNDLE_PROPERTY);
    }

    // Tooltip
    @ParametersAreNonnullByDefault
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (stack.hasTagCompound()) {
            int size = getItemAmount(stack);
            String str = I18n.format("item.bundles.bundle.fullness", size, ModConfig.bundleLimit);

            if (size == ModConfig.bundleLimit) tooltip.add(TextFormatting.DARK_RED + str);
            else tooltip.add(str);
        }
    }

    // Bar
    @Override
    public double getDurabilityForDisplay(@Nonnull ItemStack stack) {
        return 1D - ((double) getItemAmount(stack) / ModConfig.bundleLimit);
    }

    @Override
    public int getRGBDurabilityForDisplay(@Nonnull ItemStack stack) {
        return 0x6C69E2;
    }

    @Override
    public boolean showDurabilityBar(@Nonnull ItemStack stack) {
        return getItemAmount(stack) > 0;
    }

    // Events
    @ParametersAreNonnullByDefault
    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {

        ItemStack bundle = playerIn.getHeldItem(handIn);
        if (!bundle.hasTagCompound() || !playerIn.isSneaking()) return super.onItemRightClick(worldIn, playerIn, handIn);

        if (getItemAmount(bundle) > 0) {
            ItemStack[] items = getItems(bundle);

            if (items != null && items.length > 0) {
                if (!worldIn.isRemote) {
                    for (ItemStack stack : items) {
                        EntityItem item = new EntityItem(worldIn, playerIn.posX, playerIn.posY + 0.3D, playerIn.posZ, stack);
                        item.setNoPickupDelay();
                        worldIn.spawnEntity(item);
                    }

                    NBTTagCompound tag = Objects.requireNonNull(bundle.getTagCompound());
                    tag.removeTag(ITEMS);
                    tag.setTag(ITEMS, new NBTTagList());

                    setAmount(bundle, 0);

                    return new ActionResult<>(EnumActionResult.SUCCESS, bundle);

                } else
                    playerIn.playSound(ModSounds.DROP_CONTENTS, 1F, 1F);
            }
        }


        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    // NBT Utilities
    public static void addItem(ItemStack bundle, ItemStack stack) {
        NBTTagList list = getItemList(bundle);

        if (list == null) {
            NBTTagCompound compound = getOrCreateTag(bundle);
            list = new NBTTagList();
            compound.setTag(ITEMS, list);
        }

        int empty = getEmptyAmount(bundle);

        int itemSize = getItemstackAmount(stack);
        int itemCount = stack.getCount();

        int countToAdd = Math.min(itemCount, empty / itemSize);
        if (countToAdd == 0) return;

        int amountToAdd = Math.min(itemSize * itemCount, itemSize * countToAdd);

        addAmount(bundle, amountToAdd);

        NBTTagCompound compound = stack.serializeNBT();
        compound.setByte("Count", (byte) countToAdd);

        list.appendTag(compound);

        stack.setCount(stack.getCount() - countToAdd);
    }

    public static ItemStack removeItem(ItemStack bundle, int slot, int amount) {
        NBTTagList list = getItemList(bundle);
        if (list == null) {
            NBTTagCompound compound = getOrCreateTag(bundle);
            list = new NBTTagList();
            compound.setTag(ITEMS, list);
            return ItemStack.EMPTY;
        }

        slot = checkSlot(bundle, slot);

        NBTTagCompound stackTag = list.getCompoundTagAt(slot);
        ItemStack stack = new ItemStack(stackTag);
        if (stack.isEmpty()) return stack;

        amount = Math.min(stack.getCount(), amount);
        if (amount == stack.getCount()) {
            list.removeTag(slot);
        } else {
            stackTag.setByte("Count", (byte) (stack.getCount() - amount));
            stack.setCount(amount);
        }

        addAmount(bundle, -getItemstackAmount(stack) * stack.getCount());

        return stack;
    }

    public static int getSlotCount(ItemStack bundle) {
        NBTTagList list = getItemList(bundle);
        if (list == null) return 0;
        return list.tagCount();
    }

    public static int getEmptyAmount(ItemStack bundle) {
        return ModConfig.bundleLimit - getItemAmount(bundle);
    }

    public static int getItemAmount(ItemStack bundle) {
        NBTTagCompound tag = getOrCreateTag(bundle);
        if (!tag.isEmpty() && tag.hasKey(ITEM_AMOUNT)) return tag.getInteger(ITEM_AMOUNT);
        return 0;
    }

    public static boolean isBundleFull(ItemStack bundle) {
        return getItemAmount(bundle) == ModConfig.bundleLimit;
    }

    public static ItemStack[] getItems(ItemStack bundle) {
        NBTTagList list = getItemList(bundle);
        if (list == null) return null;

        int count = list.tagCount();

        if (count <= 0) return null;

        ItemStack[] array = new ItemStack[count];

        for (int i = 0; i < count; i++) {
            ItemStack stack = new ItemStack(list.getCompoundTagAt(i));
            array[i] = stack;
        }

        return array;
    }

    public static ItemStack getItem(ItemStack bundle, int slot) {
        NBTTagList list = getItemList(bundle);
        if (list == null) return ItemStack.EMPTY;
        int count = list.tagCount();
        if (count <= 0) return ItemStack.EMPTY;

        slot = checkSlot(bundle, slot);
        return new ItemStack(list.getCompoundTagAt(slot));
    }

    private static void setAmount(ItemStack bundle, int amount) {
        NBTTagCompound tag = getOrCreateTag(bundle);
        tag.setInteger(ITEM_AMOUNT, amount);
    }

    private static void addAmount(ItemStack bundle, int amount) {
        NBTTagCompound tag = getOrCreateTag(bundle);
        if (tag.hasKey(ITEM_AMOUNT)) amount += tag.getInteger(ITEM_AMOUNT);
        tag.setInteger(ITEM_AMOUNT, amount);
    }

    private static int getItemstackAmount(ItemStack stack) {
        int size = 1;
        if (ModConfig.respectStackSize && stack.getMaxStackSize() < 64) size = 64 / stack.getMaxStackSize();
        return size;
    }

    private static NBTTagList getItemList(ItemStack bundle) {
        NBTTagCompound tag = getOrCreateTag(bundle);
        if (tag.isEmpty() || !tag.hasKey(ITEMS) || getItemAmount(bundle) <= 0) return null;
        return tag.getTagList(ITEMS, Constants.NBT.TAG_COMPOUND);
    }

    private static int checkSlot(ItemStack bundle, int slot) {
        NBTTagList list = getItemList(bundle);
        if (list == null || list.tagCount() == 0) return -1;
        if (slot >= list.tagCount()) slot = list.tagCount() - 1;
        else if (slot < 0) slot = 0;
        return slot;
    }

    @Nonnull
    private static NBTTagCompound getOrCreateTag(ItemStack bundle) {
        if (!bundle.hasTagCompound()) bundle.setTagCompound(new NBTTagCompound());
        return Objects.requireNonNull(bundle.getTagCompound());
    }
}
