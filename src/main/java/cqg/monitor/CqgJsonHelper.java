package cqg.monitor;

import cqg.monitor.schema.JsonObjs;
import cqg.monitor.schema.JsonObj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by caoqingguang on 2017/3/31.
 */
abstract class CqgJsonHelper {
  private static final String SEP_KV=":";
  private static final String SEP_VV=",";
  private static final String JSON_START="{";
  private static final String JSON_END="}";
  private static final String ARR_START="[";
  private static final String ARR_END="]";

  private static String fmtKeyValue(String key,Object value){
    if(value==null||key==null){
      return null;
    }
    key=key.replace("\"","");
    if(Number.class.isAssignableFrom(value.getClass())){
      return String.format("\"%s\"%s%s",key,SEP_KV,value);
    }
    String val=value.toString().replace("\"","");
    return String.format("\"%s\"%s\"%s\"",key,SEP_KV,val);
  }

  private static String fmtKeyResultValue(String key,String value){
    if(key==null||value==null){
      return null;
    }
    key=key.replace("\"","");
    return String.format("\"%s\"%s%s",key,SEP_KV,value);
  }

  private static String fmtMap(Map<String,?> simpleMap){
    if(simpleMap==null){
      return null;
    }
    List<String> collect = simpleMap.entrySet()
      .stream()
      .filter(en -> en != null)
      .map(en -> fmtKeyValue(en.getKey(), en.getValue()))
      .filter(r -> r != null)
      .collect(Collectors.toList());
    return JSON_START+String.join(SEP_VV,collect)+JSON_END;
  }


  private static String fmtJsonObj(JsonObj jsonObj){
    if(jsonObj==null){
      return null;
    }
    return StrBuilder.start(4)
      .addNotNull(fmtKeyValue("time", jsonObj.getTime()))
      .addNotNull(fmtKeyResultValue("flags",fmtMap(jsonObj.getFlags())))
      .addNotNull(fmtKeyResultValue("values",fmtMap(jsonObj.getValues())))
      .addNotNull(fmtKeyResultValue("desc",fmtMap(jsonObj.getDesc())))
      .end(JSON_START,JSON_END,SEP_VV);
  }
  private static String fmtJsonObjList(List<JsonObj> jsonObjList){
    if(jsonObjList==null){
      return null;
    }
    StrBuilder dataBuilder = StrBuilder.start();
    for (JsonObj jsonObj : jsonObjList) {
      dataBuilder.addNotNull(fmtJsonObj(jsonObj));
    }
    return dataBuilder.end(ARR_START,ARR_END,SEP_VV);
  }

  private static String fmtJsonObjs(JsonObjs jsonObjs){
    if(jsonObjs==null){
      return null;
    }
    return StrBuilder.start(2)
      .addNotNull(fmtKeyValue("type", jsonObjs.getType()))
      .addNotNull(fmtKeyResultValue("data",fmtJsonObjList(jsonObjs.getData())))
      .end(JSON_START,JSON_END,SEP_VV);
  }

  public static String fmtJsonObjsList(List<JsonObjs> jsonObjsList){
    if(jsonObjsList==null){
      return null;
    }
    StrBuilder data = StrBuilder.start(jsonObjsList.size());
    for (JsonObjs jsonObjs : jsonObjsList) {
      data.addNotNull(fmtJsonObjs(jsonObjs));
    }
    return data.end(ARR_START,ARR_END,SEP_VV);
  }

  public static void main(String[] args) {
    System.out.println(fmtKeyValue("aaa","bbb"));
    System.out.println(fmtKeyValue("aaa",234));
    Map<String,Object> map=new HashMap<>();
    map.put("aaa",1);
    map.put("bbb","bbbbb");
    JsonObj jsonObj = new JsonObj();
    jsonObj.setTime(System.currentTimeMillis())
      .addFlag("aaa","topic1")
      .addFlag("bbbb","topic2")
      .addValue("val",1)
      .addValue("value2",2.0f);
    JsonObjs sys = new JsonObjs("sys").addData(jsonObj).addData(jsonObj);
    int i=0;
    long t = System.currentTimeMillis();
    while (i<10000){
      i++;
      fmtJsonObjs(sys);
//      System.out.println(fmtJsonObjs(sys));
    }
    System.out.println("end"+(System.currentTimeMillis()-t));
  }

  private static class StrBuilder{
    List<String> list;
    StrBuilder(){
      list=new ArrayList<>();
    }
    StrBuilder(int len){
      list=new ArrayList<>(len);
    }
    static StrBuilder start(){
      return new StrBuilder();
    }
    static StrBuilder start(int len){
      return new StrBuilder(len);
    }

    StrBuilder addNotNull(String value){
      if(value!=null&&!"".equals(value)){
        list.add(value);
      }
      return this;
    }


    String end(String startChar,String endChar,String sepChar){
      return String.format("%s %s %s",startChar,String.join(sepChar,list),endChar);
    }

  }
}
