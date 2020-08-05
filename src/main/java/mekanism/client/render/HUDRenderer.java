package mekanism.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import mekanism.api.text.ILangEntry;
import mekanism.common.MekanismLang;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.gear.HUDElement;
import mekanism.common.content.gear.HUDElement.HUDColor;
import mekanism.common.item.gear.ItemMekaSuitArmor;
import mekanism.common.util.EnumUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

public class HUDRenderer {

    private static final ResourceLocation HEAD_ICON = MekanismUtils.getResource(ResourceType.GUI_HUD, "hud_mekasuit_helmet.png");
    private static final ResourceLocation CHEST_ICON = MekanismUtils.getResource(ResourceType.GUI_HUD, "hud_mekasuit_chest.png");
    private static final ResourceLocation LEGS_ICON = MekanismUtils.getResource(ResourceType.GUI_HUD, "hud_mekasuit_leggings.png");
    private static final ResourceLocation BOOTS_ICON = MekanismUtils.getResource(ResourceType.GUI_HUD, "hud_mekasuit_boots.png");

    private static final ResourceLocation COMPASS = MekanismUtils.getResource(ResourceType.GUI, "compass.png");

    private long lastTick = -1;

    private float prevRotationYaw;
    private float prevRotationPitch;

    private final Minecraft minecraft = Minecraft.getInstance();

    public void renderHUD(float partialTick) {
        update();
        int color = HUDColor.REGULAR.getColor();
        if (MekanismConfig.client.hudOpacity.get() < 0.05F) {
            return;
        }
        RenderSystem.pushMatrix();
        float yawJitter = -absSqrt(minecraft.player.rotationYawHead - prevRotationYaw);
        float pitchJitter = -absSqrt(minecraft.player.rotationPitch - prevRotationPitch);
        RenderSystem.translatef(yawJitter, pitchJitter, 0);
        if (MekanismConfig.client.hudCompassEnabled.get()) {
            renderCompass(partialTick, color);
        }

        renderMekaSuitEnergyIcons(partialTick, color);
        renderMekaSuitModuleIcons(partialTick, color);

        RenderSystem.popMatrix();
    }

    private void update() {
        // if we're just now rendering the HUD after a pause, reset the pitch/yaw trackers
        if (lastTick == -1 || minecraft.world.getGameTime() - lastTick > 1) {
            prevRotationYaw = minecraft.player.rotationYaw;
            prevRotationPitch = minecraft.player.rotationPitch;
        }
        lastTick = minecraft.world.getGameTime();
        float yawDiff = (minecraft.player.rotationYawHead - prevRotationYaw);
        float pitchDiff = (minecraft.player.rotationPitch - prevRotationPitch);
        prevRotationYaw += yawDiff / MekanismConfig.client.hudJitter.get();
        prevRotationPitch += pitchDiff / MekanismConfig.client.hudJitter.get();
    }

    private static float absSqrt(float val) {
        float ret = (float) Math.sqrt(Math.abs(val));
        return val < 0 ? -ret : ret;
    }

    private void renderMekaSuitEnergyIcons(float partialTick, int color) {
        RenderSystem.pushMatrix();
        RenderSystem.translatef(10, 10, 0);
        int posX = 0;
        if (getStack(EquipmentSlotType.HEAD).getItem() instanceof ItemMekaSuitArmor) {
            renderHUDElement(posX, 0, HUDElement.energyPercent(HEAD_ICON, getStack(EquipmentSlotType.HEAD)), color, false);
            posX += 48;
        }
        if (getStack(EquipmentSlotType.CHEST).getItem() instanceof ItemMekaSuitArmor) {
            renderHUDElement(posX, 0, HUDElement.energyPercent(CHEST_ICON, getStack(EquipmentSlotType.CHEST)), color, false);
            posX += 48;
        }
        if (getStack(EquipmentSlotType.LEGS).getItem() instanceof ItemMekaSuitArmor) {
            renderHUDElement(posX, 0, HUDElement.energyPercent(LEGS_ICON, getStack(EquipmentSlotType.LEGS)), color, false);
            posX += 48;
        }
        if (getStack(EquipmentSlotType.FEET).getItem() instanceof ItemMekaSuitArmor) {
            renderHUDElement(posX, 0, HUDElement.energyPercent(BOOTS_ICON, getStack(EquipmentSlotType.FEET)), color, false);
        }
        RenderSystem.popMatrix();
    }

    private void renderMekaSuitModuleIcons(float partialTick, int color) {
        // create list of all elements to render
        List<HUDElement> elements = new ArrayList<>();
        for (EquipmentSlotType type : EnumUtils.ARMOR_SLOTS) {
            ItemStack stack = getStack(type);
            if (stack.getItem() instanceof ItemMekaSuitArmor) {
                elements.addAll(((ItemMekaSuitArmor) stack.getItem()).getHUDElements(stack));
            }
        }

        int startX = minecraft.getMainWindow().getScaledWidth() - 10;
        int curY = minecraft.getMainWindow().getScaledHeight() - 10;

        RenderSystem.pushMatrix();
        for (HUDElement element : elements) {
            int elementWidth = 24 + minecraft.fontRenderer.getStringWidth(element.getText().getFormattedText());
            curY -= 18;
            renderHUDElement(startX - elementWidth, curY, element, color, true);
        }
        RenderSystem.popMatrix();
    }

    private void renderHUDElement(int x, int y, HUDElement element, int color, boolean iconRight) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        MekanismRenderer.color(color);
        minecraft.getTextureManager().bindTexture(element.getIcon());
        if (!iconRight) {
            AbstractGui.blit(x, y, 0, 0, 16, 16, 16, 16);
            MekanismRenderer.resetColor();
            minecraft.fontRenderer.drawString(element.getText().getFormattedText(), x + 18, y + 5, element.getColor());
        } else {
            AbstractGui.blit(x + minecraft.fontRenderer.getStringWidth(element.getText().getFormattedText()) + 2, y, 0, 0, 16, 16, 16, 16);
            MekanismRenderer.resetColor();
            minecraft.fontRenderer.drawString(element.getText().getFormattedText(), x, y + 5, element.getColor());
        }
    }

    private void renderCompass(float partialTick, int color) {
        RenderSystem.pushMatrix();
        int posX = 25;
        int posY = minecraft.getMainWindow().getScaledHeight() - 100;
        RenderSystem.translatef(posX + 50, posY + 50, 0);
        RenderSystem.pushMatrix();
        float angle = 180 - MathHelper.lerp(partialTick, minecraft.player.prevRotationYawHead, minecraft.player.rotationYawHead);
        RenderSystem.pushMatrix();
        RenderSystem.scalef(0.7F, 0.7F, 0.7F);
        ITextComponent coords = MekanismLang.GENERIC_BLOCK_POS.translate((int) minecraft.player.getPosX(), (int) minecraft.player.getPosY(), (int) minecraft.player.getPosZ());
        minecraft.fontRenderer.drawString(coords.getFormattedText(), -minecraft.fontRenderer.getStringWidth(coords.getFormattedText()) / 2F, -4, color);
        RenderSystem.popMatrix();
        Quaternion v1 = Vector3f.XP.rotationDegrees(-60);
        Quaternion v2 = Vector3f.ZP.rotationDegrees(angle);
        RenderSystem.rotatef(v1.getX(), v1.getY(), v1.getZ(), v1.getW());
        RenderSystem.rotatef(v2.getX(), v2.getY(), v2.getZ(), v2.getW());
        minecraft.getTextureManager().bindTexture(COMPASS);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        MekanismRenderer.color(color);
        AbstractGui.blit(-50, -50, 100, 100, 0, 0, 256, 256, 256, 256);
        rotateStr(MekanismLang.NORTH_SHORT, angle, 0, color);
        rotateStr(MekanismLang.EAST_SHORT, angle, 90, color);
        rotateStr(MekanismLang.SOUTH_SHORT, angle, 180, color);
        rotateStr(MekanismLang.WEST_SHORT, angle, 270, color);
        MekanismRenderer.resetColor();
        RenderSystem.popMatrix();
        RenderSystem.popMatrix();
    }

    private void rotateStr(ILangEntry langEntry, float rotation, float shift, int color) {
        RenderSystem.pushMatrix();
        Quaternion v1 = Vector3f.ZP.rotationDegrees(shift);
        Quaternion v2 = Vector3f.ZP.rotationDegrees(-rotation - shift);
        RenderSystem.rotatef(v1.getX(), v1.getY(), v1.getZ(), v1.getW());
        RenderSystem.translatef(0, -50, 0);
        RenderSystem.rotatef(v2.getX(), v2.getY(), v2.getZ(), v2.getW());
        minecraft.fontRenderer.drawString(langEntry.translate().getFormattedText(), -2.5F, -4, color);
        RenderSystem.popMatrix();
    }

    private ItemStack getStack(EquipmentSlotType type) {
        return minecraft.player.getItemStackFromSlot(type);
    }
}
