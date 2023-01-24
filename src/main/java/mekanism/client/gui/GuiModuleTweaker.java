package mekanism.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.ObjIntConsumer;
import java.util.function.Supplier;
import mekanism.api.gear.IModule;
import mekanism.client.gui.element.GuiElementHolder;
import mekanism.client.gui.element.button.TranslationButton;
import mekanism.client.gui.element.custom.module.GuiModuleScreen;
import mekanism.client.gui.element.scroll.GuiModuleScrollList;
import mekanism.client.gui.element.slot.GuiSlot;
import mekanism.client.gui.element.slot.SlotType;
import mekanism.client.gui.element.window.GuiMekaSuitHelmetOptions;
import mekanism.common.Mekanism;
import mekanism.common.MekanismLang;
import mekanism.common.content.gear.Module;
import mekanism.common.content.gear.ModuleConfigItem;
import mekanism.common.inventory.container.ModuleTweakerContainer;
import mekanism.common.inventory.container.slot.SlotOverlay;
import mekanism.common.network.to_server.PacketUpdateModuleSettings;
import mekanism.common.registries.MekanismItems;
import mekanism.common.util.EnumUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

public class GuiModuleTweaker extends GuiMekanism<ModuleTweakerContainer> {

    private final ArmorPreview armorPreview = new ArmorPreview();
    private final ObjIntConsumer<ModuleConfigItem<?>> saveCallback;

    private GuiModuleScrollList scrollList;
    private GuiModuleScreen moduleScreen;
    private TranslationButton optionsButton;

    private int selected = -1;

    public GuiModuleTweaker(ModuleTweakerContainer container, Inventory inv, Component title) {
        super(container, inv, title);
        saveCallback = (configItem, dataIndex) -> {
            if (moduleScreen != null) {
                IModule<?> module = moduleScreen.getCurrentModule();
                if (module != null && selected != -1) {//Shouldn't be null but validate just in case
                    int slotIndex = menu.slots.get(selected).getSlotIndex();
                    Mekanism.packetHandler().sendToServer(PacketUpdateModuleSettings.create(slotIndex, module.getData(), dataIndex, configItem.getData()));
                }
            }
        };
        imageWidth = 248;
        imageHeight += 20;
    }

    @Override
    protected void addGuiElements() {
        super.addGuiElements();
        moduleScreen = addRenderableWidget(new GuiModuleScreen(this, 138, 20, saveCallback, armorPreview));
        scrollList = addRenderableWidget(new GuiModuleScrollList(this, 30, 20, 108, 116, () -> getStack(selected), this::onModuleSelected));
        addRenderableWidget(new GuiElementHolder(this, 30, 136, 108, 18));
        optionsButton = addRenderableWidget(new TranslationButton(this, 31, 137, 106, 16, MekanismLang.BUTTON_OPTIONS, this::openOptions));
        optionsButton.active = false;
        int size = menu.slots.size();
        for (int i = 0; i < size; i++) {
            Slot slot = menu.slots.get(i);
            final int index = i;
            // initialize selected item
            if (selected == -1 && isValidItem(index)) {
                select(index);
            }
            addRenderableWidget(new GuiSlot(SlotType.NORMAL, this, slot.x - 1, slot.y - 1)
                  .click((e, x, y) -> select(index))
                  .overlayColor(isValidItem(index) ? null : () -> 0xCC333333)
                  .with(() -> index == selected ? SlotOverlay.SELECT : null));
        }
    }

    private void onModuleSelected(Module<?> module) {
        moduleScreen.setModule(module);
    }

    private void openOptions() {
        addWindow(new GuiMekaSuitHelmetOptions(this, getWidth() / 2 - 140 / 2, getHeight() / 2 - 90 / 2));
    }

    @Override
    public boolean keyPressed(int key, int i, int j) {
        if (super.keyPressed(key, i, j)) {
            return true;
        }
        if (selected != -1 && (isPreviousButton(key) || isNextButton(key))) {
            int curIndex = -1;
            IntList selectable = new IntArrayList();
            for (int index = 0, slots = menu.slots.size(); index < slots; index++) {
                if (isValidItem(index)) {
                    selectable.add(index);
                    if (index == selected) {
                        curIndex = selectable.size() - 1;
                    }
                }
            }
            int targetIndex;
            if (isPreviousButton(key)) {
                targetIndex = curIndex == 0 ? selectable.size() - 1 : curIndex - 1;
            } else {//isNextButton
                targetIndex = curIndex + 1;
            }
            select(selectable.getInt(targetIndex % selectable.size()));
            return true;
        }
        return false;
    }

    private boolean isPreviousButton(int key) {
        return key == GLFW.GLFW_KEY_UP || key == GLFW.GLFW_KEY_LEFT;
    }

    private boolean isNextButton(int key) {
        return key == GLFW.GLFW_KEY_DOWN || key == GLFW.GLFW_KEY_RIGHT;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        // make sure we get the release event
        moduleScreen.onRelease(mouseX, mouseY);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    protected void drawForegroundText(@NotNull PoseStack matrix, int mouseX, int mouseY) {
        renderTitleText(matrix);
        super.drawForegroundText(matrix, mouseX, mouseY);
    }

    private void select(int index) {
        if (isValidItem(index)) {
            selected = index;
            ItemStack stack = getStack(index);
            armorPreview.tryUpdateFull(stack);
            scrollList.updateItemAndList(stack);
            scrollList.clearSelection();
            optionsButton.active = stack.getItem() == MekanismItems.MEKASUIT_HELMET.get();
        }
    }

    private boolean isValidItem(int index) {
        return ModuleTweakerContainer.isTweakableItem(getStack(index));
    }

    private ItemStack getStack(int index) {
        if (index == -1) {
            return ItemStack.EMPTY;
        }
        return menu.slots.get(index).getItem();
    }

    public static class ArmorPreview implements Supplier<LivingEntity> {

        private final Map<EquipmentSlot, Supplier<ItemStack>> lazyItems = new EnumMap<>(EquipmentSlot.class);
        private ArmorStand preview;

        protected ArmorPreview() {
            for (EquipmentSlot armorSlot : EnumUtils.ARMOR_SLOTS) {
                lazyItems.put(armorSlot, () -> {
                    ItemStack stack = Minecraft.getInstance().player.getItemBySlot(armorSlot);
                    if (stack.isEmpty()) {
                        //Fall back to MekaSuit for rendering purposes of if not wearing a full set of stuff
                        return (switch (armorSlot) {
                            case FEET -> MekanismItems.MEKASUIT_BOOTS;
                            case LEGS -> MekanismItems.MEKASUIT_PANTS;
                            case CHEST -> MekanismItems.MEKASUIT_BODYARMOR;
                            case HEAD -> MekanismItems.MEKASUIT_HELMET;
                            default -> throw new IllegalStateException("Unknown armor slot: " + armorSlot.getName());
                        }).getItemStack();
                    }
                    return stack;
                });
            }
        }

        public void tryUpdateFull(ItemStack stack) {
            if (!stack.isEmpty() && stack.getItem() instanceof ArmorItem armorItem) {
                //If the selected thing is an armor item update the stack for the slot
                // this is of use in case the item may be an armor piece but is in the hotbar
                EquipmentSlot slot = armorItem.getSlot();
                lazyItems.put(slot, () -> stack);
                updatePreview(slot, stack);
            }
        }

        public void updatePreview(EquipmentSlot slot, ItemStack stack) {
            if (preview != null) {
                preview.setItemSlot(slot, stack);
            }
        }

        public void resetToDefault(EquipmentSlot slot) {
            if (preview != null && lazyItems.containsKey(slot)) {
                updatePreview(slot, lazyItems.get(slot).get());
            }
        }

        @Override
        public LivingEntity get() {
            if (preview == null) {
                preview = new ArmorStand(EntityType.ARMOR_STAND, Minecraft.getInstance().level);
                preview.setNoBasePlate(true);
                //Copy the player's current armor when we first initialize this
                lazyItems.forEach((slot, item) -> preview.setItemSlot(slot, item.get()));
            }
            return preview;
        }
    }
}