package crazypants.structures.gen.io;

import static  crazypants.structures.gen.io.JsonUtil.getTypedObjectArray;

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
import crazypants.structures.api.runtime.IBehaviour;
import crazypants.structures.api.util.Point3i;
import crazypants.structures.api.util.Rotation;
import crazypants.structures.gen.StructureGenRegister;
import crazypants.structures.gen.io.JsonUtil.TypedObject;
import crazypants.structures.gen.structure.StructureTemplate;

public class TemplateParser {

  
  private final ParserRegister parsers = ParserRegister.instance;
   
  public StructureTemplate parseTemplateConfig(StructureGenRegister reg, String uid, String json) throws Exception {

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
              Point3i offset = JsonUtil.getPoint3iField(valObj, "offset", null);
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
      
      List<TypedObject> arrayContents;

      arrayContents = getTypedObjectArray(to, "siteValidators");
      for(TypedObject o : arrayContents) {
        ISiteValidator sv = parsers.createSiteValidator(o.type, o.obj);
        if(sv != null) {
          res.addSiteValidator(sv);
        } else {
          Log.warn("Could not parse site validator with type: " + o.type + " in template " + uid);
        }
      }
      
      arrayContents = getTypedObjectArray(to, "sitePreperations");
      for(TypedObject o : arrayContents) {
        ISitePreperation sv = parsers.createPreperation(o.type, o.obj);
        if(sv != null) {
          res.addSitePreperation(sv);
        } else {
          Log.warn("Could not parse site preperation with type: " + o.type + " in template " + uid);
        }
      }
      
      arrayContents = getTypedObjectArray(to, "decorators");
      for(TypedObject o : arrayContents) {
        IDecorator dec = parsers.createDecorator(o.type, o.obj);
        if(dec != null) {
          res.addDecorator(dec);
        } else {
          Log.warn("Could not parse decorator with type: " + o.type + " in template " + uid);
        }
      }
      
      arrayContents = getTypedObjectArray(to, "behaviours");
      for(TypedObject o : arrayContents) {
        IBehaviour behav = parsers.ceateBehaviour(o.type, o.obj);
        if(behav != null) {
          res.addBehaviour(behav);
        } else {
          Log.warn("Could not parse behaviours with type: " + o.type + " in template " + uid);
        }
      }

      reg.getResourceManager().getLootTableParser().parseLootTableCategories(to);      

    } catch (Exception e) {
      throw new Exception("TemplateParser: Could not parse generator template " + uid + ". " + e.getMessage(), e);
    }

    if(res == null || !res.isValid()) {
      throw new Exception("GeneratorParser: Could not create a valid template for " + res + " uid: " + uid);
    }

    return res;
  }

  
}
