package me.redth.circularhotbar;

import me.redth.circularhotbar.config.ModConfig;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = CircularHotbar.MODID, name = CircularHotbar.NAME, version = CircularHotbar.VERSION)
public class CircularHotbar {
    public static final String MODID = "@ID@";
    public static final String NAME = "@NAME@";
    public static final String VERSION = "@VER@";

    @Mod.Instance(MODID)
    public static CircularHotbar INSTANCE;

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        new ModConfig();
    }
}
