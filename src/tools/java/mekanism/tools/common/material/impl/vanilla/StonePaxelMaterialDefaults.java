package mekanism.tools.common.material.impl.vanilla;

import mekanism.tools.common.material.VanillaPaxelMaterial;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.Tiers;

@MethodsReturnNonnullByDefault
public class StonePaxelMaterialDefaults extends VanillaPaxelMaterial {

    @Override
    public Tiers getVanillaTier() {
        return Tiers.STONE;
    }

    @Override
    public float getPaxelDamage() {
        return 8;
    }

    @Override
    public String getConfigCommentName() {
        return "Stone";
    }
}