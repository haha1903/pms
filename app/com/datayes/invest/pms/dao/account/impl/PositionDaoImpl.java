package com.datayes.invest.pms.dao.account.impl;

import com.datayes.invest.pms.dao.account.PositionDao;
import com.datayes.invest.pms.dao.account.PositionIdGenerator;
import com.datayes.invest.pms.entity.account.Position;

import javax.inject.Inject;

public class PositionDaoImpl extends AccountRelatedDaoImpl<Position, Long> implements PositionDao {
    
    @Inject
    private PositionIdGenerator idGenerator;

    protected PositionDaoImpl() {
        super(Position.class);
    }
    
	@Override
	public void save(Position entity) {		
	    Long positionId = entity.getId();

        if (positionId == null) {
            Long id = idGenerator.getNextId();
            entity.setId(id);
        }
        
		super.save(entity);
	}
}
