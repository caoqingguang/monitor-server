package cqg.monitor.schema;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caoqingguang on 2017/3/29.
 */
public final class JsonObjs {
  private final String type;
  private final List<JsonObj> data=new ArrayList<>();

  public JsonObjs(String type) {
    this.type = type;
  }

  public String getType(){
    return type;
  }

  public JsonObjs addData(JsonObj jsonObj){
    data.add(jsonObj);
    return this;
  }

  public JsonObjs addDatas(List<JsonObj> list){
    data.addAll(list);
    return this;
  }

  public List<JsonObj> getData() {
    return data;
  }
}
