package cqg.monitor;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Supplier;


class CqgHttpServer extends Thread {

  private Supplier<String> supplier;
  private final int port;
  private final String CHARSET="UTF-8";

  public CqgHttpServer(Supplier<String> supplier, int port){
    super(Thread.currentThread().getThreadGroup(),"tvm-monitor-http-server");
    this.supplier=supplier;
    this.port=port;
  }

  public void run() {
    try {
      ServerSocket server=new ServerSocket(this.port);
      System.out.println("Accepting connections on port "+server.getLocalPort());
      while (true) {
        Socket connection=null;
        try {
          connection=server.accept();
          writeData(connection);
        } catch (IOException e) {
        }finally{
          if (connection!=null) {
            connection.close();
          }
        }
      }
    } catch (IOException e) {
      System.err.println("Could not start server. Port Occupied");
    }
  }

  private byte[] genHeader(int len) throws UnsupportedEncodingException {
    String header="HTTP/1.0 200 OK\r\n"+
      "Server: TVM 1.0\r\n"+
      "Content-length: "+len+"\r\n"+
      "Content-type: text/plain\r\n\r\n";
    return header.getBytes(CHARSET);
  }

  private void writeData(Socket connection) throws IOException {
    OutputStream out=new BufferedOutputStream(connection.getOutputStream());
    InputStream in=new BufferedInputStream(connection.getInputStream());

    StringBuffer request=new StringBuffer();
    while (true) {
      int c=in.read();
      if (c=='\r'||c=='\n'||c==-1) {
        break;
      }
      request.append((char)c);
    }
    //只接受带monitor的url，其他直接返回空
    String reqStr = request.toString();
    if(!reqStr.contains("monitor")){
      out.write(genHeader(0));
      out.flush();
      return;
    }

    String result=null;
    try{
      result = supplier.get();
      if(result==null){
        result="{}";
      }
    }catch (Exception e){
      System.out.println(e);
      result = "err";
    }
    byte[] bytes=result.getBytes(CHARSET);

    //如果检测到是HTTP/1.0及以后的协议，按照规范，需要发送一个MIME首部
    if (reqStr.indexOf("HTTP/")!=-1) {
      out.write(genHeader(bytes.length));
    }
    out.write(bytes);
    out.flush();
  }

}
