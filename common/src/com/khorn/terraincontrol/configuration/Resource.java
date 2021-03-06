package com.khorn.terraincontrol.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.khorn.terraincontrol.DefaultMaterial;
import com.khorn.terraincontrol.LocalWorld;
import com.khorn.terraincontrol.TerrainControl;
import com.khorn.terraincontrol.exception.InvalidResourceException;
import com.khorn.terraincontrol.generator.resourcegens.ResourceType;

public abstract class Resource
{
    protected int frequency;
    protected int rarity;
    protected WorldConfig worldConfig;

    /**
     * Sets the world. Needed for some resources, like CustomObject and Tree.
     * @param world
     */
    public void setWorldConfig(WorldConfig worldConfig)
    {
        this.worldConfig = worldConfig;
    }
    
    /**
     * Convenience method for creating a resource. Used to create the default resources.
     * @param world
     * @param clazz
     * @param args
     * @return
     */
    public static Resource create(WorldConfig config, Class<? extends Resource> clazz, Object... args)
    {
        List<String> stringArgs = new ArrayList<String>(args.length);
        for(Object arg: args)
        {
            stringArgs.add("" + arg);
        }
        
        Resource resource;
        try
        {
            resource = clazz.newInstance();
        } catch (InstantiationException e)
        {
            return null;
        } catch (IllegalAccessException e)
        {
            return null;
        }
        resource.setWorldConfig(config);
        try {
            resource.load(stringArgs);
        } catch(InvalidResourceException e)
        {
            TerrainControl.log("Invalid default resource! Please report! " + clazz.getName() + ": "+e.getMessage());
            e.printStackTrace();
        }
        
        return resource;
    }

    /**
     * Loads the settings. Returns false if one of the arguments contains an
     * error.
     * 
     * @param args
     *            List of args.
     * @return Returns false if one of the arguments contains an error,
     *         otherwise true.
     * @throws InvalidResourceException
     *             If the resoure is invalid.
     */
    public abstract void load(List<String> args) throws InvalidResourceException;

    /**
     * Spawns the resource at this position, ignoring rarity and frequency.
     * 
     * @param world
     * @param chunkX
     * @param chunkZ
     */
    public abstract void spawn(LocalWorld world, Random random, int x, int z);

    /**
     * Spawns the resource normally.
     * 
     * @param world
     * @param chunkX
     * @param chunkZ
     */
    public void process(LocalWorld world, Random random, int chunkX, int chunkZ)
    {
        for (int t = 0; t < frequency; t++)
        {
            if (random.nextInt(100) > rarity)
                continue;
            int x = chunkX * 16 + random.nextInt(16) + 8;
            int z = chunkZ * 16 + random.nextInt(16) + 8;
            spawn(world, random, x, z);
        }
    }

    /**
     * Gets the type of this resource.
     * 
     * @return The type of this resource.
     */
    public abstract ResourceType getType();

    /**
     * Gets a String representation, like Tree(10,BigTree,50,Tree,100)
     * 
     * @return A String representation, like Tree(10,BigTree,50,Tree,100)
     */
    public abstract String makeString();

    /**
     * Parses the string and returns a number between minValue and maxValue.
     * Returns Resource.INCORRECT_NUMBER if the string is not a number.
     * 
     * @param string
     * @param minValue
     * @param maxValue
     * @return
     * @throws InvalidResourceException
     *             If the number is invalid.
     */
    public int getInt(String string, int minValue, int maxValue) throws InvalidResourceException
    {
        try
        {
            int number = Integer.parseInt(string);
            if (number < minValue)
            {
                return minValue;
            }
            if (number > maxValue)
            {
                return maxValue;
            }
            return number;
        } catch (NumberFormatException e)
        {
            throw new InvalidResourceException("Incorrect number: " + string);
        }
    }

    /**
     * Returns the block id with the given name.
     * 
     * @param string
     * @return
     */
    public int getBlockId(String string) throws InvalidResourceException
    {
        if (string.indexOf('.') != -1)
        {
            // Ignore block data
            string = string.split("\\.")[0];
        }

        DefaultMaterial material = DefaultMaterial.getMaterial(string);
        if (material != null)
        {
            return material.id;
        }

        return getInt(string, 0, 256);
    }

    /**
     * Gets the block data from a material string.
     * 
     * @param string
     * @return
     * @throws InvalidResourceException
     */
    public int getBlockData(String string) throws InvalidResourceException
    {
        if (string.indexOf('.') == -1)
        {
            // No block data
            return 0;
        }

        // Get block data
        string = string.split("\\.")[1];
        return getInt(string, 0, 16);
    }

    public void assureSize(int size, List<String> args) throws InvalidResourceException
    {
        if (args.size() < size)
        {
            throw new InvalidResourceException("Too few arguments supplied");
        }
    }

    /**
     * Gets the material name back from the id and data.
     * 
     * @param id
     *            The block id
     * @param data
     *            The block data
     * @return String in the format blockname[.blockdata]
     */
    public String makeMaterial(int id, int data)
    {
        String materialString = "" + id;
        DefaultMaterial material = DefaultMaterial.getMaterial(id);
        if (material != DefaultMaterial.UNKNOWN_BLOCK)
        {
            // No name, return number as String
            materialString = material.toString();
        }

        if (data > 0)
        {
            materialString = materialString + "." + data;
        }

        return materialString;
    }

    /**
     * Gets the material name back from the id.
     * 
     * @param id
     *            The block id
     * @return String in the format blockname
     */
    public String makeMaterial(int id)
    {
        return makeMaterial(id, 0);
    }

    /**
     * Returns a String in the format ",materialName,materialName,etc"
     * 
     * @param ids
     * @return
     */
    public String makeMaterial(List<Integer> ids)
    {
        String string = "";
        for (int blockId : ids)
        {
            string += ",";
            string += makeMaterial(blockId);
        }
        return string;
    }
}
