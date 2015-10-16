package crazypants.structures.gen.io;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import crazypants.structures.api.gen.IChunkValidator;
import crazypants.structures.api.gen.ILocationSampler;
import crazypants.structures.api.gen.IStructureGenerator;
import crazypants.structures.api.gen.IStructureTemplate;
import crazypants.structures.gen.StructureRegister;
import crazypants.structures.gen.structure.StructureGenerator;

public class GeneratorParser {

  private final ParserRegister parsers = ParserRegister.instance;

  public GeneratorParser() {
  }

  public IStructureGenerator parseGeneratorConfig(StructureRegister reg, String uid, String json) throws Exception {

    StructureGenerator res = null;
    try {
      JsonObject to = new JsonParser().parse(json).getAsJsonObject();
      to = to.getAsJsonObject("StructureGenerator");

      res = new StructureGenerator(uid);
      if(to.has("maxAttemptsPerChunk")) {
        res.setMaxInChunk(to.get("maxAttemptsPerChunk").getAsInt());
      }
      if(to.has("maxGeneratedPerChunk")) {
        res.setMaxInChunk(to.get("maxGeneratedPerChunk").getAsInt());
      }

      if(!to.has("templates")) {
        throw new Exception("No templates field found in definition for " + uid);
      }
      JsonArray templates = to.get("templates").getAsJsonArray();
      for (JsonElement e : templates) {
        if(e.isJsonObject()) {
          JsonObject valObj = e.getAsJsonObject();
          if(!valObj.isJsonNull() && valObj.has("uid")) {
            String tpUid = valObj.get("uid").getAsString();
            IStructureTemplate st = reg.getStructureTemplate(tpUid, true);
            if(st != null) {
              res.addStructureTemaplate(st);
            }
          }
        }
      }
      if(res.getTemplates().isEmpty()) {
        throw new Exception("No valid template found in definition for " + uid);
      }

      if(to.has("LocationSampler")) {
        JsonObject ls = to.getAsJsonObject("LocationSampler");
        String samType = ls.get("type").getAsString();
        ILocationSampler samp = parsers.createSampler(samType, ls);
        if(samp != null) {
          res.setLocationSampler(samp);
        } else {
          throw new Exception("Could not parse location sampler for " + uid);
        }
      }

      if(to.has("chunkValidators")) {
        JsonArray arr = to.getAsJsonArray("chunkValidators");
        for (JsonElement e : arr) {
          if(e.isJsonObject()) {
            JsonObject valObj = e.getAsJsonObject();
            if(!valObj.isJsonNull() && valObj.has("type")) {
              String id = valObj.get("type").getAsString();
              IChunkValidator val = parsers.createChunkValidator(id, valObj);
              if(val != null) {
                res.addChunkValidator(val);
              } else {
                throw new Exception("Could not parse validator: " + id + " for template: " + uid);
              }
            }
          }
        }
      }
      
      reg.getResourceManager().getLootTableParser().parseLootTableCategories(to);

    } catch (Exception e) {
      e.printStackTrace();
      throw new Exception("TemplateParser: Could not parse generator " + uid, e);
    }

    if(res == null || !res.isValid()) {
      throw new Exception("GeneratorParser: Could not create a valid generator for " + res);
    }

    return res;

  }

}
