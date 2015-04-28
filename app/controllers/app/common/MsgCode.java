package controllers.app.common;

/***
 * XXX_XXXX
 *
 * _前为业务类型
 * _后4位为数字：第一位：0(info) 1(warn) 2(error业务逻辑) 3(fatal系统异常)
 *             第二位：业务代码
 *                    0 公用部分
 *                    1 注册登录
 *                    2 账户中心
 *
 *                    4 交易
 *                    5 p2p
 */
public enum MsgCode {

    //operation platform
	CREATE_SUCCESS("0001", "创建成功", ""),
    UPDATE_SUCCESS("0002", "更新成功", ""),
    DELETE_SUCCESS("0003", "删除成功", ""),
    OPERATE_WARNING("1000", "警告", ""),
    
    //p2p app模块
    LOAN_BID_QUERY_SUCC("0501","查询借款标列表成功"),
    LOAN_BID_DETAIL_QUERY_SUCC("0502","查询借款标详情成功"),
    LOAN_BID_PROJECT_DETAIL_QUERY_SUCC("0503","查询项目详情成功"),
    LOAN_BID_FUND_SECURITY_QUERY_SUCC("0504","查询资金安全详情成功"),
    LOAN_BID_INVEST_RECORDS_SUCC("0505","查询投标记录详情成功"),
    LOAN_BID_SHOW_QUERY_SUCC("0506","查询首页展示标的成功"),
    LOAN_BID_APR_CALCULATOR_SUCC("0507","查询利率计算器成功"),
    
    
    LOAN_BID_QUERY_FAIL("2501","查询借款标列表失败"),
    LOAN_BID_DETAIL_QUERY_ID_FAIL("2502","借款id有误"),
    LOAN_BID_DETAIL_QUERY_USERID_FAIL("2503","解析用户id有误"),
    LOAN_BID_DETAIL_QUERY_ERROR("2504","查询出现异常，给您带来的不便敬请谅解"),
    LOAN_BID_SHOW_QUERY_FAIL("2505","查询首页展示标的失败"),
    LOAN_BID_APR_CALCULATOR_FAIL("2506","查询利率计算器失败"),
    
    

	;





    private String code;
    private String message;
    private String detail;

    private MsgCode(String code, String message){
        this(code, message,"");
    }

    private MsgCode(String code, String message, String detail){
        this.code = code;
        this.message = message;
        this.detail = detail;
    }

    public String getCode(){
        return this.code;
    }

    public String getMessage(){
        return this.message;
    }

    public String getDetail(){
        return this.detail;
    }

    public static String getDescByCode(String code) {
        if(code == null) {
            return null;
        }
        for(MsgCode msgCode : MsgCode.values()) {
            if(code.equals(msgCode.getCode())) {
                return msgCode.getMessage();
            }
        }
        return null;
    }
}
