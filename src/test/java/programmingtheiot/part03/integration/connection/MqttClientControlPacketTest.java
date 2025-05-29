package programmingtheiot.part03.integration.connection;

 import static org.junit.Assert.*;
 
 import java.util.logging.Logger;
 
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 
 import programmingtheiot.common.ConfigConst;
 import programmingtheiot.common.ConfigUtil;
 import programmingtheiot.common.IDataMessageListener;
 import programmingtheiot.common.ResourceNameEnum;
 import programmingtheiot.data.*;
 import programmingtheiot.gda.connection.*;
 

 public class MqttClientControlPacketTest
 {
	 // static
	 
	 private static final Logger _Logger =
		 Logger.getLogger(MqttClientControlPacketTest.class.getName());
	 
	 
	 // member var's
	 
	 private MqttClientConnector mqttClient = null;
	 
	 
	 // test setup methods
	 
	 @Before
	 public void setUp() throws Exception
	 {
		 this.mqttClient = new MqttClientConnector();
	 }
	 
	 @After
	 public void tearDown() throws Exception
	 {
	 }
	 
	 // test methods
	 
	 @Test
	 public void testConnectAndDisconnect()
	 {
		 assertTrue(this.mqttClient.connectClient());
 		 assertTrue(this.mqttClient.disconnectClient());
	 }
	 
	 @Test
	 public void testServerPing()
	 {
		 assertTrue(this.mqttClient.connectClient());
 
		 try {
			 Thread.sleep(5000);
		 } catch (InterruptedException e) {
			 _Logger.warning("Interrupted while waiting for PINGREQ and PINGRESP packets.");
		 }
		 assertTrue(this.mqttClient.disconnectClient());
	 }
	 
	 @Test
	 public void testServerPingAlternative() {
		 boolean connected = this.mqttClient.connectClient();
		 assertTrue("MQTT client should connect", connected);
		 long start = System.currentTimeMillis();
		 long waitTime = 5000L;
		 while (System.currentTimeMillis() - start < waitTime) {
			 try {
				 Thread.sleep(1000);
			 } catch (InterruptedException e) {
				 _Logger.warning("Sleep interrupted during ping wait loop.");
			 }
		 }
		 boolean disconnected = this.mqttClient.disconnectClient();
		 assertTrue("MQTT client should disconnect", disconnected);
	 }
	 
	 @Test
	 public void testPubSub()
	 {
		 assertTrue(this.mqttClient.connectClient());
 		 assertTrue(this.mqttClient.subscribeToTopic(ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE, 1));
 		 assertTrue(this.mqttClient.publishMessage(ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE, "Test message QoS 1", 1));
 		 assertTrue(this.mqttClient.publishMessage(ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE, "Test message QoS 2", 2));
 		 assertTrue(this.mqttClient.unsubscribeFromTopic(ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE));
 		 assertTrue(this.mqttClient.disconnectClient());
	 }
	 
	 @Test
	 public void testPubSubAlternative() {
		 assertTrue("Should connect to MQTT broker", this.mqttClient.connectClient());
		 boolean subscribed = this.mqttClient.subscribeToTopic(ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE, 1);
		 assertTrue("Should subscribe to topic", subscribed);
		 boolean pub1 = this.mqttClient.publishMessage(ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE, "Alt test message QoS 1", 1);
		 boolean pub2 = this.mqttClient.publishMessage(ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE, "Alt test message QoS 2", 2);
		 assertTrue("Should publish message with QoS 1", pub1);
		 assertTrue("Should publish message with QoS 2", pub2);
		 boolean unsub = this.mqttClient.unsubscribeFromTopic(ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE);
		 assertTrue("Should unsubscribe from topic", unsub);
		 boolean disconnected = this.mqttClient.disconnectClient();
		 assertTrue("Should disconnect from MQTT broker", disconnected);
	 }
	 
 }