package cqg.monitor.schema.internal;

import cqg.monitor.schema.JsonObjs;
import cqg.monitor.schema.JsonObjsSupplier;
import cqg.monitor.schema.FormatUtil;
import cqg.monitor.schema.JsonObj;

/**
 * Created by caoqingguang on 2017/3/29.
 */
public final class SysJsonObjsSupplier implements JsonObjsSupplier {

  private static final long startTime=System.currentTimeMillis();
  @Override
  public JsonObjs getJsonObj() {
    long now = System.currentTimeMillis();
    JsonObj jsonObj = new JsonObj()
      .setTime(FormatUtil.getCurrentMinuteMillis())
      .addValue("sys_start", startTime)
      .addValue("sys_now", now)
      .addDesc("sys_start",FormatUtil.formatMillis(startTime))
      .addDesc("sys_now",FormatUtil.formatMillis(now));
    return new JsonObjs("sys").addData(jsonObj);
  }


}
