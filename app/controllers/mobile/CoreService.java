package controllers.mobile;

import business.Token;
import constants.Constants;
import controllers.BaseController;
import net.sf.json.JSONObject;
import org.apache.commons.logging.Log;
import play.Logger;
import play.mvc.Http;
import play.mvc.results.Redirect;
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
    /***入口url
     * https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx320badb1a6f6b806&redirect_uri=http%3A%2F%2Fp2pv2.sunlights.me%2Fmobile%2FquickRegister&response_type=code&scope=snsapi_base&state=123#wechat_redirect
     *
     *
     */

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

    public static void getGateUrl() throws IOException {
       String url= WebChartUtil.geturl();
        Logger.info("url：" + url);
    }

    public static void getCode() {

        Http.Response.current().setContentTypeIfNotSet("text/html; charset=utf-8");
        Logger.info("用户进入：");
        String code = Http.Request.current().params.get("code");
        String status= Http.Request.current().params.get("state");
        Logger.info("code："+code+"state"+status);
        try {
            OutputStream os = Http.Response.current().out;
            os.write(code.getBytes());
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void getUserToken() throws IOException {

        Http.Response.current().setContentTypeIfNotSet("text/html; charset=utf-8");
        Logger.info("用户进入：");
        String code = Http.Request.current().params.get("code");
        String status = Http.Request.current().params.get("state");
        Logger.info("code为：" + code + "status:" + status);
        JSONObject authInfo = WebChartUtil.getTOKENANDOPENID(code);
    }

    public static void getOpenId() throws IOException {

        Http.Response.current().setContentTypeIfNotSet("text/html; charset=utf-8");
        Logger.info("用户进入：");
        String code= Http.Request.current().params.get("code");
        String status= Http.Request.current().params.get("state");
        Logger.info("code为："+code+"status:"+status);
        JSONObject authInfo=WebChartUtil.getOpenIdAuth(code);
        Object openid = authInfo.get("openid");

       String openId="";
        if(null!=openid){
            openId= openid.toString();
        }
        Logger.info("openid为："+openId);
        if(null!=openid && openId.trim()!=""){
            if(status.equals(Constants.WEIXINSTATUS.LOGIN)){
                Logger.info("openid:"+openId+"status:"+status);

                LoginAction.login();
             }else
            if(status.equals(Constants.WEIXINSTATUS.REGISTER)){
                Logger.info("openid:"+openId+"status:"+status);
                LoginAction.register(openId, status);
            }else
            if(status.equals(Constants.WEIXINSTATUS.QUICKREGISTERSUCCESS)){
                Logger.info("openid:"+openId+"status:"+status);
                QuickRegister.registerSuccess(openId, status);
            }else
            if(status.equals(Constants.WEIXINSTATUS.MOBILEHADREGISTER)){
                Logger.info("openid:"+openId+"status:"+status);
                QuickRegister.registerSuccess(openId,status);
            }
            if(status.equals(Constants.WEIXINSTATUS.INTERCEPTORREDIRECT)){
                Logger.info("openid:" + openId + "status:" + status);
                flash.put("openId",openId);
                    MainContent.moneyMatters();
                }
        }else{
         render();
        }
    }
    public static void landding(){
        render();
    }
    public static void serviceprocess() throws IOException {
        Http.Response.current().setContentTypeIfNotSet("text/html; charset=utf-8");
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
