package mekanism.client.gui.element;


import mekanism.api.text.EnumColor;
import mekanism.client.gui.GuiUtils;
import mekanism.client.gui.IGuiWrapper;
import mekanism.common.MekanismLang;
import mekanism.common.lib.Color;
import mekanism.common.util.text.TextUtils;
import net.minecraft.util.text.ITextComponent;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class GuiColorPickerSlot extends GuiRelativeElement {

    private final Supplier<Color> supplier;
    private final Consumer<Color> consumer;

    public GuiColorPickerSlot(IGuiWrapper gui, int x, int y, Supplier<Color> supplier, Consumer<Color> consumer) {
        super(gui, x, y, 18, 18);
        this.supplier = supplier;
        this.consumer = consumer;

        addChild(new GuiElementHolder(gui, x, y, 18, 18));
    }

    @Override
    public void renderToolTip(int mouseX, int mouseY) {
        super.renderToolTip(mouseX, mouseY);
        ITextComponent hex = MekanismLang.GENERIC_HEX.translateColored(EnumColor.GRAY, TextUtils.hex(false, 3, supplier.get().rgb()));
        guiObj.displayTooltip(hex, mouseX, mouseY);
    }

    @Override
    public void renderForeground(int mouseX, int mouseY) {
        super.renderForeground(mouseX, mouseY);
        GuiUtils.fill(relativeX + 1, relativeY + 1, getButtonWidth() - 2, getButtonHeight() - 2, supplier.get().argb());
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        GuiColorWindow window = new GuiColorWindow(guiObj, guiObj.getWidth() / 2 - 160 / 2, guiObj.getHeight() / 2 - 120 / 2, consumer);
        window.setColor(supplier.get());
        guiObj.addWindow(window);
    }
}
