package test.jupiter;

import com.weston.stpapi.*;
import com.weston.jupiter.generated.*;
import com.weston.jupiter.common.*;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.concurrent.*;

public class OrderInputExample
{
    public OrderInputExample(){
    }

    public static void main(String[] args){
        String localeName = "Tiger"; //args[0];
        String configDirectory = "/home/taot/Downloads/jupiter/stp-api/etc/"; //args[1];

//        MyExecutionListener execListener = new MyExecutionListener();
        STPClient client = new STPClient();
        if(client.init("xingen.song", "passwd", localeName, configDirectory)){
            //not implemented
            //client.addExecutionListener(execListener);

            OrderBasket basket = new OrderBasket();
            basket.stp = WireBoolean.TRUE;

            OrderPM order              = new OrderPM();
            //staging environment
            order.externalClientID     = "dyStgClient01";    // tenent client id
            order.externalAccountID    = "dyStgAcctEQ";      // pms account id
            order.externalSubaccountID = "dyStgSubacctEQ1";  // no subaccount right now
            //dev environment
            order.externalClientID     = "dyClient01";
            order.externalAccountID    = "dyAcct1";
            order.externalSubaccountID = "dySubacct-01";

            order.emsSecurityID        = 21L;//4=XSHE.000002
            order.side                 = Side.Buy;
            order.amountOpen           = 1900;
            order.priceLimit           = 0.0;  // because using VWAP, use 0.0
            order.priceGuideline       = 0.0;  // because using VWAP, use 0.0
            // Straight through process
            if(basket.stp == WireBoolean.TRUE) {  // 自动合规, 自动拆单, etc
                order.stpAlgorithm      = TradeType.VWAP_Basic;
                order.stpStartTime      = 100000; //93000;
                order.stpEndTime        = 102000;
                order.stpBrokerCapacity = BrokerCapacity.Agency;
            }

            if(client.generateExternalOrderID(order, 1) != null){
                basket.orders.add (order);
            }

            client.sendNewOrderBasket(basket);
        }
        client.fini();
    }

}