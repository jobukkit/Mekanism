package mekanism.common.item.gear;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.Action;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.functions.FloatSupplier;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.ICustomModule.ModuleDamageAbsorbInfo;
import mekanism.api.gear.IModule;
import mekanism.api.inventory.AutomationType;
import mekanism.api.math.FloatingLong;
import mekanism.api.math.FloatingLongSupplier;
import mekanism.api.text.EnumColor;
import mekanism.client.key.MekKeyHandler;
import mekanism.client.key.MekanismKeyHandler;
import mekanism.client.render.armor.CustomArmor;
import mekanism.client.render.armor.MekaSuitArmor;
import mekanism.common.Mekanism;
import mekanism.common.MekanismLang;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.ItemCapabilityWrapper;
import mekanism.common.capabilities.chemical.item.RateLimitMultiTankGasHandler;
import mekanism.common.capabilities.chemical.item.RateLimitMultiTankGasHandler.GasTankSpec;
import mekanism.common.capabilities.energy.BasicEnergyContainer;
import mekanism.common.capabilities.energy.item.RateLimitEnergyHandler;
import mekanism.common.capabilities.laser.item.LaserDissipationHandler;
import mekanism.common.capabilities.radiation.item.RadiationShieldingHandler;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.gear.IModuleContainerItem;
import mekanism.common.content.gear.Module;
import mekanism.common.content.gear.mekasuit.ModuleJetpackUnit;
import mekanism.common.content.gear.shared.ModuleEnergyUnit;
import mekanism.common.item.gear.ItemJetpack.JetpackMode;
import mekanism.common.item.interfaces.IModeItem;
import mekanism.common.registries.MekanismGases;
import mekanism.common.registries.MekanismModules;
import mekanism.common.util.StorageUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemStack.TooltipDisplayFlags;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class ItemMekaSuitArmor extends ItemSpecialArmor implements IModuleContainerItem, IModeItem {

    private static final Set<DamageSource> ALWAYS_SUPPORTED_SOURCES = new LinkedHashSet<>(Arrays.asList(
          DamageSource.ANVIL, DamageSource.CACTUS, DamageSource.CRAMMING, DamageSource.DRAGON_BREATH, DamageSource.DRY_OUT,
          DamageSource.FALL, DamageSource.FALLING_BLOCK, DamageSource.FLY_INTO_WALL, DamageSource.GENERIC,
          DamageSource.HOT_FLOOR, DamageSource.IN_FIRE, DamageSource.IN_WALL, DamageSource.LAVA, DamageSource.LIGHTNING_BOLT,
          DamageSource.ON_FIRE, DamageSource.SWEET_BERRY_BUSH, DamageSource.WITHER));

    public static Set<DamageSource> getSupportedSources() {
        return Collections.unmodifiableSet(ALWAYS_SUPPORTED_SOURCES);
    }

    private static final MekaSuitMaterial MEKASUIT_MATERIAL = new MekaSuitMaterial();

    private final Set<GasTankSpec> gasTankSpecs = new HashSet<>();
    private final float absorption;
    //Full laser dissipation causes 3/4 of the energy to be dissipated and the remaining energy to be refracted
    private final double laserDissipation;
    private final double laserRefraction;

    public ItemMekaSuitArmor(EquipmentSlotType slot, Properties properties) {
        super(MEKASUIT_MATERIAL, slot, properties.rarity(Rarity.EPIC).setNoRepair().stacksTo(1));
        if (slot == EquipmentSlotType.HEAD) {
            gasTankSpecs.add(GasTankSpec.createFillOnly(MekanismConfig.gear.mekaSuitNutritionalTransferRate, MekanismConfig.gear.mekaSuitNutritionalMaxStorage,
                  (gas, automationType, stack) -> hasModule(stack, MekanismModules.NUTRITIONAL_INJECTION_UNIT), gas -> gas == MekanismGases.NUTRITIONAL_PASTE.get()));
            absorption = 0.15F;
            laserDissipation = 0.15;
            laserRefraction = 0.2;
        } else if (slot == EquipmentSlotType.CHEST) {
            gasTankSpecs.add(GasTankSpec.createFillOnly(MekanismConfig.gear.mekaSuitJetpackTransferRate, MekanismConfig.gear.mekaSuitJetpackMaxStorage,
                  (gas, automationType, stack) -> hasModule(stack, MekanismModules.JETPACK_UNIT), gas -> gas == MekanismGases.HYDROGEN.get()));
            absorption = 0.4F;
            laserDissipation = 0.3;
            laserRefraction = 0.4;
        } else if (slot == EquipmentSlotType.LEGS) {
            absorption = 0.3F;
            laserDissipation = 0.1875;
            laserRefraction = 0.25;
        } else if (slot == EquipmentSlotType.FEET) {
            absorption = 0.15F;
            laserDissipation = 0.1125;
            laserRefraction = 0.15;
        } else {
            throw new IllegalArgumentException("Unknown Equipment Slot Type");
        }
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        // safety check
        return 0;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@Nonnull ItemStack stack, World world, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flag) {
        if (MekKeyHandler.isKeyPressed(MekanismKeyHandler.detailsKey)) {
            addModuleDetails(stack, tooltip);
        } else {
            StorageUtils.addStoredEnergy(stack, tooltip, true);
            if (!gasTankSpecs.isEmpty()) {
                StorageUtils.addStoredGas(stack, tooltip, true, false);
            }
            tooltip.add(MekanismLang.HOLD_FOR_MODULES.translateColored(EnumColor.GRAY, EnumColor.INDIGO, MekanismKeyHandler.detailsKey.getTranslatedKeyMessage()));
        }
    }

    @Override
    public boolean makesPiglinsNeutral(@Nonnull ItemStack stack, @Nonnull LivingEntity wearer) {
        return true;
    }

    @Override
    public boolean isEnderMask(@Nonnull ItemStack stack, @Nonnull PlayerEntity player, @Nonnull EndermanEntity enderman) {
        return getSlot() == EquipmentSlotType.HEAD;
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        //TODO: Eventually look into making it so that we can have a "secondary durability" bar for rendering things like stored gas
        return StorageUtils.getEnergyDurabilityForDisplay(stack);
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return MekanismConfig.client.energyColor.get();
    }

    @Override
    public boolean isFoil(@Nonnull ItemStack stack) {
        return !stack.isEmpty() && super.isFoil(stack) && IModuleContainerItem.hasOtherEnchants(stack);
    }

    @Override
    public void fillItemCategory(@Nonnull ItemGroup group, @Nonnull NonNullList<ItemStack> items) {
        super.fillItemCategory(group, items);
        if (allowdedIn(group)) {
            ItemStack stack = new ItemStack(this);
            items.add(StorageUtils.getFilledEnergyVariant(stack, getMaxEnergy(stack)));
        }
    }

    @Override
    public void onArmorTick(ItemStack stack, World world, PlayerEntity player) {
        super.onArmorTick(stack, world, player);
        for (Module<?> module : getModules(stack)) {
            module.tick(player);
        }
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
        stack.hideTooltipPart(TooltipDisplayFlags.MODIFIERS);
        //Note: We interact with this capability using "manual" as the automation type, to ensure we can properly bypass the energy limit for extracting
        // Internal is used by the "null" side, which is what will get used for most items
        ItemCapabilityWrapper wrapper = new ItemCapabilityWrapper(stack, RateLimitEnergyHandler.create(() -> getChargeRate(stack), () -> getMaxEnergy(stack),
              BasicEnergyContainer.manualOnly, BasicEnergyContainer.alwaysTrue),
              RadiationShieldingHandler.create(item -> isModuleEnabled(item, MekanismModules.RADIATION_SHIELDING_UNIT) ? ItemHazmatSuitArmor.getShieldingByArmor(slot) : 0),
              LaserDissipationHandler.create(item -> isModuleEnabled(item, MekanismModules.LASER_DISSIPATION_UNIT) ? laserDissipation : 0,
                    item -> isModuleEnabled(item, MekanismModules.LASER_DISSIPATION_UNIT) ? laserRefraction : 0));
        if (!gasTankSpecs.isEmpty()) {
            wrapper.add(RateLimitMultiTankGasHandler.create(gasTankSpecs));
        }
        return wrapper;
    }

    @Nonnull
    public GasStack useGas(ItemStack stack, Gas type, long amount) {
        Optional<IGasHandler> capability = stack.getCapability(Capabilities.GAS_HANDLER_CAPABILITY).resolve();
        if (capability.isPresent()) {
            IGasHandler gasHandlerItem = capability.get();
            return gasHandlerItem.extractChemical(new GasStack(type, amount), Action.EXECUTE);
        }
        return GasStack.EMPTY;
    }

    public GasStack getContainedGas(ItemStack stack, Gas type) {
        Optional<IGasHandler> capability = stack.getCapability(Capabilities.GAS_HANDLER_CAPABILITY).resolve();
        if (capability.isPresent()) {
            IGasHandler gasHandlerItem = capability.get();
            for (int i = 0; i < gasHandlerItem.getTanks(); i++) {
                GasStack gasInTank = gasHandlerItem.getChemicalInTank(i);
                if (gasInTank.getType() == type) {
                    return gasInTank;
                }
            }
        }
        return GasStack.EMPTY;
    }

    @Override
    public void changeMode(@Nonnull PlayerEntity player, @Nonnull ItemStack stack, int shift, boolean displayChangeMessage) {
        for (Module<?> module : getModules(stack)) {
            if (module.handlesModeChange()) {
                module.changeMode(player, stack, shift, displayChangeMessage);
                return;
            }
        }
    }

    @Override
    public boolean supportsSlotType(ItemStack stack, @Nonnull EquipmentSlotType slotType) {
        return slotType == getSlot() && getModules(stack).stream().anyMatch(Module::handlesModeChange);
    }

    @Override
    public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
        if (slot == EquipmentSlotType.CHEST && !entity.isShiftKeyDown()) {
            //Don't allow elytra flight if the player is sneaking. This lets the player exit elytra flight early
            IModule<?> module = getModule(stack, MekanismModules.ELYTRA_UNIT);
            if (module != null && module.isEnabled() && module.canUseEnergy(entity, MekanismConfig.gear.mekaSuitElytraEnergyUsage.get())) {
                //If we can use the elytra, check if the jetpack unit is also installed, and if it is,
                // only mark that we can use the elytra if the jetpack is not set to hover or if it is if it has no hydrogen stored
                IModule<ModuleJetpackUnit> jetpack = getModule(stack, MekanismModules.JETPACK_UNIT);
                return jetpack == null || !jetpack.isEnabled() || jetpack.getCustomInstance().getMode() != JetpackMode.HOVER ||
                       getContainedGas(stack, MekanismGases.HYDROGEN.get()).isEmpty();
            }
        }
        return false;
    }

    @Override
    public boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
        //Note: As canElytraFly is checked just before this we don't bother validating ahead of time we have the energy
        // or that we are the correct slot
        if (!entity.level.isClientSide && (flightTicks + 1) % 20 == 0) {
            IModule<?> module = getModule(stack, MekanismModules.ELYTRA_UNIT);
            if (module != null && module.isEnabled()) {
                module.useEnergy(entity, MekanismConfig.gear.mekaSuitElytraEnergyUsage.get());
            }
        }
        return true;
    }

    @Nonnull
    @Override
    @OnlyIn(Dist.CLIENT)
    public CustomArmor getGearModel() {
        switch (getSlot()) {
            case HEAD:
                return MekaSuitArmor.HELMET;
            case CHEST:
                return MekaSuitArmor.BODYARMOR;
            case LEGS:
                return MekaSuitArmor.PANTS;
            default:
                return MekaSuitArmor.BOOTS;
        }
    }

    private FloatingLong getMaxEnergy(ItemStack stack) {
        IModule<ModuleEnergyUnit> module = getModule(stack, MekanismModules.ENERGY_UNIT);
        return module == null ? MekanismConfig.gear.mekaSuitBaseEnergyCapacity.get() : module.getCustomInstance().getEnergyCapacity(module);
    }

    private FloatingLong getChargeRate(ItemStack stack) {
        IModule<ModuleEnergyUnit> module = getModule(stack, MekanismModules.ENERGY_UNIT);
        return module == null ? MekanismConfig.gear.mekaSuitBaseChargeRate.get() : module.getCustomInstance().getChargeRate(module);
    }

    public static float getDamageAbsorbed(PlayerEntity player, DamageSource source, float amount) {
        return getDamageAbsorbed(player, source, amount, null);
    }

    public static boolean tryAbsorbAll(PlayerEntity player, DamageSource source, float amount) {
        List<Runnable> energyUsageCallbacks = new ArrayList<>(4);
        if (getDamageAbsorbed(player, source, amount, energyUsageCallbacks) >= 1) {
            //If we can fully absorb it, actually use the energy from the various pieces and then return that we absorbed it all
            for (Runnable energyUsageCallback : energyUsageCallbacks) {
                energyUsageCallback.run();
            }
            return true;
        }
        return false;
    }

    private static float getDamageAbsorbed(PlayerEntity player, DamageSource source, float amount, @Nullable List<Runnable> energyUseCallbacks) {
        if (amount <= 0) {
            return 0;
        }
        float ratioAbsorbed = 0;
        List<FoundArmorDetails> armorDetails = new ArrayList<>();
        //Start by looping the armor, allowing modules to absorb damage if they can
        for (ItemStack stack : player.inventory.armor) {
            if (!stack.isEmpty() && stack.getItem() instanceof ItemMekaSuitArmor) {
                IEnergyContainer energyContainer = StorageUtils.getEnergyContainer(stack, 0);
                if (energyContainer != null) {
                    FoundArmorDetails details = new FoundArmorDetails(energyContainer, (ItemMekaSuitArmor) stack.getItem());
                    armorDetails.add(details);
                    for (Module<?> module : details.armor.getModules(stack)) {
                        if (module.isEnabled()) {
                            ModuleDamageAbsorbInfo damageAbsorbInfo = getModuleDamageAbsorbInfo(module, source);
                            if (damageAbsorbInfo != null) {
                                float absorption = damageAbsorbInfo.getAbsorptionRatio().getAsFloat();
                                ratioAbsorbed += absorbDamage(details.usageInfo, amount, absorption, ratioAbsorbed, damageAbsorbInfo.getEnergyCost());
                                if (ratioAbsorbed >= 1) {
                                    //If we have fully absorbed the damage, stop checking/trying to absorb more
                                    break;
                                }
                            }
                        }
                    }
                    if (ratioAbsorbed >= 1) {
                        //If we have fully absorbed the damage, stop checking/trying to absorb more
                        break;
                    }
                }
            }
        }
        if (ratioAbsorbed < 1) {
            //If we haven't fully absorbed it check the individual pieces of armor for if they can absorb any
            FloatSupplier absorbRatio = null;
            for (FoundArmorDetails details : armorDetails) {
                if (absorbRatio == null) {
                    //If we haven't looked up yet if we can absorb the damage type and if we can't
                    // stop checking if the armor is able to
                    if (!ALWAYS_SUPPORTED_SOURCES.contains(source) && source.isBypassArmor()) {
                        break;
                    }
                    // Next lookup the ratio at which we can absorb the given damage type from the config
                    absorbRatio = MekanismConfig.gear.mekaSuitDamageRatios.getOrDefault(source, MekanismConfig.gear.mekaSuitUnspecifiedDamageRatio);
                    if (absorbRatio.getAsFloat() == 0) {
                        //If the config specifies that the damage type shouldn't be blocked at all
                        // stop checking if the armor is able to
                        break;
                    }
                }
                float absorption = details.armor.absorption * absorbRatio.getAsFloat();
                ratioAbsorbed += absorbDamage(details.usageInfo, amount, absorption, ratioAbsorbed, MekanismConfig.gear.mekaSuitEnergyUsageDamage);
                if (ratioAbsorbed >= 1) {
                    //If we have fully absorbed the damage, stop checking/trying to absorb more
                    break;
                }
            }
        }
        for (FoundArmorDetails details : armorDetails) {
            //Use energy/or enqueue usage for each piece as needed
            if (!details.usageInfo.energyUsed.isZero()) {
                if (energyUseCallbacks == null) {
                    details.energyContainer.extract(details.usageInfo.energyUsed, Action.EXECUTE, AutomationType.MANUAL);
                } else {
                    energyUseCallbacks.add(() -> details.energyContainer.extract(details.usageInfo.energyUsed, Action.EXECUTE, AutomationType.MANUAL));
                }
            }
        }
        return Math.min(ratioAbsorbed, 1);
    }

    @Nullable
    private static <MODULE extends ICustomModule<MODULE>> ModuleDamageAbsorbInfo getModuleDamageAbsorbInfo(IModule<MODULE> module, DamageSource damageSource) {
        return module.getCustomInstance().getDamageAbsorbInfo(module, damageSource);
    }

    private static float absorbDamage(EnergyUsageInfo usageInfo, float amount, float absorption, float currentAbsorbed, FloatingLongSupplier energyCost) {
        //Cap the amount that we can absorb to how much we have left to absorb
        absorption = Math.min(1 - currentAbsorbed, absorption);
        float toAbsorb = amount * absorption;
        if (toAbsorb > 0) {
            FloatingLong usage = energyCost.get().multiply(toAbsorb);
            if (usage.isZero()) {
                //No energy is actually needed to absorb the damage, either because of the config
                // or how small the amount to absorb is
                return absorption;
            } else if (usageInfo.energyAvailable.greaterOrEqual(usage)) {
                //If we have more energy available than we need, increase how much energy we "used"
                // and decrease how much we have available.
                usageInfo.energyUsed = usageInfo.energyUsed.plusEqual(usage);
                usageInfo.energyAvailable = usageInfo.energyAvailable.minusEqual(usage);
                return absorption;
            } else if (!usageInfo.energyAvailable.isZero()) {
                //Otherwise, if we have energy available but not as much as needed to fully absorb it
                // then we calculate what ratio we are able to block
                float absorbedPercent = usageInfo.energyAvailable.divide(usage).floatValue();
                usageInfo.energyUsed = usageInfo.energyUsed.plusEqual(usageInfo.energyAvailable);
                usageInfo.energyAvailable = FloatingLong.ZERO;
                return absorption * absorbedPercent;
            }
        }
        return 0;
    }

    //TODO - 1.18: Switch this to a record
    private static class FoundArmorDetails {

        private final IEnergyContainer energyContainer;
        private final EnergyUsageInfo usageInfo;
        private final ItemMekaSuitArmor armor;

        public FoundArmorDetails(IEnergyContainer energyContainer, ItemMekaSuitArmor armor) {
            this.energyContainer = energyContainer;
            this.usageInfo = new EnergyUsageInfo(energyContainer.getEnergy());
            this.armor = armor;
        }
    }

    private static class EnergyUsageInfo {

        private FloatingLong energyAvailable;
        private FloatingLong energyUsed = FloatingLong.ZERO;

        public EnergyUsageInfo(FloatingLong energyAvailable) {
            //Copy it so we can just use minusEquals without worry
            this.energyAvailable = energyAvailable.copy();
        }
    }

    // This is unused for the most part; toughness / damage reduction is handled manually, though it can fall back to netherite values
    protected static class MekaSuitMaterial extends BaseSpecialArmorMaterial {

        @Override
        public int getDefenseForSlot(@Nonnull EquipmentSlotType slot) {
            return ArmorMaterial.NETHERITE.getDefenseForSlot(slot);
        }

        @Override
        public float getToughness() {
            return ArmorMaterial.NETHERITE.getToughness();
        }

        @Override
        public float getKnockbackResistance() {
            return ArmorMaterial.NETHERITE.getKnockbackResistance();
        }

        @Nonnull
        @Override
        public String getName() {
            return Mekanism.MODID + ":mekasuit";
        }
    }
}