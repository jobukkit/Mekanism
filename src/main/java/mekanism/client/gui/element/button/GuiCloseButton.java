package mekanism.client.gui.element.button;


import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.GuiWindow;
import mekanism.common.MekanismLang;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;

public class GuiCloseButton extends MekanismImageButton {

    public GuiCloseButton(IGuiWrapper gui, int x, int y, GuiWindow window) {
        super(gui, x, y, 8, MekanismUtils.getResource(ResourceType.GUI_BUTTON, "close.png"), window::close);
    }

    @Override
    public void renderToolTip(int mouseX, int mouseY) {
        displayTooltip(MekanismLang.CLOSE.translate(), mouseX, mouseY);
    }

    @Override
    public boolean resetColorBeforeRender() {
        return false;
    }
}
