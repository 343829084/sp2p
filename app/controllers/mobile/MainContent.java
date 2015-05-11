package controllers.mobile;

import constants.Constants;
import constants.SQLTempletes;
import controllers.BaseController;
import models.y_front_show_bids;
import net.sf.json.JSONObject;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import play.Logger;
import play.db.jpa.JPA;
import utils.ErrorInfo;

import javax.persistence.Query;
import java.io.IOException;
import java.util.*;

/**
 * Created by libaozhong on 2015/5/5.
 */
public class MainContent extends BaseController {


    public static Map<String,Object> toJson(String jsonString){
        HashMap<String,Object> map=new HashMap<String,Object>();
        if(jsonString.isEmpty()){
            return map;
        }

        char pre='[';
        char preNext='{';
        char last=']';
        char lastPre='}';
        int start_index=0;
        int end_index=0;
         for(int i=0;i<jsonString.length()-1;i++){
             if(jsonString.charAt(i)==pre && jsonString.charAt(i+1)==preNext){
                 start_index=i+1;
             }
             if(jsonString.charAt(i)==lastPre && jsonString.charAt(i+1)==last){
                 end_index=i+1;
             }

         }
        String array=  jsonString.substring(start_index, end_index);
        String[] resultArray = array.split("\\{");
        List<String> splitArray= new ArrayList<String>();
        for(int i=0;i<resultArray.length;i++){
            if(resultArray[i].indexOf("\\}")!=-1){
                splitArray.add(resultArray[i].substring(0,resultArray[i].indexOf("\\}")-1));
            }
        }
      Iterator<String>  iterator=splitArray.iterator();
        while(iterator.hasNext()){
        String[]  jsonArray=  iterator.next().split(",");
            for(int i=0;i<jsonArray.length;i++){

            }
        }
        return  map;
    };

    public static Object getHttpResult(ErrorInfo error){
        String severity = null;
        Object message=null;
        try {
            HttpClient httpClient = new HttpClient();
            PostMethod postMethod = new PostMethod(Constants.FP_ACTIVITY_IMAG_URL);
            postMethod.addRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=gbk");// 在头文件中设置转码

            NameValuePair[] data = {
                    new NameValuePair("index", "0"),
                    new NameValuePair("pageSize", "10"),
                    new NameValuePair("filter", "0")};
            postMethod.setRequestBody(data);
            int statusCode = httpClient.executeMethod(postMethod);

            String result = postMethod.getResponseBodyAsString();
            JSONObject jsonResult = JSONObject.fromObject(result);
             message = jsonResult.get("message");
            Object value = jsonResult.get("value");
            if(message!= null && message instanceof JSONObject){
                severity = ((JSONObject)message).getString("severity");
                if(!severity.equals("000")){
                    error.code = -1;
                    error.msg = ((JSONObject)message).getString("summary");
                    return value;
                }

            }
            Logger.info("statusCode:" + statusCode + ", result:" + result + "");
            postMethod.releaseConnection();
        } catch (HttpException e) {
            e.printStackTrace();
            Logger.info("修改密码时时,更新保存用户密码时："+e.getMessage());
            error.code = -3;
            error.msg = "对不起，由于FP平台出现故障，此次密码修改保存失败！";

            return null;
        } catch (IOException e) {
            e.printStackTrace();
            Logger.info("修改密码时时,更新保存用户密码时："+e.getMessage());
            error.code = -3;
            error.msg = "对不起，由于FP平台出现故障，此次密码修改保存失败！";

            return null;
        }
        return null;
    }
    /*
       * 跳转金品页面
       */
    public static void bestProduct() {
        ErrorInfo error = new ErrorInfo();
        List<y_front_show_bids> bidList = new ArrayList<y_front_show_bids>();
        StringBuffer sql = new StringBuffer("");
        sql.append(SQLTempletes.SELECT);
        sql.append(SQLTempletes.V_FRONT_HOMEPAGE_SHOW_BID);
        try{
            Query query = JPA.em().createNativeQuery(sql.toString(),y_front_show_bids.class);
            bidList = query.getResultList();
        }catch (Exception e) {
            e.printStackTrace();
            error.msg = "系统异常，给您带来的不便敬请谅解！";
            error.code = -1;
        }
        y_front_show_bids bid= bidList.get(0);

     Object message=   getHttpResult(error);
        render(bid,message);
    }

    /**
     * 跳转到财富页面
     */
    public static void property() {
        String loginOrRegister = Constants.LOGIN_AREAL_FLAG;
        render();
    }
    /**
     * 跳转到财富页面
     */
    public static void moneyMatters() {
        String loginOrRegister = Constants.LOGIN_AREAL_FLAG;
        render();
    }
    /**
     * 跳转到me页面
     */
    public static void me() {
        String loginOrRegister = Constants.LOGIN_AREAL_FLAG;
        render();
    }
}
