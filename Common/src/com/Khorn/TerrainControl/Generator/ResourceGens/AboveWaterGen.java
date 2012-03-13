package com.Khorn.TerrainControl.Generator.ResourceGens;

import com.Khorn.TerrainControl.Configuration.Resource;
import com.Khorn.TerrainControl.DefaultMaterial;
import com.Khorn.TerrainControl.LocalWorld;

import java.util.Random;

public class AboveWaterGen extends ResourceGenBase
{
    @Override
    protected void SpawnResource(LocalWorld world, Random rand, Resource res, int x, int z)
    {
        int y = world.getHighestBlockYAt(x, z);

        for (int i = 0; i < 10; i++)
        {
            int j = x + rand.nextInt(8) - rand.nextInt(8);
            //int k = y + rand.nextInt(4) - rand.nextInt(4);
            int m = z + rand.nextInt(8) - rand.nextInt(8);
            if ((!world.isEmpty(j, y, m)) || (!world.getMaterial(j, y - 1, m).isLiquid()))
                continue;
            world.setBlock(j, y, m, res.BlockId, 0, false, false, false);
        }
    }
}
