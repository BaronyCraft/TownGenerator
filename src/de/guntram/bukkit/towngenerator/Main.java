/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.guntram.bukkit.towngenerator;

import org.bukkit.Bukkit;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author gbl
 */
public class Main extends JavaPlugin {
    
    @Override
    public void onEnable() {
        saveDefaultConfig();
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id)
    {
        return new TownGenerator(this, worldName);
    }
}
