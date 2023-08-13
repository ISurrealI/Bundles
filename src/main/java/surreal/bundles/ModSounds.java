package surreal.bundles;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class ModSounds {

    public static SoundEvent DROP_CONTENTS;
    public static SoundEvent INSERT;
    public static SoundEvent REMOVE;

    public static void init() {
        DROP_CONTENTS = create("item.bundle.drop_contents");
        INSERT = create("item.bundle.insert");
        REMOVE = create("item.bundle.remove");
    }

    private static SoundEvent create(String name) {
        return new SoundEvent(new ResourceLocation(Bundles.MODID, name));
    }
}
