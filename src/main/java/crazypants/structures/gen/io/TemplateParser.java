package crazypants.structures.gen.io;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import crazypants.structures.Log;
import crazypants.structures.api.gen.IDecorator;
import crazypants.structures.api.gen.ISitePreperation;
import crazypants.structures.api.gen.ISiteValidator;
import crazypants.structures.api.gen.IStructureComponent;
import crazypants.structures.api.util.Point3i;
import crazypants.structures.api.util.Rotation;
import crazypants.structures.gen.StructureRegister;
import crazypants.structures.gen.structure.StructureTemplate;

public class TemplateParser {

  
  private final ParserRegister parsers = ParserRegister.instance;
   
  public StructureTemplate parseTemplateConfig(StructureRegister reg, String uid, String json) throws Exception {

    StructureTemplate res = null;
    try {

      JsonObject to = new JsonParser().parse(json).getAsJsonObject();
      to = to.getAsJsonObject("StructureTemplate");

      res = new StructureTemplate(uid);

      if(to.has("canSpanChunks")) {
        res.setCanSpanChunks(to.get("canSpanChunks").getAsBoolean());
      }

      JsonArray components = to.get("components").getAsJsonArray();
      for (JsonElement e : components) {
        if(e.isJsonObject()) {
          JsonObject valObj = e.getAsJsonObject();
          if(!valObj.isJsonNull()&& valObj.has("uid")) {
            String compUid = valObj.get("uid").getAsString();            
            IStructureComponent st = reg.getStructureComponent(compUid, true);            
            if(st != null) {
              Point3i offset = null;
              if(valObj.has("offset")) {
                JsonElement offsetJE = valObj.get("offset");
                if(offsetJE.isJsonArray()) {
                  JsonArray arr = offsetJE.getAsJsonArray();
                  if(!arr.isJsonNull() && arr.size() == 3) {
                    offset = new Point3i(arr.get(0).getAsInt(), arr.get(1).getAsInt(), arr.get(2).getAsInt());                    
                  }
                }
              }
              res.addComponent(st, offset);
            }
          }
        }
      }
      if(res.getComponents().isEmpty()) {
        throw new Exception("No valid components found in definition for " + uid);
      }

      if(to.has("rotations")) {
        List<Rotation> rots = new ArrayList<Rotation>();
        JsonElement jrots = to.get("rotations");
        if(jrots.isJsonArray()) {
          for (JsonElement rot : jrots.getAsJsonArray()) {
            if(!rot.isJsonNull()) {
              Rotation r = Rotation.get(rot.getAsInt());
              if(r != null) {
                rots.add(r);
              }
            }
          }
        }
        res.setRotations(rots);
      }

      if(to.has("siteValidators")) {

        JsonArray arr = to.getAsJsonArray("siteValidators");
        for (JsonElement e : arr) {
          if(e.isJsonObject()) {
            JsonObject valObj = e.getAsJsonObject();
            if(!valObj.isJsonNull() && valObj.has("type")) {
              String id = valObj.get("type").getAsString();
              ISiteValidator val = parsers.createSiteValidator(id, valObj);
              if(val != null) {
                res.addSiteValidator(val);
              } else {
                throw new Exception("Could not parse validator: " + id + " for template: " + uid);
              }
            }
          }
        }

      }

      if(to.has("sitePreperations")) {

        JsonArray arr = to.getAsJsonArray("sitePreperations");
        for (JsonElement e : arr) {
          if(e.isJsonObject()) {
            JsonObject valObj = e.getAsJsonObject();
            if(!valObj.isJsonNull() && valObj.has("type")) {
              String id = valObj.get("type").getAsString();
              ISitePreperation val = parsers.createPreperation(id, valObj);
              if(val != null) {
                res.addSitePreperation(val);
              } else {
                throw new Exception("Could not parse preperation: " + id + " for template: " + uid);
              }
            }
          }
        }

      }

      if(to.has("decorators")) {

        JsonArray arr = to.getAsJsonArray("decorators");
        for (JsonElement e : arr) {
          if(e.isJsonObject()) {
            JsonObject valObj = e.getAsJsonObject();
            if(!valObj.isJsonNull() && valObj.has("type")) {
              String id = valObj.get("type").getAsString();
              IDecorator dec = parsers.createDecorator(id, valObj);
              if(dec != null) {
                res.addDecorator(dec);
              } else {
                //throw new Exception("Could not parse decorator: " + id + " for template: " + uid);
                Log.warn("Could not parse decorator with type: " + id + " in template " + uid);
              }
            }
          }
        }

      }

      ChestGenParser.parseChestGen(to);

    } catch (Exception e) {
      throw new Exception("TemplateParser: Could not parse generator template " + uid + ". " + e.getMessage(), e);
    }

    if(res == null || !res.isValid()) {
      throw new Exception("GeneratorParser: Could not create a valid template for " + res + " uid: " + uid);
    }

    return res;
  }
  
}
