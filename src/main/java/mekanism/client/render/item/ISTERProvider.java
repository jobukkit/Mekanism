package mekanism.client.render.item;

import mekanism.client.render.item.block.*;
import mekanism.client.render.item.gear.*;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;

import java.util.concurrent.Callable;

//This class is used to prevent class loading issues on the server without having to use OnlyIn hacks
public class ISTERProvider {

    public static Callable<ItemStackTileEntityRenderer> energyCube() {
        return RenderEnergyCubeItem::new;
    }

    public static Callable<ItemStackTileEntityRenderer> dissolution() {
        return RenderChemicalDissolutionChamberItem::new;
    }

    public static Callable<ItemStackTileEntityRenderer> fluidTank() {
        return RenderFluidTankItem::new;
    }

    public static Callable<ItemStackTileEntityRenderer> industrialAlarm() {
        return RenderIndustrialAlarmItem::new;
    }

    public static Callable<ItemStackTileEntityRenderer> entangloporter() {
        return RenderQuantumEntangloporterItem::new;
    }

    public static Callable<ItemStackTileEntityRenderer> seismicVibrator() {
        return RenderSeismicVibratorItem::new;
    }

    public static Callable<ItemStackTileEntityRenderer> activator() {
        return RenderSolarNeutronActivatorItem::new;
    }

    public static Callable<ItemStackTileEntityRenderer> armoredJetpack() {
        return RenderArmoredJetpack::new;
    }

    public static Callable<ItemStackTileEntityRenderer> disassembler() {
        return RenderAtomicDisassembler::new;
    }

    public static Callable<ItemStackTileEntityRenderer> flamethrower() {
        return RenderFlameThrower::new;
    }

    public static Callable<ItemStackTileEntityRenderer> freeRunners() {
        return RenderFreeRunners::new;
    }

    public static Callable<ItemStackTileEntityRenderer> scubaMask() {
        return RenderScubaMask::new;
    }

    public static Callable<ItemStackTileEntityRenderer> jetpack() {
        return RenderJetpack::new;
    }

    public static Callable<ItemStackTileEntityRenderer> scubaTank() {
        return RenderScubaTank::new;
    }
}