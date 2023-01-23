package mekanism.generators.client.gui;

import mekanism.client.gui.element.gauge.*;
import mekanism.client.gui.element.gauge.GuiNumberGauge.INumberInfoHandler;
import mekanism.client.gui.element.progress.GuiProgress;
import mekanism.client.gui.element.progress.ProgressType;
import mekanism.client.gui.element.tab.GuiEnergyTab;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.MekanismRenderer.FluidType;
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
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.text.ITextComponent;

import java.util.Arrays;

public class GuiFusionReactorHeat extends GuiFusionReactorInfo {

    private static final double MAX_LEVEL = 500_000_000;

    public GuiFusionReactorHeat(EmptyTileContainer<TileEntityFusionReactorController> container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);
    }

    @Override
    public void init() {
        super.init();
        addButton(new GuiEnergyTab(() -> {
            FusionReactorMultiblockData multiblock = tile.getMultiblock();
            return Arrays.asList(MekanismLang.STORING.translate(EnergyDisplay.of(multiblock.energyContainer.getEnergy(), multiblock.energyContainer.getMaxEnergy())),
                  GeneratorsLang.PRODUCING_AMOUNT.translate(EnergyDisplay.of(multiblock.getPassiveGeneration(false, true))));
        }, this));
        addButton(new GuiNumberGauge(new INumberInfoHandler() {
            @Override
            public TextureAtlasSprite getIcon() {
                return MekanismRenderer.getBaseFluidTexture(Fluids.LAVA, FluidType.STILL);
            }

            @Override
            public double getLevel() {
                return tile.getMultiblock().getLastPlasmaTemp();
            }

            @Override
            public double getScaledLevel() {
                return Math.min(1, getLevel() / MAX_LEVEL);
            }

            @Override
            public ITextComponent getText() {
                return GeneratorsLang.REACTOR_PLASMA.translate(MekanismUtils.getTemperatureDisplay(getLevel(), TemperatureUnit.KELVIN, true));
            }
        }, GaugeType.STANDARD, this, 7, 50));
        addButton(new GuiProgress(() -> {
            FusionReactorMultiblockData multiblock = tile.getMultiblock();
            return multiblock.getLastPlasmaTemp() > multiblock.getLastCaseTemp() ? 1 : 0;
        }, ProgressType.SMALL_RIGHT, this, 29, 76));
        addButton(new GuiNumberGauge(new INumberInfoHandler() {
            @Override
            public TextureAtlasSprite getIcon() {
                return MekanismRenderer.getBaseFluidTexture(Fluids.LAVA, FluidType.STILL);
            }

            @Override
            public double getLevel() {
                return tile.getMultiblock().getLastCaseTemp();
            }

            @Override
            public double getScaledLevel() {
                return Math.min(1, getLevel() / MAX_LEVEL);
            }

            @Override
            public ITextComponent getText() {
                return GeneratorsLang.REACTOR_CASE.translate(MekanismUtils.getTemperatureDisplay(getLevel(), TemperatureUnit.KELVIN, true));
            }
        }, GaugeType.STANDARD, this, 61, 50));
        addButton(new GuiProgress(() -> tile.getMultiblock().getCaseTemp() > 0 ? 1 : 0, ProgressType.SMALL_RIGHT, this, 83, 61));
        addButton(new GuiProgress(() -> {
            FusionReactorMultiblockData multiblock = tile.getMultiblock();
            return multiblock.getCaseTemp() > 0 && !multiblock.waterTank.isEmpty() && multiblock.steamTank.getStored() < multiblock.steamTank.getCapacity() ? 1 : 0;
        }, ProgressType.SMALL_RIGHT, this, 83, 91));
        addButton(new GuiFluidGauge(() -> tile.getMultiblock().waterTank, () -> tile.getFluidTanks(null), GaugeType.SMALL, this, 115, 84));
        addButton(new GuiGasGauge(() -> tile.getMultiblock().steamTank, () -> tile.getGasTanks(null), GaugeType.SMALL, this, 151, 84));
        addButton(new GuiEnergyGauge(tile.getMultiblock().energyContainer, GaugeType.SMALL, this, 115, 46));
        addButton(new GuiFusionReactorTab(this, tile, FusionReactorTab.FUEL));
        addButton(new GuiFusionReactorTab(this, tile, FusionReactorTab.STAT));
    }

    @Override
    protected void drawForegroundText(int mouseX, int mouseY) {
        drawTitleText(GeneratorsLang.FUSION_REACTOR.translate(), 5);
        super.drawForegroundText(mouseX, mouseY);
    }
}