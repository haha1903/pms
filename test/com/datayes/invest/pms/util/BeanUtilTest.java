package com.datayes.invest.pms.util;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

public class BeanUtilTest {
    
    @Test
    public void testClone() {
        SecurityPosition position = new SecurityPosition(1L, 2L, 3, "GOOG");
        SecurityPosition clone = BeanUtil.clone(position);
        Assert.assertTrue("position != clone", position != clone);
        Assert.assertTrue("position equals clone", BeanUtil.equals(position, clone));
    }
    
    @Test
    public void testEquals() {
        SecurityPosition p1 = new SecurityPosition(1L, 2L, 3, "GOOG");
        SecurityPosition p2 = new SecurityPosition(1L, 2L, 3, "GOOG");
        SecurityPosition p3 = new SecurityPosition(1L, 2L, 3, "APPL");
        Assert.assertTrue("comparing p1 and p2", BeanUtil.equals(p1, p2));
        Assert.assertFalse("comparing p2 and p3", BeanUtil.equals(p1, p3));
    }

    @Test
    public void testHashCode() {
        SecurityPosition p1 = new SecurityPosition(1L, 2L, 3, "GOOG");
        SecurityPosition p2 = new SecurityPosition(1L, 2L, 3, "GOOG");
        Assert.assertEquals("hash code of p1 and p2 should equal", BeanUtil.hashCode(p1), BeanUtil.hashCode(p2));
    }
    
    @Test
    public void testToString() {
        SecurityPosition p1 = new SecurityPosition(1L, 2L, 3, "GOOG");
        Assert.assertEquals(
            "SecurityPosition [accountId = 2, ledgerId = 3, positionId = 1, security = GOOG]",
            BeanUtil.toString(p1));
    }
    
    @Test
    public void testCloneComposite() {
        PositionHist ph = new PositionHist(
            new PositionHist.PK(1L, "2013-10-17"), BigDecimal.ONE);
        PositionHist clone = BeanUtil.clone(ph);
        Assert.assertTrue("ph != clone", ph != clone);
        Assert.assertTrue("ph equals clone", BeanUtil.equals(ph, clone));
    }
    
    @Test
    public void testEqualsComposite() {
        PositionHist ph1 = new PositionHist(
            new PositionHist.PK(1L, "2013-10-17"), BigDecimal.ONE);
        
        PositionHist ph2 = new PositionHist(
            new PositionHist.PK(1L, "2013-10-17"), BigDecimal.ONE);
        
        PositionHist ph3 = new PositionHist(
            new PositionHist.PK(1L, "2013-10-18"), BigDecimal.ONE);
        
        Assert.assertTrue("comparing ph1 and ph2", BeanUtil.equals(ph1, ph2));
        Assert.assertFalse("comparing ph1 and ph3", BeanUtil.equals(ph1, ph3));
    }

    @Test
    public void testHashCodeComposite() {
        PositionHist ph1 = new PositionHist(new PositionHist.PK(1L, "2013-10-17"), BigDecimal.ONE);
        PositionHist ph2 = new PositionHist(new PositionHist.PK(1L, "2013-10-17"), BigDecimal.ONE);

        Assert.assertEquals("hash code ph1 and ph2 should equal", BeanUtil.hashCode(ph1), BeanUtil.hashCode(ph2));
    }
    
    @Test
    public void testToStringComposite() {
        PositionHist.PK pk = new PositionHist.PK(1L, "2013-10-17");
        PositionHist ph = new PositionHist(pk, BigDecimal.ONE);
        Assert.assertEquals(
            "PositionHist [pk = PK [asOfDate = 2013-10-17, positionId = 1], quantity = 1]",
            BeanUtil.toString(ph));
    }
}
