package com.datayes.invest.pms.dao.account.impl;

import java.util.List;

import javax.persistence.Query;

import com.datayes.invest.pms.dao.account.PositionDao;
import com.datayes.invest.pms.entity.account.Position;

public class PositionDaoImpl extends AccountRelatedDaoImpl<Position, Long> implements PositionDao {

    protected PositionDaoImpl() {
        super(Position.class);
    }

    public long findLargestPositionId() {
        Query q = getEntityManager().createQuery("select max(id) from Position");
        List list = q.getResultList();
        if (list == null || list.isEmpty() || list.get(0) == null) {
            return 0;
        }
        return (long) list.get(0);
    }
    
	@Override
	public void save(Position entity) {		
	    Long positionId = entity.getId();

        if (positionId == null) {
            // TODO fix this
            entity.setId(1L);
        }
        
        if (entity.getId().equals(Long.valueOf(96L))) {
            System.out.println("stop");
        }

		super.save(entity);
	}
}
