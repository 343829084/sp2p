package business;

import java.util.Date;

/**
 * Created by libaozhong on 2015/6/11.
 */
public class RedPacket {
    private Long hongBaoId;    // 红包表主键
    private String billNo;     // 红包订单号
    private String openid;     // 领取红包的用户ID
    private Integer amount;    // 领取红包金额 单位分
    private Date addTime;      // 添加时间
    private Integer result;    // 领取红包结果 0失败 1成功 2锁定
    private String remark;     // 备注  用于保存微信返回的json
}
