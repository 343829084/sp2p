package utils;

import constants.Constants;
import net.sf.json.JSONObject;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import play.mvc.Http;

import java.io.*;
import java.net.URLEncoder;

/**
 * Created by libaozhong on 2015/6/4.
 */
public  class WebChartUtil {
    //01184441bb65fbd817c6996609e6e4dQ
    static class Constants{
        static final String appId="wx320badb1a6f6b806";
        static final String appsecret="6b4fe8bb2a14522e2391984b3f303a9a";
        private String get_access_token_url="https://api.weixin.qq.com/sns/oauth2/access_token?" +
                "appid=APPID" +
                "&secret=SECRET&" +
                "code=CODE&grant_type=authorization_code";
        private static String  BINDURL="http://p2pv2.sunlights.me/mobile/quickRegister";
        private static String  BINDURLBACK="http://p2pv2.sunlights.me/mobile/weixin/openId";
        private static String OPENIDURL= "https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=redUrl&response_type=code&scope=snsapi_base&state=123#wechat_redirect";
        private static String GETUERINFO="https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
        public static String  GETCODEURL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect";
        public static String  GETTOKEN = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
        public static String CODEEXCHANGEOPENID="https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
    }
    public  static String geturl(){
        Constants.OPENIDURL=  Constants.OPENIDURL.replace("APPID", urlEnodeUTF8(Constants.appId));
        Constants.OPENIDURL=  Constants.OPENIDURL.replace("SECRET", urlEnodeUTF8(Constants.appsecret));
        Constants.OPENIDURL=  Constants.OPENIDURL.replace("redUrl", urlEnodeUTF8(Constants.BINDURLBACK));
        return Constants.OPENIDURL;
    }

    public  static String setUrl(String code){
//        String newUrl = URLEncoder.encode(redurl);

        Constants.CODEEXCHANGEOPENID= Constants.CODEEXCHANGEOPENID.replace("APPID", urlEnodeUTF8(Constants.appId));
        Constants.CODEEXCHANGEOPENID= Constants.CODEEXCHANGEOPENID.replace("SECRET", urlEnodeUTF8(Constants.appsecret));
        Constants.CODEEXCHANGEOPENID= Constants.CODEEXCHANGEOPENID.replace("CODE", urlEnodeUTF8(code));
        return Constants.CODEEXCHANGEOPENID;
    }

    public static String getOpenIdAuth(String code) throws IOException {

        String url= setUrl(code);
        HttpClient httpClient = new HttpClient();
        GetMethod getMethod = new GetMethod(url);
        getMethod.addRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");// 在头文件中设置转码
        httpClient.setTimeout(3000);
        int statusCode = httpClient.executeMethod(getMethod);

        String resultStr = getMethod.getResponseBodyAsString();
     return resultStr;

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
        Constants.GETTOKEN  = Constants.GETTOKEN.replace("APPID", urlEnodeUTF8(Constants.appId));
        Constants.GETTOKEN  = Constants.GETTOKEN.replace("APPSECRET",urlEnodeUTF8(Constants.appsecret));
        String severity = null;
        Object message = null;
        try {
          return  getReqMethod(Constants.GETTOKEN);

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

