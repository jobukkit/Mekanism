package mekanism.tools.common.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.common.config.value.CachedIntValue;
import mekanism.tools.client.render.GlowArmor;
import mekanism.tools.common.IHasRepairType;
import mekanism.tools.common.ToolsLang;
import mekanism.common.lib.attribute.AttributeCache;
import mekanism.common.lib.attribute.IAttributeRefresher;
import mekanism.tools.common.material.MaterialCreator;
import mekanism.tools.common.registries.ToolsItems;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemMekanismArmor extends ArmorItem implements IHasRepairType {

    private final MaterialCreator material;

    public ItemMekanismArmor(MaterialCreator material, EquipmentSlotType slot, Item.Properties properties, boolean makesPiglinsNeutral) {
        super(material, slot, properties);
        this.material = material;
        CachedIntValue armorConfig;
        if (slot == EquipmentSlotType.FEET) {
            armorConfig = material.bootArmor;
        } else if (slot == EquipmentSlotType.LEGS) {
            armorConfig = material.leggingArmor;
        } else if (slot == EquipmentSlotType.CHEST) {
            armorConfig = material.chestplateArmor;
        } else if (slot == EquipmentSlotType.HEAD) {
            armorConfig = material.helmetArmor;
        } else {
            throw new IllegalArgumentException("Invalid slot type for armor");
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flag) {
        tooltip.add(ToolsLang.HP.translate(stack.getMaxDamage() - stack.getDamage()));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BipedModel getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType armorSlot, BipedModel _default) {
        if (itemStack.getItem() == ToolsItems.REFINED_GLOWSTONE_HELMET.getItem() || itemStack.getItem() == ToolsItems.REFINED_GLOWSTONE_CHESTPLATE.getItem()
            || itemStack.getItem() == ToolsItems.REFINED_GLOWSTONE_LEGGINGS.getItem() || itemStack.getItem() == ToolsItems.REFINED_GLOWSTONE_BOOTS.getItem()) {
            return GlowArmor.getGlow(armorSlot);
        }
        return super.getArmorModel(entityLiving, itemStack, armorSlot, _default);
    }

    @Nonnull
    @Override
    public Ingredient getRepairMaterial() {
        return getArmorMaterial().getRepairMaterial();
    }

    @Override
    public int getDamageReduceAmount() {
        return getArmorMaterial().getDamageReductionAmount(getEquipmentSlot());
    }

    @Override
    public float getToughness() {
        return getArmorMaterial().getToughness();
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return material.getDurability(getEquipmentSlot());
    }

    @Override
    public boolean isDamageable() {
        return material.getDurability(getEquipmentSlot()) > 0;
    }

    @Nonnull
    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(@Nonnull EquipmentSlotType slot) {
        Multimap<String, AttributeModifier> attributes = HashMultimap.create();
        if (slot == getEquipmentSlot()) {
            UUID modifier = ARMOR_MODIFIERS[slot.getIndex()];
            attributes.put(SharedMonsterAttributes.ARMOR.getName(), new AttributeModifier(modifier, "Armor modifier", getDamageReduceAmount(), AttributeModifier.Operation.ADDITION));
            attributes.put(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName(), new AttributeModifier(modifier, "Armor toughness", getToughness(), Operation.ADDITION));
        }
        return attributes;
    }
}