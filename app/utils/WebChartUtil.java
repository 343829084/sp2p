package utils;

import constants.Constants;
import net.sf.json.JSONObject;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URLEncoder;


/**
 * Created by libaozhong on 2015/6/4.
 */
public  class WebChartUtil {
    public static final String WECHAT = "FP.SOCIAL.TYPE.1";

    //01184441bb65fbd817c6996609e6e4dQ
    static class WeChatConstants {
        private static String OPENIDURL =  "https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=redUrl&response_type=code&scope=snsapi_base&state=STATUS#wechat_redirect";
        public static String  GETTOKEN =   "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
        public static String CODEEXCHANGEOPENID="https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
    }
    public  static String buildWeChatGateUrl(String status){
        WeChatConstants.OPENIDURL = WeChatConstants.OPENIDURL.replace("APPID", urlEnodeUTF8(Constants.WECHAT_APPID));
        WeChatConstants.OPENIDURL = WeChatConstants.OPENIDURL.replace("SECRET", urlEnodeUTF8(Constants.WECHAT_APPSECRET));
        WeChatConstants.OPENIDURL = WeChatConstants.OPENIDURL.replace("redUrl", urlEnodeUTF8(Constants.WECHAT_CALLBACK_URL));
        if (StringUtils.isNotEmpty(status)) {
            WeChatConstants.OPENIDURL = WeChatConstants.OPENIDURL.replace("STATUS", status);
        }
        return WeChatConstants.OPENIDURL;
    }

    public  static String buildRequestOpenIdUrl(String code){
        WeChatConstants.CODEEXCHANGEOPENID = WeChatConstants.CODEEXCHANGEOPENID.replace("APPID", urlEnodeUTF8(Constants.WECHAT_APPID));
        WeChatConstants.CODEEXCHANGEOPENID = WeChatConstants.CODEEXCHANGEOPENID.replace("SECRET", urlEnodeUTF8(Constants.WECHAT_APPSECRET));
        WeChatConstants.CODEEXCHANGEOPENID = WeChatConstants.CODEEXCHANGEOPENID.replace("CODE", urlEnodeUTF8(code));
        return WeChatConstants.CODEEXCHANGEOPENID;
    }

    public static String getOpenIdAuth(String code) throws IOException {

        String url= buildRequestOpenIdUrl(code);
        HttpClient httpClient = new HttpClient();
        GetMethod getMethod = new GetMethod(url);
        getMethod.addRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");// 在头文件中设置转码
        httpClient.setTimeout(3000);
        int statusCode = httpClient.executeMethod(getMethod);

        JSONObject resultStr = JSONObject.fromObject(getMethod.getResponseBodyAsString());

        if (resultStr == null || resultStr.get("openid") == null) {
            return null;
        }

        return resultStr.get("openid").toString();
    }


    public static String urlEnodeUTF8(String str) {
        String result = str;
        try {
            result = URLEncoder.encode(str, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public static JSONObject getToken() {
        String result = null;
        WeChatConstants.GETTOKEN  = WeChatConstants.GETTOKEN.replace("APPID", urlEnodeUTF8(Constants.WECHAT_APPID));
        WeChatConstants.GETTOKEN  = WeChatConstants.GETTOKEN.replace("APPSECRET",urlEnodeUTF8(Constants.WECHAT_APPSECRET));
        String severity = null;
        Object message = null;
        try {
          return  getReqMethod(WeChatConstants.GETTOKEN);

        }catch (Exception e){

        }
        return null;
    }
    public static JSONObject getReqMethod(String url) throws IOException {
        HttpClient httpClient = new HttpClient();
        GetMethod getMethod = new GetMethod(url);
        getMethod.addRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=gbk");// 在头文件中设置转码

        httpClient.setTimeout(3000);
        int statusCode = httpClient.executeMethod(getMethod);
        JSONObject jsonResult=new JSONObject();
        String resultStr = getMethod.getResponseBodyAsString();

        jsonResult =JSONObject.fromObject(resultStr);;

        return jsonResult;
    }



}
