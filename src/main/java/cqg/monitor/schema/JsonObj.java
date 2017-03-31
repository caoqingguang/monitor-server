package cqg.monitor.schema;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by caoqingguang on 2017/3/29.
 */
public final class JsonObj {
  long time=System.currentTimeMillis();
  Map<String,Object> flags=new HashMap<>(8);
  Map<String,Object> values=new HashMap<>(4);
  Map<String,Object> desc=new HashMap<>(2);

  public long getTime(){
    return time;
  }
  public JsonObj setTime(long time){
    this.time=time;
    return this;
  }
  public JsonObj setFlags(Map<String,String> flags){
    this.flags.clear();
    this.flags.putAll(flags);
    return this;
  }
  public JsonObj addFlag(String name,Object value){
    this.flags.put(name,value);
    return this;
  }

  public Map<String, Object> getFlags() {
    return flags;
  }

  public JsonObj setValues(Map<String,Object> values){
    this.values.clear();
    this.values.putAll(values);
    return this;
  }

  public JsonObj addValue(String name,Object value){
    this.values.put(name,value);
    return this;
  }

  public Map<String, Object> getValues() {
    return values;
  }

  public Map<String, Object> getDesc() {
    desc.put("human_time", FormatUtil.formatMillis(time));
    return desc;
  }

  public JsonObj addDesc(String descName,String descValue) {
    this.desc.put(descName,descValue);
    return this;
  }

}
