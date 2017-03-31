package cqg.monitor.schema.internal;

import cqg.monitor.schema.JsonObjs;
import cqg.monitor.schema.JsonObjsSupplier;
import cqg.monitor.schema.FormatUtil;
import cqg.monitor.schema.JsonObj;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadMXBean;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by caoqingguang on 2017/3/29.
 */
public final class JvmJsonObjsSupplier implements JsonObjsSupplier {

  private static final int MB=1024*1024;
  @Override
  public JsonObjs getJsonObj() {
    long time = FormatUtil.getCurrentMinuteMillis();
    return new JsonObjs("jvm")
      .addData(getThreadJson(time))
      .addData(getHeapJson(time))
      .addDatas(getGCJson(time))
      .addDatas(getMemoryDetailJson(time));
  }

  private JsonObj getThreadJson(long currentTime){
    ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
    return new JsonObj().setTime(currentTime)
      .addFlag("jvm_thread","jvm_thread")
      .addValue("thread_num", threadBean.getThreadCount());
  }

  private static Map<String,String> gcDic;
  static {
    Map<String,String> gcDicTmp=new HashMap<>();
    Arrays.asList("PS Scavenge", "ParNew", "Copy", "G1 Young Generation")
      .forEach(gc->gcDicTmp.put(gc,"YGC"));
    Arrays.asList("PS MarkSweep", "ConcurrentMarkSweep", "MarkSweepCompact", "G1 Old Generation")
      .forEach(gc->gcDicTmp.put(gc,"FGC"));
    gcDic = Collections.unmodifiableMap(gcDicTmp);
  }

  private List<JsonObj> getGCJson(long currentTime){
    List<JsonObj> list = new ArrayList<>();
    for (GarbageCollectorMXBean gcMXBean : ManagementFactory.getGarbageCollectorMXBeans()) {
      String name = gcMXBean.getName();
      String shotName = gcDic.get(gcMXBean.getName());
      if(shotName==null){
        shotName="Other";
      }
      JsonObj jsonObj = new JsonObj()
        .setTime(currentTime)
        .addFlag("jvm_gc_type", shotName)
        .addFlag("jvm_gc_type2", name)
        .addValue("jvm_gc_count", gcMXBean.getCollectionCount())
        .addValue("jvm_gc_usetime", gcMXBean.getCollectionTime());
      list.add(jsonObj);
    }
    return list;
  }

  private JsonObj getHeapJson(long currentTime) {
    MemoryUsage heapMemoryUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
    long used = heapMemoryUsage.getUsed();
    long max = heapMemoryUsage.getMax();
    used /= MB;
    max /= MB;
    float ratio=0;
    if(max>0){
      ratio = new BigDecimal(used*100f / max).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    return new JsonObj()
      .setTime(currentTime)
      .addFlag("jvm_heap_info", "jvm_heap_info")
      .addValue("jvm_heap_usesize", used)
      .addValue("jvm_heap_maxsize", max)
      .addValue("jvm_heap_ratio", ratio);
  }

  private static Map<String,String> memDic;
  static {
    Map<String,String> tmp=new HashMap<>();
    Arrays.asList("PS Eden Space","Par Eden Space","Eden Space","G1 Eden Space")
      .forEach(mem->tmp.put(mem,"Eden"));
    Arrays.asList("PS Survivor Space","Par Survivor Space","Survivor Space","G1_Survivor Space")
      .forEach(mem->tmp.put(mem,"Survivor"));
    Arrays.asList("PS Old Gen","CMS Old Gen","Tenured Gen","G1 Old Gen")
      .forEach(mem->tmp.put(mem,"OldGen"));
    memDic=Collections.unmodifiableMap(tmp);
  }
  private List<JsonObj> getMemoryDetailJson(long currentTime) {
    List<JsonObj> list=new ArrayList<>();
    // 内存区信息
    for (MemoryPoolMXBean memoryPoolMXBean : ManagementFactory.getMemoryPoolMXBeans()) {
      String name = memoryPoolMXBean.getName();
      String shortName = memDic.get(name);
      if(shortName==null){
        shortName="Other";
      }
      MemoryUsage memoryUsage = memoryPoolMXBean.getUsage();
      long used = memoryUsage.getUsed();
      long max = memoryUsage.getMax();
      used /= MB;
      max /= MB;
      float ratio=0;
      if(max>0){
        ratio = new BigDecimal(used*100f / max).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
      }
      JsonObj jsonObj = new JsonObj()
        .setTime(currentTime)
        .addFlag("jvm_mem_type", shortName)
        .addFlag("jvm_mem_type2", name)
        .addValue("jvm_mem_usesize", used)
        .addValue("jvm_mem_maxsize", max)
        .addValue("jvm_mem_ratio", ratio);
      list.add(jsonObj);
    }
    return list;
  }
}
