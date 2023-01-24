package mekanism.client.gui.element.filter;

import mekanism.client.gui.IGuiWrapper;
import mekanism.common.tile.base.TileEntityMekanism;

import javax.annotation.Nullable;

public interface GuiFilterHelper<TILE extends TileEntityMekanism> {

    @Nullable
    default GuiFilterSelect getFilterSelect(IGuiWrapper gui, TILE tile) {
        return null;
    }

    int getRelativeX();

    int getRelativeY();
}