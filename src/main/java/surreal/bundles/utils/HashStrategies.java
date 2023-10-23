package surreal.bundles.utils;

import it.unimi.dsi.fastutil.Hash;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Objects;

public class HashStrategies {

    public static Hash.Strategy<ItemStack> ITEMSTACK_STRATEGY = new Hash.Strategy<ItemStack>() {
        @Override
        public int hashCode(ItemStack o) {
            if (o == null || o.isEmpty()) return 0;

            int i = Item.getIdFromItem(o.getItem()) << 17;
            i |= (o.getMetadata() + 1) << 31;
            if (o.hasTagCompound()) i |= Objects.hashCode(o.getTagCompound());

            return i;
        }

        @Override
        public boolean equals(ItemStack a, ItemStack b) {
            if (a == null || b == null) return false;
            boolean nbt = !a.hasTagCompound() || Objects.requireNonNull(a.getTagCompound()).equals(Objects.requireNonNull(b.getTagCompound()));
            return a.isItemEqual(b) && nbt;
        }
    };
}
