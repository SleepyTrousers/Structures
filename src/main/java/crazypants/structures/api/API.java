package crazypants.structures.api;

/**
   * The Ender Structures mod adds world gen buildings defined using json config files and block data in nbt format.
   *
   * 
   * The Structure Generation Process
   * *********************************
   * 
   * For example, the mod 'Cheese+' wants to add a cheese factory to world gen, with two building variants.
   * To do this, at least five files must be created: cheesfactory.gen, cheesefactory1.stp, cheesefactory1.nbt, cheesefactory2.stp and cheesefactory2.nbt.
   * 
   * 
   * Generation Config (.gen)
   * ------------------------------
   * 
   * The generation config file (cheesefactory.gen) is used to create an 'IStutureGenerator' instance which determines how often and where new structures are created.
   * When a new chunk is being generated each 'IStutureGenerator' is given in the opportunity to build a new structure.
   * 
   * Chunk Validators
   * The suitability of the chunk is first checked by querying all the 'IChunkValidators' defined in the config file. For example:
    
    "chunkValidators" : [
                    
            { "type" : "RandomValidator", 
                      "chancePerChunk" : 0.05 },
                                  
            { "type" : "BiomeValidator", 
                      "match" : "any",                
                      "types" : [ "MESA", "FOREST", "PLAINS", "MOUNTAIN", "HILLS", "SWAMP", "SANDY", "SNOWY", "WASTELAND" ],
                      "typesExcluded" : [ "BEACH", "MUSHROOM", "OCEAN", "RIVER", "NETHER", "END"],
                      "names" : [],
                      "namesExcluded" : [] },                                    
            
            
            { "type" : "DimensionValidator",                               
                      "names" : [],
                      "namesExcluded" : ["Twilight Forest"] },
            
            
            { "type" : "SpacingValidator",
                      "minSpacing" : 500,
                      "templateFilter" : [ "cheesefactory1, cheesefactory2" ] }                                
                                                           
       ]
    
   * In this example a new structure will be only be created if:
   * - Math.rand() < 0.05 (RandomValidator)
   * - The biome is of the appropriate type (BiomeValidator)
   * - It is not the Twlight Forest dimension (DimensionValidator)
   * - There is no other Cheese Factory stuctures within 500 blocks
   * 
   * 
   * Location Samplers
   * If the chunk is valid, a candidate location for the origin of the new structure is then chosen by an ISurfaceSampler. For example:
    
    "LocationSampler" : {
              "type" : "SurfaceSampler",
              "distanceFromSurface" : 2,
              "canGenerateOnFluid" : "false"            
        }
   * 
   * This sampler will attempt to find a location 2 blocks off the ground within the chunk.
   * If a candidate location is found a building template is then selected. In this example, two possible templates have been specified so one is randomly selected. 
   
   "templates" : [
          {"uid" : "cheesefactory1"},
          {"uid" : "cheesefactory2"}    
        ],
    
   * 
   * Whether a structure is generated is then determined by the selected template.
   * 
   * 
   * Template Config (.stl)
   * ------------------------------
   * The selected structure template (either cheesefactory1.stl or cheesefactory2.stl) is then used to create a new IStructureTemplate.
   * 
   * The suitability of the location chosen by the IStructureGenerator is determined by querying all the 'ISiteValidators' specified in the 
   * config. For exmaple:
   * 
      "siteValidators" : [
        
         { "type" : "SpacingValidator", 
                    "minSpacing" : 20,
                    "templateFilter" : []},
                                            
        { "type" : "LevelGroundValidator",
                    "canSpawnOnWater" : "false",
                    "tolerance" : 2,
                    "sampleSpacing" : 4,
                    "maxSamples" : 50,
                    "Border" : { "sizeXZ" : 1 }}                    
        
        ]
   * 
   * In this instance, a build location will be rejected if:
   * - it is within 20 blocks of any other structure (SpacingValidator)
   * - the grounds height does not vary by more than two within the structures bounds and
   * there is not liquid within the structures bounds (LevelGroundValidator)
   * 
   * If the selected site is valid, any configured site works are then carried out. For example:
   * 
    "sitePreperations" : [
                  
            { "type": "ClearPreperation", 
                      "clearPlants" : "true",          
                      "Border" : {
                          "up" : 3, "down" : 0, "north" : 1, "south" : 1, "east" : 1, "west" : 1
                      }
            },
           
            { "type" : "FillPreperation",
                      "clearPlants" : "true",
                      "Border" : {
                          "sizeXZ" : 1
                      }}           
       ]
   * 
   * Before the structure is added:
   * - ClearPreperation will clear all blocks with the structures bounds plus the specified border. 
   * - The fill preperation fills any empty space underneath the structure
   * When combined this preperations create a level surface to build on.
   * 
   * Once the site is prepared, all the components of the structure are then placed into the world.
   * 
     "components" : [
          {"uid" : "cheesefactory1"}
        ]
   
   * 
   * Once a structure is built decorators can then be used modify it. For example:
   * 
     "decorators" : [
        
         { "type" : "LootTableInventory",
                    "category" : "mineshaftCorridor",                      
                    "targets" : ["inv0", "inv1"]
                    }}               
        
        ]
   * 
   * The LootTableInventory decorator will add items to all inventories tagged 'inv0' and 'inv1'. The items are created using the 'mineshaftCorridor' loot table category 
   * 
   * Runtime behaviors are ... TBD
   
    "behaviours" : [
           
           { "type": "GuardianSpawner",
             "minTimeBetweenSpawns":1000,
             "maxTimeBetweenSpawns":1200,
             "mobs" : [
               {"name":"Creeper", "count":3}
             ]}
   
   * 
   * 
   * Component Data(.nbt)
   * ------------------------------
   * Structures are build in world by using one or more components defined in nbt files (cheesefactory1.nbt and cheesefactpry2.nbt).
   * These files contain the blocks and tile entities to be placed in world and are created using the EnderStructureCreator mod.
   * In its simplest form, this is used to export all the blocks within a specified bounds to an nbt file.
   * 
   * Locations within a component may be tagged for use by things such as decorators (e.g. to place content in chests) and
   * behaviours (e.g. create an explosion when a block certain block is broken) 
   * 
   * 
   * 
   * 
   * 
   * Adding New Structures
   * *********************************
   * 
   * Config Directory
   * ------------------
   * Ender Structures will automatically load all generator (.gen), template (.stp) and building component (.nbt) files in the 'config/enderdtructures/structures' 
   * directory and the those in the structures .jar file using the resource path '/assets/enderstructures/structures/'. The simplest way to add new structures is 
   * to copy the required generator (.gen), template (.stp) and/or building component (.nbt) files into this directory. 
   * 
   * API
   * -------------------  
   * To enabled Ender Structures to locate content distributed with other mods the mod must let EnderStructures know where to look. Ender Structures can search 
   * either the class path (inside a mods jar file) or a directory, typically the mods config directory.
   * 
   * To register a search path, one of two IMC messages must be sent.     
   * 
   * For example, the 'SmellyCheese' mod adds a 'Cheese Factory' structure. This requires cheesefact.gen, checkfact.stp and chesefact.nbt.
   * These files are included in the mods jar file with the path: /assets/smellycheese/structures. In this case, the following code should be
   * added to the mods load method:
   
     FMLInterModComms.sendMessage("EnderStructures", "addResourcePath", "/assets/smellycheese/structures");
    
   * The preferred method would be for 'Smelly Cheese Factory' to copy the required files into its config dir. This allows users to easily
   * modify the configs. In this case, the following code would be needed:
    
    File configDirectory = new File(event.getModConfigurationDirectory(), "smellycheese");
    FMLInterModComms.sendMessage("EnderStructures", "addResourceDir", configDirectory.getAbsolutePath());
    
    
   * Once the resource paths have been added each structure (.gen file) must be registered. This is done via another IMC message:
   
    FMLInterModComms.sendMessage("EnderStructures", "registerGenerator", "cheesefact"); 
           
   * 
   */
public class API {
  
  /*
   * The IMC messages supported by EnderStructures are included bellow for refernce. It is preferable not to
   * refer to these constants directly to avoid a depend on the API at runtime.
   */
  
  /**
   * Key for a string message to add a resource path to the search list used to load structures.
   */
  public static final String ADD_RESOURCE_PATH = "addResourcePath";
  
  /**
   * Key for a string message to add a resource directory to the search list used to load structures.
   */
  public static final String ADD_RESOURCE_DIR = "addResourceDir";
  
  /**
   * Key for a string message to register a generator, where the message payload is the UID of the generator. For example,
   * to register 'foo.gen' you set the string to 'foo'
   */
  public static final String REGISTER_GENERATOR = "registerGenerator";
  
  /**
   * Key for a string message to register a generator.
   */
  public static final String REGISTER_VILLAGE_GENERATOR = "registerVillageGenerator";
  
  /**
   * Key for a string message to register a type. The string will be used:
   * Class.forName(theString).newInstance(), which should return an ITyped instance
   */
  public static final String REGISTER_TYPE = "registerType";
  
}
