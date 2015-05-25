package controllers.mobile;


import business.*;
import constants.Constants;
import constants.SQLTempletes;
import controllers.BaseController;
import controllers.SubmitRepeat;
import controllers.interceptor.H5Interceptor;
import models.*;
import net.sf.json.JSONObject;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import play.Logger;
import play.db.jpa.JPA;
import play.mvc.With;
import utils.ErrorInfo;
import utils.PageBean;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.IOException;
import java.util.*;

/**
 * Created by libaozhong on 2015/5/5.
 */
@With({H5Interceptor.class, SubmitRepeat.class})
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
            httpClient.setTimeout(3000);
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
        User user = new User();  // User.currUser();
        user.setId(14);
        int payType=1;
      long userId=user.getId();
        ErrorInfo error = new ErrorInfo();
        v_user_account_statistics accountStatistics = User.queryAccountStatistics(userId, error);

        if(error.code < 0) {
            render(Constants.ERROR_PAGE_PATH_FRONT);
        }

        Optimization.UserOZ accountInfo = new Optimization.UserOZ(userId);

        if(error.code < 0) {
            render(Constants.ERROR_PAGE_PATH_FRONT);
        }

        List<v_user_details> userDetails = User.queryUserDetail(userId, error);

        if(error.code < 0) {
            render(Constants.ERROR_PAGE_PATH_FRONT);
        }

        List<UserBankAccounts> userBanks = UserBankAccounts.queryUserAllBankAccount(userId);
        BackstageSet backstageSet = BackstageSet.getCurrentBackstageSet();
        String content = News.queryContent(Constants.NewsTypeId.VIP_AGREEMENT, error);

        List<t_content_news> news = News.queryNewForFront(Constants.NewsTypeId.MONEY_TIPS, 3,error);

        boolean isIps = Constants.IPS_ENABLE;
        /*******查询客户投资未收款的标的详情***********************************************************/

        Map<String,Object> conditionMap = new HashMap<String, Object>();
        List<Object> params = new ArrayList<Object>();
        List<v_bill_invest> bills = new ArrayList<v_bill_invest>();
        StringBuffer sql = new StringBuffer("");
        StringBuffer sqlBill=new StringBuffer("");
        sql.append(SQLTempletes.SELECT);
        sql.append(SQLTempletes.V_BILL_INVEST);
        sql.append(SQLTempletes.LOAN_INVESTBILL_RECEIVE[payType]);
        sql.append("and c.id = ?");
        params.add(userId);
        sql.append(" group by receive_time");
        EntityManager em = JPA.em();
        Query query = em.createNativeQuery(sql.toString(),v_bill_invest.class);
        for(int n = 1; n <= params.size(); n++){
            query.setParameter(n, params.get(n-1));
        }
        bills = query.getResultList();
       for(int i=0;i< bills.size();i++){
           StringBuffer conditions = new StringBuffer(" where b.id = ?");
           sql = new StringBuffer();
           sql.append("select b.status from t_bids b");
           sql.append(conditions);
           params.clear();
           params.add(bills.get(i).bid_id);
           Query queryBill = em.createNativeQuery(sql.toString());

           for(int n = 1; n <= params.size(); n++){
               queryBill.setParameter(n, params.get(n-1));
           }
           
           int status = Integer.parseInt(queryBill.getResultList().get(0).toString());

           bills.get(i).bidStatus=status;
       }
        render(user, accountStatistics, accountInfo, userDetails, userBanks, backstageSet, content, bills, isIps);

    }
    /**
     * 跳转到理财页面
     */
    public static void moneyMatters() {

        ErrorInfo error = new ErrorInfo();
        int currPage = 1;

        if(params.get("currPage")!=null) {
            currPage = Integer.parseInt(params.get("currPage"));
        }

        PageBean<v_front_all_bids> pageBean = new PageBean<v_front_all_bids>();
        pageBean= Invest.queryAllBids(Constants.SHOW_TYPE_1, currPage, 100, null, null, null, null, null, null, null, null, "0", null,null, null, error);
        render(pageBean);

    }
    /**
     * 跳转到me页面
     */
    public static void me() {

        render();
    }
}
