package com.datayes.invest.pms.system

import com.datayes.invest.pms.config.Config
import com.datayes.invest.pms.logging.Logging
import com.weston.jupiter.common.MarsPackage
import com.weston.jupiter.generated.{Execution, TVP}
import javax.inject.Inject
import javax.jms.{Message, MessageListener, Session, TextMessage}
import org.apache.activemq.{ActiveMQConnection, ActiveMQConnectionFactory}

class TransactionThread extends Runnable with Logging {
  
  @Inject
  private var transactionProcess: TransactionProcess = null

  var session: Session = null
  
  def closeSession(): Unit = {
    if (session != null) {
      try {
        session.close
      } catch {
        case e: Throwable => throw new RuntimeException("close activemq sesson error!")
      }
    }
  }
  
  def createSession(): Session = {
    val jmsHost = Config.INSTANCE.getString("oms.host")
    val jmsPort = Config.INSTANCE.getInt("oms.port")
    val jmsUrl = "tcp://" + jmsHost + ":" + jmsPort

    val username = ActiveMQConnection.DEFAULT_USER
    val password = ActiveMQConnection.DEFAULT_PASSWORD
    val factory = new ActiveMQConnectionFactory(username, password, jmsUrl)
    val conn = factory.createConnection
    conn.start()
    session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE)
    session
  }

  def run(): Unit = {
    val recvTopicName = Config.INSTANCE.getString("oms.execution.topic")
    val session = createSession;
    val topic = session.createTopic(recvTopicName);
    val consumer = session.createConsumer(topic)
    
    logger.info("Transaction thread started")
    
    consumer.setMessageListener(new MessageListener() {
      def onMessage(msg: Message): Unit = {
        try {
          val text = msg.asInstanceOf[TextMessage].getText
          val pkg = new MarsPackage
          val result = pkg.fromTagValueMessage(text)
          var marsMsg = pkg.pop
          while (marsMsg != null) {
            val instance = marsMsg.getClassInstanceTag()
            if (TVP.Execution.equals(instance)) {
              val execution = new Execution(marsMsg.getBody)
              logger.info("Execution recevied: {}", execution)
              transactionProcess.process(execution)
            }
            marsMsg = pkg.pop
          }
        } catch {
          case e: Throwable => logger.error(e.toString, e)
        }
      }
    });
  }
}