package com.khorn.terraincontrol;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import com.khorn.terraincontrol.configuration.Resource;
import com.khorn.terraincontrol.customobjects.CustomObject;
import com.khorn.terraincontrol.customobjects.CustomObjectLoader;
import com.khorn.terraincontrol.customobjects.CustomObjectManager;
import com.khorn.terraincontrol.generator.resourcegens.ResourcesManager;

public class TerrainControl
{
    /**
     * The world height that the engine supports. Not the actual height the
     * world is capped at. 256 in Minecraft.
     */
    public static int worldHeight = 256;

    /**
     * The world depth that the engine supports. Not the actual depth the world
     * is capped at. 0 in Minecraft.
     */
    public static int worldDepth = 0;

    private static TerrainControlEngine engine;
    private static ResourcesManager resourcesManager;
    private static CustomObjectManager customObjectManager;

    // Used before TerrainControl is initialized
    private static Map<String, CustomObjectLoader> customObjectLoaders = new HashMap<String, CustomObjectLoader>();
    private static Map<String, CustomObject> specialCustomObjects;
    private static Map<String, Class<? extends Resource>> resourceTypes;

    private TerrainControl()
    {
        // Forbidden to instantiate.
    }

    /**
     * Starts the engine, making all API methods available.
     * 
     * @param engine
     *            The implementation of the engine.
     */
    public static void startEngine(TerrainControlEngine engine)
    {
        if (TerrainControl.engine != null)
        {
            throw new UnsupportedOperationException("Engine is already set!");
        }
        TerrainControl.engine = engine;

        if (customObjectLoaders == null)
        {
            customObjectLoaders = new HashMap<String, CustomObjectLoader>();
        }
        if (specialCustomObjects == null)
        {
            specialCustomObjects = new HashMap<String, CustomObject>();
        }
        customObjectManager = new CustomObjectManager(customObjectLoaders, specialCustomObjects);

        if (resourceTypes == null)
        {
            resourceTypes = new HashMap<String, Class<? extends Resource>>();
        }
        
        resourcesManager = new ResourcesManager(resourceTypes);
        //resourcesManager.start(resourceTypes);
    }

    /**
     * Null out static references to free up memory. Should be called on
     * shutdown.
     */
    public static void stopEngine()
    {
        engine = null;
        customObjectManager = null;
        resourcesManager = null;

        customObjectLoaders.clear();
        specialCustomObjects.clear();
        resourceTypes.clear();
    }

    /**
     * Returns the engine, containing the API methods.
     * 
     * @return The engine
     */
    public static TerrainControlEngine getEngine()
    {
        return engine;
    }

    /**
     * Returns the world object with the given name.
     * 
     * @param name
     *            The name of the world.
     * @return The world object.
     */
    public static LocalWorld getWorld(String name)
    {
        return engine.getWorld(name);
    }

    /**
     * Convienence method to quickly get the biome name at the given
     * coordinates. Will return null if the world isn't loaded by Terrain
     * Control.
     * 
     * @param world
     *            The world name.
     * @param x
     *            The block x in the world.
     * @param z
     *            The block z in the world.
     * @return The biome name, or null if the world isn't managed by Terrain
     *         Control.
     */
    public static String getBiomeName(String worldName, int x, int z)
    {
        LocalWorld world = getWorld(worldName);
        if (world == null)
        {
            // World isn't loaded by Terrain Control
            return null;
        }
        return world.getBiome(x, z).getName();
    }

    /**
     * Logs the message(s) with normal importance. Message will be prefixed with
     * TerrainControl, so don't do that yourself.
     * 
     * @param messages
     *            The messages to log.
     */
    public static void log(String... messages)
    {
        engine.log(Level.INFO, messages);
    }

    /**
     * Logs the message(s) with the given importance. Message will be prefixed
     * with TerrainControl, so don't do that yourself.
     * 
     * @param messages
     *            The messages to log.
     */
    public static void log(Level level, String... messages)
    {
        engine.log(level, messages);
    }

    /**
     * Returns the CustomObject manager, with hooks to spawn CustomObjects.
     * 
     * @return The CustomObject manager.
     */
    public static CustomObjectManager getCustomObjectManager()
    {
        return customObjectManager;
    }

    /**
     * Returns the Resource manager.
     * 
     * @return The Resource manager.
     */
    public static ResourcesManager getResourcesManager()
    {
        return resourcesManager;
    }

    /**
     * Registers a CustomObject loader. Can be called before Terrain Control is
     * fully loaded.
     * 
     * @param extension
     *            The file extension, without a dot.
     * @param loader
     *            The loader.
     */
    public static void registerCustomObjectLoader(String extension, CustomObjectLoader loader)
    {
        if (customObjectLoaders == null)
        {
            customObjectLoaders = new HashMap<String, CustomObjectLoader>();
        }
        customObjectLoaders.put(extension.toLowerCase(), loader);
    }

    /**
     * Registers a special CustomObject, like UseWorld or UseBiome or a tree.
     * Can be called before Terrain Control is fully loaded.
     * 
     * @param extension
     * @param loader
     */
    public static void registerSpecialCustomObject(String name, CustomObject object)
    {
        if (specialCustomObjects == null)
        {
            specialCustomObjects = new HashMap<String, CustomObject>();
        }
        specialCustomObjects.put(name.toLowerCase(), object);
    }

    /**
     * Register a new Resource type. Can be called before Terrain Control is
     * fully loaded.
     * 
     * @param name
     * @param resourceType
     */
    public static void registerResourceType(String name, Class<? extends Resource> resourceType)
    {
        if (resourceTypes == null)
        {
            resourceTypes = new HashMap<String, Class<? extends Resource>>();
        }
        resourceTypes.put(name.toLowerCase(), resourceType);
    }

}
