package controllers.supervisor.activity.service;

import business.RedPacketBill;
import models.RedPacketBillModel;

import java.math.BigDecimal;

/**
 * Created by Yuan on 2015/6/16.
 */
public class RedPacketBillService {

    public RedPacketBillModel save(RedPacketBill redPacketBill) {
        try {
            RedPacketBillModel redPacketBillModel = new RedPacketBillModel();
            redPacketBillModel.setAddTime(redPacketBill.getAddTime());
            redPacketBillModel.setAmount(new BigDecimal(redPacketBill.getAmount()).divide(new BigDecimal(100)));
            redPacketBillModel.setBillNo(redPacketBill.getBillNo());
            redPacketBillModel.setOpenId(redPacketBill.getOpenid());
            redPacketBillModel.setRedPacketId(Long.valueOf(redPacketBill.getRedPackId()));
            redPacketBillModel.setRemark(redPacketBill.getRemark());
            //        redPacketBillModel.setReturnMessage(redPacketBill.getReturnMsg());
            redPacketBillModel.setReturnCode(redPacketBill.getResult());
            redPacketBillModel.save();
            return redPacketBillModel;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
