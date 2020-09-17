package mekanism.client.gui;


import mekanism.client.gui.element.GuiGraph;
import mekanism.client.gui.element.tab.GuiBoilerTab;
import mekanism.client.gui.element.tab.GuiBoilerTab.BoilerTab;
import mekanism.client.gui.element.tab.GuiHeatTab;
import mekanism.common.MekanismLang;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.boiler.BoilerMultiblockData;
import mekanism.common.inventory.container.tile.EmptyTileContainer;
import mekanism.common.tile.multiblock.TileEntityBoilerCasing;
import mekanism.common.util.HeatUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.UnitDisplayUtils.TemperatureUnit;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

import java.util.Collections;

public class GuiBoilerStats extends GuiMekanismTile<TileEntityBoilerCasing, EmptyTileContainer<TileEntityBoilerCasing>> {

    private GuiGraph boilGraph;
    private GuiGraph maxGraph;

    public GuiBoilerStats(EmptyTileContainer<TileEntityBoilerCasing> container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);
    }

    @Override
    public void init() {
        super.init();
        addButton(new GuiBoilerTab(this, tile, BoilerTab.MAIN));
        addButton(new GuiHeatTab(() -> {
            ITextComponent environment = MekanismUtils.getTemperatureDisplay(tile.getMultiblock().lastEnvironmentLoss, TemperatureUnit.KELVIN, false);
            return Collections.singletonList(MekanismLang.DISSIPATED_RATE.translate(environment));
        }, this));
        addButton(boilGraph = new GuiGraph(this, 8, 83, 160, 36, MekanismLang.BOIL_RATE::translate));
        addButton(maxGraph = new GuiGraph(this, 8, 122, 160, 36, MekanismLang.MAX_BOIL_RATE::translate));
        maxGraph.enableFixedScale((long) ((MekanismConfig.general.superheatingHeatTransfer.get() * tile.getMultiblock().superheatingElements) / HeatUtils.getWaterThermalEnthalpy()));
    }

    @Override
    protected void drawForegroundText(int mouseX, int mouseY) {
        drawCenteredText(MekanismLang.BOILER_STATS.translate(), 0, getXSize(), 6, titleTextColor());
        BoilerMultiblockData multiblock = tile.getMultiblock();
        drawString(MekanismLang.BOILER_MAX_WATER.translate(formatInt(multiblock.waterTank.getCapacity())), 8, 26, titleTextColor());
        drawString(MekanismLang.BOILER_MAX_STEAM.translate(formatInt(multiblock.steamTank.getCapacity())), 8, 35, titleTextColor());
        drawString(MekanismLang.BOILER_HEAT_TRANSFER.translate(), 8, 49, subheadingTextColor());
        drawString(MekanismLang.BOILER_HEATERS.translate(multiblock.superheatingElements), 14, 58, titleTextColor());
        double boilCapacity = MekanismConfig.general.superheatingHeatTransfer.get() * multiblock.superheatingElements / HeatUtils.getWaterThermalEnthalpy();
        boilCapacity *= HeatUtils.getSteamEnergyEfficiency();
        drawString(MekanismLang.BOILER_CAPACITY.translate(formatInt((long) boilCapacity)), 8, 72, titleTextColor());
        super.drawForegroundText(mouseX, mouseY);
    }

    @Override
    public void tick() {
        super.tick();
        BoilerMultiblockData multiblock = tile.getMultiblock();
        boilGraph.addData(multiblock.lastBoilRate);
        maxGraph.addData(multiblock.lastMaxBoil);
    }
}