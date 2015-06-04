package controllers.mobile;

import controllers.BaseController;
import play.mvc.Http;

import java.io.*;

/**
 * Created by libaozhong on 2015/6/4.
 */
public class CoreService extends BaseController {
   public static void serviceauth() throws IOException {
       play.mvc.Http.Response.current().setHeader("contentType", "text/html; charset=utf-8");
       /** 读取接收到的xml消息 */
//       StringBuffer sb = new StringBuffer();
//       InputStream is = Http.Request.current().body;
//       InputStreamReader isr = null;
//       try {
//           isr = new InputStreamReader(is, "UTF-8");
//       } catch (UnsupportedEncodingException e) {
//           e.printStackTrace();
//       }
//       BufferedReader br = new BufferedReader(isr);
//       String s = "";
//       while ((s = br.readLine()) != null) {
//           sb.append(s);
//       }
//       String xml = sb.toString(); //次即为接收到微信端发送过来的xml数据

       String result = "";
       /** 判断是否是微信接入激活验证，只有首次接入验证时才会收到echostr参数，此时需要把它直接返回 */
       Http.Request reuqets = Http.Request.current();
       String echostr =reuqets.params.get("echostr");
       if (echostr != null && echostr.length() > 1) {
           result = echostr;
       } else {
           //正常的微信处理流程
//           result = new WechatProcess().processWechatMag(xml);
       }

       try {
           OutputStream os = Http.Response.current().out;
           os.write(result.getBytes("UTF-8"));
           os.flush();
           os.close();
       } catch (Exception e) {
           e.printStackTrace();
       }
   }
}
