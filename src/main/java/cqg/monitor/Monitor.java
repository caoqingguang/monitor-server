package cqg.monitor;

import cqg.monitor.schema.JsonObjs;
import cqg.monitor.schema.JsonObjsSupplier;
import cqg.monitor.schema.internal.SysJsonObjsSupplier;
import cqg.monitor.schema.internal.JvmJsonObjsSupplier;
import cqg.monitor.schema.internal.CodeJsonObjsSupplier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 统计专用客户端
 * Created by caoqingguang on 2017/3/3.
 */
public final class Monitor {

  private static final List<JsonObjsSupplier> dumpSuppiers=new ArrayList<>(4);
  private static CodeJsonObjsSupplier topicGroupCenter;
  private static final Function<List<JsonObjs>,String> convert= CqgJsonHelper::fmtJsonObjsList;
  private static boolean hasStart;

  public synchronized static void start(int port){
    if(hasStart){
      return;
    }
    try {
      dumpSuppiers.clear();
      regSupplier(new SysJsonObjsSupplier());
      topicGroupCenter = new CodeJsonObjsSupplier();
      regSupplier(topicGroupCenter);
      regSupplier(new JvmJsonObjsSupplier());
      new CqgHttpServer(Monitor::dumpStatistics,port)
        .start();
      System.out.println("统计用客户端  初始化完成");
      hasStart=true;
    } catch (Exception e) {
      System.out.println("统计用客户端  初始化失败");
      e.printStackTrace();
    }
  }

  //注册扩展信息
  public static void regSupplier(JsonObjsSupplier supplier){
    if(supplier==null){
      return;
    }
    dumpSuppiers.add(supplier);
  }

  public static void incrCount(String topic) {
    action(()->topicGroupCenter.incrCount(topic));
  }

  public static void incrTime(String topic) {
    action(()->topicGroupCenter.incrTime(topic));
  }

  public static void incrCountBy(String topic, int addCount) {
    action(()->topicGroupCenter.incrCountBy(topic, addCount));
  }

  public static void incrTimeBy(String topic, int millis){
    action(()->topicGroupCenter.incrTimeBy(topic,millis));
  }

  public static void incrCountAndTimeBy(String topic, long millis) {
    action(()->topicGroupCenter.incrCountAndTimeBy(topic, millis));
  }

  public static void incrCountAndTimeBy(String topic, int addCount, long millis) {
    action(()->topicGroupCenter.incrCountAndTimeBy(topic, addCount, millis));
  }

  private static void action(CqgAction action){
    if(hasStart){
      try{
        action.doit();
      }catch (Exception e){
      }
    }
  }


  public static String dumpStatistics() {
    List<JsonObjs> collect = dumpSuppiers.stream()
      .map(supplier -> supplier.getJsonObj())
      .collect(Collectors.toList());
    return convert.apply(collect);
  }

}
