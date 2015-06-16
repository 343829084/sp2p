package controllers.supervisor.activity.service;

import business.PageVo;
import controllers.supervisor.activity.vo.RedPacketVo;
import models.RedPacketModel;
import play.db.jpa.JPA;

import javax.persistence.Query;
import java.util.List;

/**
 * Created by Yuan on 2015/6/16.
 */
public class RedPacketService {

    public void save(RedPacketVo redPacketVo) {
        RedPacketModel redPacketModel = redPacketVo.convertToRedPacket();
        redPacketModel.save();

    }

    public List<RedPacketVo> findRedPacketVos(PageVo pageVo) {
        int index = pageVo.getIndex();
        int pageSize = pageVo.getPageSize();
        StringBuffer jpql = new StringBuffer();
        jpql.append(" select new ");
        jpql.append(RedPacketVo.class.getName() + "(r)");
        jpql.append(" from RedPacketModel r");
        jpql.append(" where 1=1");

        Query query = JPA.em().createQuery(jpql.toString());
        query.setFirstResult(index);
        if (pageSize > 0) {
            query.setMaxResults(pageSize);
        }

        List<RedPacketVo> resultList = query.getResultList();

        return resultList;
    }

    public RedPacketVo findRedPacketVoById(Long id) {
        if (id == null) return null;
        StringBuffer jpql = new StringBuffer();
        jpql.append(" select new ");
        jpql.append(RedPacketVo.class.getName() + "(r)");
        jpql.append(" from RedPacketModel r");
        jpql.append(" where 1=1");
        jpql.append(" and r.id = ").append(id);
        List<RedPacketVo> list = JPA.em().createQuery(jpql.toString()).getResultList();
        return list.isEmpty() ? null : list.get(0);
    }


}
