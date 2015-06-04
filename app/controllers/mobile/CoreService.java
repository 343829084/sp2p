package controllers.mobile;

import business.Token;
import controllers.BaseController;
import net.sf.json.JSONObject;
import org.apache.commons.logging.Log;
import play.Logger;
import play.mvc.Http;
import utils.WebChartUtil;

import java.io.*;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;

import static utils.WebChartUtil.getOpenIdAuth;

/**
 * Created by libaozhong on 2015/6/4.
 */
public class CoreService extends BaseController {

    private static Token token =new Token();
    public static String  GetCodeRequest = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect";
    public static String  GETTOKEN = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";

    public static void serviceauth() throws IOException {
       play.mvc.Http.Response.current().setHeader("contentType", "text/html; charset=utf-8");
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



    public static void getOpenId() throws IOException {

        Http.Response.current().setContentTypeIfNotSet("text/html; charset=utf-8");

//        String resulit = getOpenIdAuth();
        Logger.info("用户进入：");
        Logger.info("参数：" + request.toString());
//        String code=request.params.get("code");
  
//        renderHtml(resulit);

    }
    public static void serviceprocess() throws IOException {
        Http.Response.current().setContentTypeIfNotSet("text/html; charset=utf-8");

        String code=request.params.get("code");
        Http.Request reuqets = Http.Request.current();
        if(null!=token.getExpireDate() && null!=token.getValue() && token.getExpireDate().after(new Date())){
            final JSONObject newToken = WebChartUtil.getToken();
            token.setValue("TOKEN");
            Calendar ca=Calendar.getInstance();
            ca.setTime(new Date());
            ca.add(Calendar.MINUTE,120);
            token.setExpireDate(ca.getTime());
        }

        try {
            OutputStream os = Http.Response.current().out;

            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
