/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.guntram.bukkit.towngenerator;

import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author gbl
 */
public class TownGenerator extends ChunkGenerator {
    
    final int plotsize;
    final int streetwidth;
    final int islandsSize;

    OpenSimplexNoise noiseGenerator;

    TownGenerator(JavaPlugin plugin, String worldName) {
        
        System.out.println("creating towngenerator, world="+worldName);
        World world=Bukkit.getWorld(worldName);
        System.out.println("new TownGenerator for "+worldName+", getWorld is "+world);
        if (world!=null) {
            noiseGenerator=new OpenSimplexNoise(world.getSeed());
        }
        FileConfiguration config = plugin.getConfig();
        
        plotsize=config.getInt("worlds."+worldName+".plotsize",
                    config.getInt("worlds.default.plotsize", 60));
        streetwidth=config.getInt("worlds."+worldName+".streetwidth",
                    config.getInt("worlds.default.streetwidth", 6));
        islandsSize=config.getInt("worlds."+worldName+".islandssize",
                    config.getInt("worlds.default.islandssize", 0));
    }

    // for 1.13
    @Override
    public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biome) {
        
        if (noiseGenerator == null) {
            noiseGenerator=new OpenSimplexNoise(world.getSeed());
        }
        
        ChunkData result=this.createChunkData(world);
        int x, z;
        result.setRegion(0, 0, 0, 16, 0, 16, Material.BEDROCK);
        result.setRegion(0, 1, 0, 16, 20, 16, Material.STONE);
        result.setRegion(0, 20, 0, 16, 63, 16, Material.DIRT);
        for(x = 0; x < 16; x++) {
            for(z = 0; z < 16; z++) {
                biome.setBiome(x, z, Biome.FLOWER_FOREST);
                int realX=(chunkX<<4)+x;
                int realZ=(chunkZ<<4)+z;
                int plotX=((realX%plotsize)+plotsize)%plotsize;
                int plotZ=((realZ%plotsize)+plotsize)%plotsize;
                
                
                if (islandsSize != 0
                &&  realX >= -plotsize*3/2+streetwidth/2
                &&  realZ >= -plotsize*3/2+streetwidth/2
                &&  realX <   plotsize*3/2-streetwidth/2
                &&  realZ <   plotsize*3/2-streetwidth/2) {

                    biome.setBiome(x, z, Biome.WARM_OCEAN);
                    int maxdy;
                    int distleft = realX+1-(-plotsize*3/2+streetwidth/2); maxdy=distleft;
                    int disttop  = realZ+1-(-plotsize*3/2+streetwidth/2); if (disttop < maxdy) maxdy=disttop;
                    int distright= plotsize*3/2-streetwidth/2-realX;    if (distright < maxdy) maxdy=distright;
                    int distbot  = plotsize*3/2-streetwidth/2-realZ;    if (distbot   < maxdy) maxdy=distbot;
                    
                    double noise=   noiseGenerator.eval((double)realX/islandsSize  , (double)realZ/islandsSize  ) * 0.6+
                                    noiseGenerator.eval((double)realX/islandsSize/2, (double)realZ/islandsSize/2) * 0.3+ 
                                    noiseGenerator.eval((double)realX/islandsSize/5, (double)realZ/islandsSize/5) * 0.1+
                                    0.0;
                            
                    int sandy=53+(int)(20*((noise+1)/2));
                    if (sandy < 64-maxdy) sandy=64-maxdy;
                    if (sandy > 64+maxdy) sandy=64+maxdy;
                    int y=50;
                    while (y<sandy) {
                        result.setBlock(x, y++, z, Material.SAND);
                    }
                    while (y<64) {
                        result.setBlock(x, y++, z, Material.WATER);
                    }
                } else {
                    int streetX=plotX-(plotsize-streetwidth)/2;
                    int streetZ=plotZ-(plotsize-streetwidth)/2;

                    Material block=Material.GRASS;
                    if (plotX >= (plotsize-streetwidth)/2
                    &&  plotX <  (plotsize+streetwidth)/2
                    ||  plotZ >= (plotsize-streetwidth)/2
                    &&  plotZ <  (plotsize+streetwidth)/2)
                        block=Material.CLAY;
                    result.setBlock(x, 63, z, block);
                }
            }
        }
        return result;
    }
    
    public Location getFixedSpawLocation(World world, Random random) {
        return new Location(world, 0, 64, 0);
    }
}
