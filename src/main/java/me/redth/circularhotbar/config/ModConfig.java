package me.redth.circularhotbar.config;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.HUD;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;
import me.redth.circularhotbar.CircularHotbar;
import me.redth.circularhotbar.hud.HotbarHud;

@SuppressWarnings("unused")
public class ModConfig extends Config {
    @HUD(name = "Hotbar", category = "Hotbar")
    public static HotbarHud hotbarHud = new HotbarHud();

    public ModConfig() {
        super(new Mod(CircularHotbar.NAME, ModType.HUD), CircularHotbar.MODID + ".json");
        initialize();
    }

}

