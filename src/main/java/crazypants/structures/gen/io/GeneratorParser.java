package crazypants.structures.gen.io;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import crazypants.structures.Log;
import crazypants.structures.api.gen.IChunkValidator;
import crazypants.structures.api.gen.IDecorator;
import crazypants.structures.api.gen.ILocationSampler;
import crazypants.structures.api.gen.ISitePreperation;
import crazypants.structures.api.gen.ISiteValidator;
import crazypants.structures.api.gen.IStructureComponent;
import crazypants.structures.api.gen.IStructureGenerator;
import crazypants.structures.api.gen.IStructureTemplate;
import crazypants.structures.api.util.Rotation;
import crazypants.structures.gen.StructureRegister;
import crazypants.structures.gen.structure.StructureGenerator;
import crazypants.structures.gen.structure.StructureTemplate;

public class GeneratorParser {

  private final ParserRegister parsers = ParserRegister.instance;

  public GeneratorParser() {
  }

  public IStructureGenerator parseGeneratorConfig(StructureRegister reg, String json) throws Exception {
    String uid = null;
    StructureGenerator res = null;
    try {
      JsonObject to = new JsonParser().parse(json).getAsJsonObject();
      to = to.getAsJsonObject("StructureGenerator");

      uid = to.get("uid").getAsString();

      res = new StructureGenerator(uid);      
      if (to.has("maxAttemptsPerChunk")) {
        res.setMaxInChunk(to.get("maxAttemptsPerChunk").getAsInt());
      }
      if (to.has("maxGeneratedPerChunk")) {
        res.setMaxInChunk(to.get("maxGeneratedPerChunk").getAsInt());
      }

      if (!to.has("templates")) {
        throw new Exception("No templates field found in definition for " + uid);
      }
      JsonArray templates = to.get("templates").getAsJsonArray();
      for (JsonElement e : templates) {
        JsonObject valObj = e.getAsJsonObject();
        if (!valObj.isJsonNull() && valObj.has("uid")) {
          String tpUid = valObj.get("uid").getAsString();
          IStructureTemplate st = reg.getStructureTemplate(tpUid, true);
          if (st != null) {
            res.addStructureTemaplate(st);
          }
        }
      }
      if (res.getTemplates().isEmpty()) {
        throw new Exception("No valid template found in definition for " + uid);
      }

      if (to.has("LocationSampler")) {
        JsonObject ls = to.getAsJsonObject("LocationSampler");
        String samType = ls.get("type").getAsString();
        ILocationSampler samp = parsers.createSampler(samType, ls);
        if (samp != null) {
          res.setLocationSampler(samp);
        } else {
          throw new Exception("Could not parse location sampler for " + uid);
        }
      }

      if (to.has("chunkValidators")) {
        JsonArray arr = to.getAsJsonArray("chunkValidators");
        for (JsonElement e : arr) {
          if (e.isJsonObject()) {
            JsonObject valObj = e.getAsJsonObject();
            if (!valObj.isJsonNull() && valObj.has("type")) {
              String id = valObj.get("type").getAsString();
              IChunkValidator val = parsers.createChunkValidator(id, valObj);
              if (val != null) {
                res.addChunkValidator(val);
              } else {
                throw new Exception("Could not parse validator: " + id + " for template: " + uid);
              }
            }
          }
        }
      }
      
      ChestGenParser.parseChestGen(to);

    } catch (Exception e) {
      e.printStackTrace();
      throw new Exception("TemplateParser: Could not parse generator " + uid, e);
    }

    if (res == null || !res.isValid()) {
      throw new Exception("GeneratorParser: Could not create a valid generator for " + res);
    }

    return res;

  }

  

  public StructureTemplate parseTemplateConfig(StructureRegister reg, String json) throws Exception {

    String uid = null;
    StructureTemplate res = null;
    try {

      JsonObject to = new JsonParser().parse(json).getAsJsonObject();
      to = to.getAsJsonObject("StructureTemplate");

      uid = to.get("uid").getAsString();
      res = new StructureTemplate(uid);

      if (to.has("canSpanChunks")) {
        res.setCanSpanChunks(to.get("canSpanChunks").getAsBoolean());
      }

      JsonArray components = to.get("components").getAsJsonArray();
      for (JsonElement e : components) {
        JsonObject valObj = e.getAsJsonObject();
        if (!valObj.isJsonNull() && valObj.has("uid")) {
          String tpUid = valObj.get("uid").getAsString();
          IStructureComponent st = reg.getStructureComponent(tpUid, true);
          if (st != null) {
            res.addComponent(st);
          }
        }
      }
      if (res.getComponents().isEmpty()) {
        throw new Exception("No valid components found in definition for " + uid);
      }

      if (to.has("rotations")) {
        List<Rotation> rots = new ArrayList<Rotation>();
        for (JsonElement rot : to.get("rotations").getAsJsonArray()) {
          if (!rot.isJsonNull()) {
            Rotation r = Rotation.get(rot.getAsInt());
            if (r != null) {
              rots.add(r);
            }
          }
        }
        res.setRotations(rots);
      }

      if (to.has("siteValidators")) {

        JsonArray arr = to.getAsJsonArray("siteValidators");
        for (JsonElement e : arr) {
          JsonObject valObj = e.getAsJsonObject();
          if (!valObj.isJsonNull() && valObj.has("type")) {
            String id = valObj.get("type").getAsString();
            ISiteValidator val = parsers.createSiteValidator(id, valObj);
            if (val != null) {
              res.addSiteValidator(val);
            } else {
              throw new Exception("Could not parse validator: " + id + " for template: " + uid);
            }
          }
        }

      }
      
      
      if (to.has("sitePreperations")) {

        JsonArray arr = to.getAsJsonArray("sitePreperations");
        for (JsonElement e : arr) {
          JsonObject valObj = e.getAsJsonObject();
          if (!valObj.isJsonNull() && valObj.has("type")) {
            String id = valObj.get("type").getAsString();
            ISitePreperation val = parsers.createPreperation(id, valObj);
            if (val != null) {
              res.addSitePreperation(val);
            } else {
              throw new Exception("Could not parse preperation: " + id + " for template: " + uid);
            }
          }
        }

      }
      
      if (to.has("decorators")) {

        JsonArray arr = to.getAsJsonArray("decorators");
        for (JsonElement e : arr) {
          JsonObject valObj = e.getAsJsonObject();
          if (!valObj.isJsonNull() && valObj.has("type")) {
            String id = valObj.get("type").getAsString();
            IDecorator dec = parsers.createDecorator(id, valObj);
            if (dec != null) {
              res.addDecorator(dec);
            } else {
              //throw new Exception("Could not parse decorator: " + id + " for template: " + uid);
              Log.warn("Could not parse decorator with type: " + id + " in template " + uid);
            }
          }
        }

      }
      
      ChestGenParser.parseChestGen(to);

    } catch (Exception e) {
      throw new Exception("TemplateParser: Could not parse generator template " + uid + ". " + e.getMessage(), e);
    }

    if (res == null || !res.isValid()) {
      throw new Exception("GeneratorParser: Could not create a valid template for " + res + " uid: " + uid);
    }

    return res;
  }

}
