package controllers.mobile;

import business.*;
import constants.Constants;
import constants.SQLTempletes;
import controllers.BaseController;
import controllers.SubmitRepeat;
import controllers.interceptor.H5Interceptor;
import models.*;
import play.db.jpa.JPA;
import play.mvc.With;
import utils.ErrorInfo;
import utils.PageBean;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by libaozhong on 2015/5/14.
 */
@With({H5Interceptor.class, SubmitRepeat.class})
public class TradeController extends BaseController {
    public static void tradeHistory(){
        User user =User.currUser();
        Invest invest=new Invest();
        ErrorInfo error = new ErrorInfo();
        PageBean<v_invest_records> invetResult = invest.queryUserInvestRecords(user.getId(), "1", "100", null, null, error);
        if(error.code < 0) {
            render(Constants.ERROR_PAGE_PATH_FRONT);
        }
        List<v_invest_records> vInvestRecords = invetResult.page;
        for(v_invest_records v:vInvestRecords){
            v.getStrStatus();
        }
        List<t_user_recharge_details> userRechargeDetails = User.queryRechargeRecordByUserId(user.getId());
        java.util.Date date=new  java.util.Date();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
         Calendar ca=Calendar.getInstance();
        ca.setTime(date);
        ca.set(Calendar.YEAR,ca.get(Calendar.YEAR)-1);
       String now= sdf.format(date);
        String begin=sdf.format(ca.getTime());
        PageBean<v_user_withdrawals> withdrawalRecord = User.queryWithdrawalRecord(user.getId(), "0", begin, now, null, null, error);
        List<v_user_withdrawals> withdrawals = withdrawalRecord.page;
        render(vInvestRecords, userRechargeDetails,withdrawals);
    }

/*****************跳转到待收金额****************************/
    public static void remainMoney(){
        User user =  User.currUser();
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

        List<t_content_news> news = News.queryNewForFront(Constants.NewsTypeId.MONEY_TIPS, 3, error);
         double totalRemain=0;

        boolean isIps = Constants.IPS_ENABLE;
        List<v_bill_invest> resultBills= Collections.emptyList();
        resultBills= getResultBills(payType,userId,3);
       for(int n = 0;n < resultBills.size();n++){
           totalRemain+=resultBills.get(n).income_amounts;
           int unit=resultBills.get(0).period_unit;
           unit= unit==-1?unit*=-365:unit==0?unit*=30:unit;
           resultBills.get(0).invest_period=unit;
        }
        render(user, accountStatistics, accountInfo, userDetails, userBanks, backstageSet, content, resultBills, isIps,totalRemain);
    }
    /**
     * 跳转到财富页面冻结金额
     */
    public static void tradeList() {

        User user = User.currUser();
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

        List<t_content_news> news = News.queryNewForFront(Constants.NewsTypeId.MONEY_TIPS, 3, error);

        boolean isIps = Constants.IPS_ENABLE;
        List<v_bill_invest> resultBills= Collections.emptyList();
            resultBills= getResultBills(payType,userId,2);
          double InvestAmount=getInvestAmount(resultBills,error);
            render(user, accountStatistics, accountInfo, userDetails, userBanks, backstageSet, content, resultBills, isIps,InvestAmount);
    }

    private static double getInvestAmount(List<v_bill_invest> resultBills,ErrorInfo error) {
        double total=0;
        for(v_bill_invest vbi:resultBills){
            Invest invest=new Invest();
            PageBean<v_invest_records> investResult = invest.queryBidInvestRecords(1, 30, vbi.bid_id, error);
            List<v_invest_records> resut = investResult.page;
            for(v_invest_records v:resut){
                total+=v.invest_amount;
            }
        }
        return total;
    }

    public static  List<v_bill_invest> getResultBills(int payType,long userId,int bidStatus ) {
        List<v_bill_invest> resultBills= new ArrayList<v_bill_invest>();
        Map<String,Object> conditionMap = new HashMap<String, Object>();
        List<Object> params = new ArrayList<Object>();
        List<v_bill_invest> bills = new ArrayList<v_bill_invest>();
        StringBuffer sql = new StringBuffer("");

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
            Bid bid=new Bid();
            bid.setId(bills.get(i).bid_id);

            int status =bid.status;
            if(status==bidStatus ){
                bills.get(i).apr=bid.apr;
                bills.get(i).bidStatus=status;
                bills.get(i).bidTime= bid.time;
                bills.get(i).invest_period=bid.investPeriod;
                bills.get(i).period_unit=bid.periodUnit;
                resultBills.add(bills.get(i));
            }

        }
        return resultBills;
    }
}
