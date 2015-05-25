package controllers.mobile;

import business.User;
import constants.Constants;
import controllers.BaseController;
import net.sf.json.JSONObject;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;
import play.Logger;
import play.cache.Cache;
import utils.ErrorInfo;
import utils.RegexUtils;

import java.io.IOException;
import java.util.Date;

/**
 * <p>Project: com.shovesoft.sp2p</p>
 * <p>Title: LoginController.java</p>
 * <p>Description: </p>
 * <p>Copyright (c) 2014 Sunlights.cc</p>
 * <p>All Rights Reserved.</p>
 *
 * @author <a href="mailto:jiaming.wang@sunlights.cc">wangJiaMing</a>
 */

public class LoginAction extends BaseController {

    /*
     * 跳转到登录页面
     */
    public static void login() {
        render();
    }

    public static void delegateSuccess(){
        render();
    }
    public static void openAccount(){
        render();
    }

    public static void doLogin(){
        ErrorInfo error = new ErrorInfo();

        String name = params.get("name");
        String password = params.get("password");
        flash.put("name", name);
        flash.put("password", password);

        String url = request.headers.get("referer").value();

        if (StringUtils.isBlank(name)) {
            error.code = -1;
            error.msg = "请输入用户名";
            flash.error(error.msg);
            redirect(url);
        }
        if (StringUtils.isBlank(password)) {
            error.code = -1;
            error.msg = "请输入密码";
            flash.error(error.msg);
            redirect(url);
        }

        User user = new User();
        user.name = name;

        if (user.id < 0) {
            error.code = -1;
            error.msg = "该用户名不存在";
            flash.error(error.msg);
            redirect(url);
        }

        if (user.login(password,false, error) < 0) {
            flash.error(error.msg);
            redirect(url);
        }

        redirect("https://www.baidu.com");//TODO
    }


    /**
     * 跳转到注册页面
     */
    public static void register() {
        render();
    }

    public static void doRegister(){
//        checkAuthenticity();//TODO
        JSONObject json = new JSONObject();
        ErrorInfo error = new ErrorInfo();

        String mobile = params.get("name");//the user name is mobile
        String password = params.get("password");
        String recommendUserName = params.get("recommended");

        registerValidation(error);

        if (error.code < 0) {
            json.put("error",error);
            renderJSON(json);
        }

        String authentication_id = registerFp(error, mobile, password);

        if (error.code < 0) {
            json.put("error",error);
            renderJSON(json);
        }

        User user = new User();
        user.time = new Date();
        user.name = mobile;
        user.password = password;
        user.mobile = mobile;
        user.authentication_id = authentication_id;
        user.recommendUserName = recommendUserName;

        user.register(error);

        if (error.code < 0) {
            json.put("error",error);
            renderJSON(json);
        }

        registerGiveJinDou(error,mobile);

        json.put("error",error);
        renderJSON(json);
        //TODO userid session cookie?
    }

    private static void registerValidation(ErrorInfo error){
        String mobile = params.get("name");//the user name is mobile
        String password = params.get("password");
        String verifyCode = params.get("verifyCode");

        if (StringUtils.isBlank(mobile)) {
            error.code = -1;
            error.msg = "请填写手机号";
            return;
        }
        if (StringUtils.isBlank(password)) {
            error.code = -1;
            error.msg = "请输入密码";
            return;
        }
        if (StringUtils.isBlank(verifyCode)) {
            error.code = -1;
            error.msg = "请输入验证码";
            return;
        }
        if (!RegexUtils.isMobileNum(mobile)) {
            error.code = -1;
            error.msg = "请填写正确的手机号码";
            return;
        }
        if (!RegexUtils.isValidPassword(password)) {
            error.code = -1;
            error.msg = "请填写符合要求的密码";
            return;
        }

        String cacheVerifyCode = (String) Cache.get(mobile);//TODO
//        if (!verifyCode.equalsIgnoreCase(cacheVerifyCode)) {
//            error.code = -1;
//            error.msg = "验证码输入有误";
//            return;
//        }

        User.isNameExist(mobile, error);
    }

    private static String registerFp(ErrorInfo error, String mobile, String password) {
        String authentication_id = null;
        String severity = null;
        try {
            HttpClient httpClient = new HttpClient();
            PostMethod postMethod = new PostMethod(Constants.FP_REGISTER_URL);
            postMethod.addRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=gbk");// 在头文件中设置转码

            NameValuePair[] data = {
                    new NameValuePair("mobilePhoneNo", mobile),
                    new NameValuePair("passWord", password),
                    new NameValuePair("channel", "1")};
            postMethod.setRequestBody(data);
            int statusCode = httpClient.executeMethod(postMethod);

            String result = postMethod.getResponseBodyAsString();
            JSONObject jsonResult = JSONObject.fromObject(result);
            Object message = jsonResult.get("message");
            if(message!= null && message instanceof JSONObject){
                severity = ((JSONObject)message).getString("severity");
                if(!severity.equals("0")){
                    error.code = -1;
                    error.msg = ((JSONObject)message).getString("summary");
                }
            }
            Object value = jsonResult.get("value");
            if(value!= null && value instanceof JSONObject){
                authentication_id = ((JSONObject)value).getString("authenticationId");
            }
            Logger.info("statusCode:" + statusCode + ", result:" + result + "");
            postMethod.releaseConnection();

        } catch (HttpException e) {
            e.printStackTrace();
            error.code = -1;
            error.msg = "注册失败";
        } catch (IOException e) {
            e.printStackTrace();
            error.code = -1;
            error.msg = "注册失败";
        }
        return authentication_id;
    }


    /**
     * 注册送金豆
     */
    static void registerGiveJinDou(ErrorInfo error,String name){
        HttpClient httpClient = new HttpClient();
        PostMethod RegGiveJinDou = new PostMethod(Constants.FP_REGISTER_GIVE_JINDOU);
        RegGiveJinDou.addRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=gbk");// 在头文件中设置转码
        NameValuePair[] giveJinDoudata = {
                new NameValuePair("mobilePhoneNo", name)
        };
        RegGiveJinDou.setRequestBody(giveJinDoudata);
        String severity = null;
        try {
            int statusCode = httpClient.executeMethod(RegGiveJinDou);
            String result = RegGiveJinDou.getResponseBodyAsString();
            JSONObject jsonResult = JSONObject.fromObject(result);
            Object message = jsonResult.get("message");
            if(message!= null && message instanceof JSONObject){
                severity = ((JSONObject)message).getString("severity");
                if(!severity.equals("0")){
                    error.code = -1;
                    error.msg = ((JSONObject)message).getString("summary");
                }
            }
            Logger.info("注册送金豆接口:statusCode-----------:"+statusCode+", result----------:"+result);
        } catch (HttpException e) {
            e.printStackTrace();
            error.code = -1;
            error.msg = "注册成功,送金豆失败,请联系客服！";
        } catch (IOException e) {
            e.printStackTrace();
            error.code = -1;
            error.msg = "注册成功,送金豆失败,请联系客服！";
        } finally{
            RegGiveJinDou.releaseConnection();
        }
    }
}
