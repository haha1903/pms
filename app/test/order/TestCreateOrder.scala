package test.order

import com.datayes.invest.pms.system.SystemInjectors
import com.datayes.invest.pms.service.order.{Order, OrderBasket, OrderService }
import com.datayes.invest.pms.dbtype.TradeSide
import com.datayes.invest.pms.logic.order.OrderManager
import org.joda.time.LocalTime

object TestCreateOrder {

  def main(args: Array[String]): Unit = {
    val injector = SystemInjectors.INSTANCE.getInjector()

    val orderService = injector.getInstance(classOf[OrderManager])
    val basket = createBasket()
    orderService.createOrders(basket)
  }

  private def createBasket(): OrderBasket = {
    val basket = new OrderBasket()

    val order = new Order()
    order.setAccountId(2L)
    order.setSecurityId(2L)
    order.setAmount(1000)
    order.setTradeSide(TradeSide.BUY)
    order.setStpFlag(true)
    order.setStpAlgorithm("hello")
    order.setStpStartTime(LocalTime.now())
    order.setStpEndTime(LocalTime.now())
    order.setPriceLimit(java.math.BigDecimal.valueOf(0.1));


    basket.getOrders().add(order);

    basket
  }
}
