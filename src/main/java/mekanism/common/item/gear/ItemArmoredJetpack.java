package mekanism.common.item.gear;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import mcp.MethodsReturnNonnullByDefault;
import mekanism.client.render.armor.CustomArmor;
import mekanism.client.render.armor.JetpackArmor;
import mekanism.client.render.item.ISTERProvider;
import mekanism.common.Mekanism;
import mekanism.common.config.MekanismConfig;
import mekanism.common.lib.attribute.AttributeCache;
import mekanism.common.lib.attribute.IAttributeRefresher;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemArmoredJetpack extends ItemJetpack {

    private static final ArmoredJetpackMaterial ARMORED_JETPACK_MATERIAL = new ArmoredJetpackMaterial();

    public ItemArmoredJetpack(Properties properties) {
        super(ARMORED_JETPACK_MATERIAL, properties.setISTER(ISTERProvider::armoredJetpack));
    }

    @Override
    public int getDamageReduceAmount() {
        return getArmorMaterial().getDamageReductionAmount(getEquipmentSlot());
    }

    @Override
    public float getToughness() {
        return getArmorMaterial().getToughness();
    }

    @Nonnull
    @Override
    @OnlyIn(Dist.CLIENT)
    public CustomArmor getGearModel() {
        return JetpackArmor.ARMORED_JETPACK;
    }

    @ParametersAreNonnullByDefault
    @MethodsReturnNonnullByDefault
    private static class ArmoredJetpackMaterial extends JetpackMaterial {

        @Override
        public int getDamageReductionAmount(EquipmentSlotType slotType) {
            return slotType == EquipmentSlotType.CHEST ? MekanismConfig.gear.armoredJetpackArmor.get() : 0;
        }

        @Override
        public String getName() {
            return Mekanism.MODID + ":jetpack_armored";
        }

        @Override
        public float getToughness() {
            return MekanismConfig.gear.armoredJetpackToughness.get();
        }
    }
}