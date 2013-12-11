package com.datayes.invest.pms.dao.account.impl;

import com.datayes.invest.pms.dao.account.PositionDao;
import com.datayes.invest.pms.dao.account.PositionIdGenerator;
import com.datayes.invest.pms.entity.account.Position;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import javax.inject.Inject;
import javax.persistence.Query;
import java.util.List;

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

    @Override
    public List<Position> findByAccountIdBeforeAsOfDate(Long accountId, LocalDate asOfDate) {
        Query q = getEntityManager().createQuery(
                "from Position where accountId = :accountId and openDate < :asOfDate");
        q.setParameter("accountId", accountId);
        q.setParameter("asOfDate", new LocalDateTime(asOfDate.plusDays(1).toDateTimeAtStartOfDay()));

        List<Position> list = (List<Position>) q.getResultList();
        return list;
    }
}
