package ru.lionzxy.simlyhammer.config;

import akka.io.Tcp;
import cpw.mods.fml.common.Loader;
import net.minecraftforge.common.config.Configuration;
import ru.lionzxy.simlyhammer.hammers.BoundHammer;
import ru.lionzxy.simlyhammer.hammers.IC2EnergyHammer;
import ru.lionzxy.simlyhammer.hammers.RFHammer;

import java.io.File;

/**
 * Created by nikit on 02.09.2015.
 */
public class Config {

    public static Configuration config;
    public static boolean pick, MTorch, MDiamond, MAxe, MShovel, repair, MTrash, MVacuum, MCheckVacuum, MCheckTrash, MSmelt, debugI, MDye;

    public static void createConfig() {
        JsonConfig.load();
        File configFile = new File(Loader.instance().getConfigDir() + "/SimplyHammers", "SimplyHammer.cfg");
        config = new Configuration(configFile, "0.8.5");
        config.getCategory("general");
        pick = config.get("general", "Prospector's Pick", true).getBoolean();
        MTorch = Config.config.get("modif", "TorchModif", true).getBoolean();
        MShovel = Config.config.get("modif", "ShovelModif", true).getBoolean();
        MAxe = Config.config.get("modif", "AxeModif", true).getBoolean();
        MDiamond = Config.config.get("modif", "DiamondModif", true).getBoolean();
        MTrash = Config.config.get("modif", "TrashModif", true).getBoolean();
        MCheckTrash = Config.config.get("modif", "TrashCheckModif", true, "Check every pickup item all inventory slot").getBoolean();
        MVacuum = Config.config.get("modif", "VacuumModif", true).getBoolean();
        MCheckVacuum = Config.config.get("modif", "VacuumCheckModif", true, "Check every harvest block all inventory slot").getBoolean();
        repair = Config.config.get("modif", "RepairTool", true).getBoolean();
        MSmelt = Config.config.get("modif", "SmeltModif", true).getBoolean();
        MDye = Config.config.get("modif", "SmeltModif", true).getBoolean();
        debugI = config.get("modif", "DyeUpgrade", true).getBoolean();
        RFHammer.cost = JsonConfig.get("rfhammer","cost",200).getInt();
        IC2EnergyHammer.cost = JsonConfig.get("ic2hammer","cost",50).getInt();
        BoundHammer.cost = JsonConfig.get("boundHammer","cost",5).getInt();
        config.save();
        config.load();
    }

    public static void saveConfig(){
        config.save();
        JsonConfig.save();
    }

}
