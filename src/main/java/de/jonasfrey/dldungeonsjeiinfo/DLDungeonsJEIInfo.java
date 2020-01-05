package de.jonasfrey.dldungeonsjeiinfo;

import mezz.jei.api.IModRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = DLDungeonsJEIInfo.MODID, name = DLDungeonsJEIInfo.NAME, version = DLDungeonsJEIInfo.VERSION)
public class DLDungeonsJEIInfo {
    
    public static final String MODID = "dldungeonsjeiinfo";
    public static final String NAME = "DLDungeons JEI Info";
    public static final String VERSION = "1.0";
    public static Logger logger;
    static String DLDungeonsRoot = "";
    
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        String path = event.getModConfigurationDirectory().toPath().getParent().toAbsolutePath().toString();
        if (!path.endsWith("/")) {
            path += "/";
        }
        path += "config/DLDungeonsJBG/";
        DLDungeonsRoot = path;
        logger.info("DLD Config Path: " + path);
    }
    
    @Mod.EventHandler
    public void init(FMLServerStartingEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
}
