/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.guntram.bukkit.towngenerator;

import java.util.Random;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.Material;
import org.bukkit.World;

/**
 *
 * @author gbl
 */
public class TownGenerator extends ChunkGenerator {
    
    static final int PLOTSIZE=96;
    static final int STREETWIDTH=8;

    @Override
    public byte[][] generateBlockSections(World world, Random random, int chunkX, int chunkZ, BiomeGrid biomeGrid)
    {
        byte[][] result = new byte[world.getMaxHeight() / 16][]; //world height / chunk part height (=16, look above)

        int x, y, z;
        for(x = 0; x < 16; x++) {
            for(z = 0; z < 16; z++) {
                setBlock(result, x, 0, z, (byte)Material.BEDROCK.getId());
                for (y=1; y<=20; y++) {
                    setBlock(result, x, y, z, (byte)Material.STONE.getId());
                }
                for (y=21; y<63; y++) {
                    setBlock(result, x, y, z, (byte)Material.DIRT.getId());
                }
                int realX=(chunkX<<4)+x;
                int realZ=(chunkZ<<4)+z;
                int plotX=((realX%PLOTSIZE)+PLOTSIZE)%PLOTSIZE;
                int plotZ=((realZ%PLOTSIZE)+PLOTSIZE)%PLOTSIZE;

                int streetX=plotX-(PLOTSIZE-STREETWIDTH)/2;
                int streetZ=plotZ-(PLOTSIZE-STREETWIDTH)/2;
                
                int block=Material.GRASS.getId();
                if (plotX >= (PLOTSIZE-STREETWIDTH)/2
                &&  plotX <  (PLOTSIZE+STREETWIDTH)/2
                ||  plotZ >= (PLOTSIZE-STREETWIDTH)/2
                &&  plotZ <  (PLOTSIZE+STREETWIDTH)/2)
                    block=Material.CLAY.getId();
                setBlock(result, x, 63, z, (byte)(block&0xff));
            }
        }        
        
        
        return result;
    }
    
    void setBlock(byte[][] result, int x, int y, int z, byte blkid) {
        // is this chunk part already initialized?
        if (result[y >> 4] == null) {
            // Initialize the chunk part
            result[y >> 4] = new byte[4096];
        }
        // set the block (look above, how this is done)
        result[y >> 4][((y & 0xF) << 8) | (z << 4) | x] = blkid;
    }    
}
