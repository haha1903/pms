package com.datayes.invest.pms.dao.account.impl;

import javax.inject.Inject;

import com.datayes.invest.pms.dao.account.IdGenerator;
import com.datayes.invest.pms.dao.account.PositionDao;
import com.datayes.invest.pms.entity.account.Position;

public class PositionDaoImpl extends AccountRelatedDaoImpl<Position, Long> implements PositionDao {
    
    @Inject
    private IdGenerator idGenerator;

    protected PositionDaoImpl() {
        super(Position.class);
    }
    
	@Override
	public void save(Position entity) {		
	    Long positionId = entity.getId();

        if (positionId == null) {
            Long id = idGenerator.getNextPositionId();
            entity.setId(id);
        }
        
		super.save(entity);
	}
}
