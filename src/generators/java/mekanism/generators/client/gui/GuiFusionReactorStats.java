package mekanism.generators.client.gui;

import mekanism.api.text.EnumColor;
import mekanism.client.gui.element.tab.GuiEnergyTab;
import mekanism.common.MekanismLang;
import mekanism.common.inventory.container.tile.EmptyTileContainer;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.UnitDisplayUtils.TemperatureUnit;
import mekanism.common.util.text.EnergyDisplay;
import mekanism.generators.client.gui.element.GuiFusionReactorTab;
import mekanism.generators.client.gui.element.GuiFusionReactorTab.FusionReactorTab;
import mekanism.generators.common.GeneratorsLang;
import mekanism.generators.common.content.fusion.FusionReactorMultiblockData;
import mekanism.generators.common.tile.fusion.TileEntityFusionReactorController;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

import java.text.NumberFormat;
import java.util.Arrays;

public class GuiFusionReactorStats extends GuiFusionReactorInfo {

    private static final NumberFormat nf = NumberFormat.getIntegerInstance();

    public GuiFusionReactorStats(EmptyTileContainer<TileEntityFusionReactorController> container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);
    }

    @Override
    public void init() {
        super.init();
        addButton(new GuiEnergyTab(() -> {
            FusionReactorMultiblockData multiblock = tile.getMultiblock();
            return Arrays.asList(MekanismLang.STORING.translate( EnergyDisplay.of(multiblock.energyContainer.getEnergy(), multiblock.energyContainer.getMaxEnergy())),
                  GeneratorsLang.PRODUCING_AMOUNT.translate(EnergyDisplay.of(multiblock.getPassiveGeneration(false, true))));
        }, this));
        addButton(new GuiFusionReactorTab(this, tile, FusionReactorTab.HEAT));
        addButton(new GuiFusionReactorTab(this, tile, FusionReactorTab.FUEL));
    }

    @Override
    protected void drawForegroundText(int mouseX, int mouseY) {
        drawTitleText(GeneratorsLang.FUSION_REACTOR.translate(), 5);
        FusionReactorMultiblockData multiblock = tile.getMultiblock();
        if (multiblock.isFormed()) {
            drawString(GeneratorsLang.REACTOR_PASSIVE.translateColored(EnumColor.DARK_GREEN), 6, 26, titleTextColor());
            drawTextScaledBound(GeneratorsLang.REACTOR_MIN_INJECTION.translate(multiblock.getMinInjectionRate(false)), 16, 36, titleTextColor(), 156);
            drawTextScaledBound(GeneratorsLang.REACTOR_IGNITION.translate(MekanismUtils.getTemperatureDisplay(multiblock.getIgnitionTemperature(false),
                  TemperatureUnit.KELVIN, true)), 16, 46, titleTextColor(), 156);
            drawTextScaledBound(GeneratorsLang.REACTOR_MAX_PLASMA.translate(MekanismUtils.getTemperatureDisplay(multiblock.getMaxPlasmaTemperature(false),
                  TemperatureUnit.KELVIN, true)), 16, 56, titleTextColor(), 156);
            drawTextScaledBound(GeneratorsLang.REACTOR_MAX_CASING.translate(MekanismUtils.getTemperatureDisplay(multiblock.getMaxCasingTemperature(false),
                  TemperatureUnit.KELVIN, true)), 16, 66, titleTextColor(), 156);
            drawTextScaledBound(GeneratorsLang.REACTOR_PASSIVE_RATE.translate(EnergyDisplay.of(multiblock.getPassiveGeneration(false, false))),
                  16, 76, titleTextColor(), 156);

            drawString(GeneratorsLang.REACTOR_ACTIVE.translateColored(EnumColor.DARK_BLUE), 6, 92, titleTextColor());
            drawTextScaledBound(GeneratorsLang.REACTOR_MIN_INJECTION.translate(multiblock.getMinInjectionRate(true)), 16, 102, titleTextColor(), 156);
            drawTextScaledBound(GeneratorsLang.REACTOR_IGNITION.translate(MekanismUtils.getTemperatureDisplay(multiblock.getIgnitionTemperature(true),
                  TemperatureUnit.KELVIN, true)), 16, 112, titleTextColor(), 156);
            drawTextScaledBound(GeneratorsLang.REACTOR_MAX_PLASMA.translate(MekanismUtils.getTemperatureDisplay(multiblock.getMaxPlasmaTemperature(true),
                  TemperatureUnit.KELVIN, true)), 16, 122, titleTextColor(), 156);
            drawTextScaledBound(GeneratorsLang.REACTOR_MAX_CASING.translate(MekanismUtils.getTemperatureDisplay(multiblock.getMaxCasingTemperature(true),
                  TemperatureUnit.KELVIN, true)), 16, 132, titleTextColor(), 156);
            drawTextScaledBound(GeneratorsLang.REACTOR_PASSIVE_RATE.translate(EnergyDisplay.of(multiblock.getPassiveGeneration(true, false))),
                  16, 142, titleTextColor(), 156);
            drawTextScaledBound(GeneratorsLang.REACTOR_STEAM_PRODUCTION.translate(nf.format(multiblock.getSteamPerTick(false))), 16, 152, titleTextColor(), 156);
        }
        super.drawForegroundText(mouseX, mouseY);
    }
}