package demo;

import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.SocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class TestMQTTSubcribe {
	private static String broker = "ssl://x.x.x.x:8883";
	private static String topic = "topic-ssl";
	
	private static MqttClient client;
	private static String clientId = String.valueOf(Math.random());
	
	private static String CLIENT_KEY_STORE = "D:/fugaoyang/jks3/rr.jks";
    private static String CLIENT_KEY_STORE_PASSWORD = "password";
	
	private static Logger log = LogManager.getLogger(TestMQTTSubcribe.class); 
	
	public static void main(String[] args){
	try {
		// ��ʼ��mqtt���Ķ�
		client = new MqttClient(broker, clientId, new MemoryPersistence());
	} catch (MqttException e) {
		log.error("client init failed, error:{}", e.getMessage());
	}
	
	// ���ӷ�����
    connect();
    
	
 // ������Ϣ
    String[] topicArr = { topic };
    try {
		client.subscribe(topicArr);
	} catch (MqttException e) {
		log.error("subscribe failed, error:{}", e.getMessage());
	}
}

	private static void connect() {
		// MQTT����������
		MqttConnectOptions options = new MqttConnectOptions();
		// �����Ƿ����session,�����������Ϊfalse��ʾ�������ᱣ���ͻ��˵����Ӽ�¼������Ϊtrue��ʾÿ�����ӵ������������µ��������
		options.setCleanSession(false);
		// ���ó�ʱʱ�� ��λΪ��
		options.setConnectionTimeout(20);
		// ���ûỰ����ʱ�� ��λΪ�� ��������ÿ��5���ʱ����ͻ��˷��͸���Ϣ�жϿͻ����Ƿ����ߣ������������û�������Ļ���
		options.setKeepAliveInterval(10);
		options.setAutomaticReconnect(true);// �����Զ�����
		
		// ���ûص�
		try {
			options.setSocketFactory(getSocketFactory());
		} catch (Exception e) {
			log.error("get socket failed, error:{}", e.getMessage());
		}
		client.setCallback(new MqttCallbackExtended() {
			public void connectionLost(Throwable cause) {
				log.info("connection lost");
			}

			public void messageArrived(String topic, MqttMessage message) throws Exception {
				log.info("message arrived");
				System.out.println(topic+"--"+new String(message.getPayload()));
			}

			public void deliveryComplete(IMqttDeliveryToken token) {
				log.info("delivery complete");
			}

			public void connectComplete(boolean reconnect, String serverURI) {
				log.info("connect complete");
			}});
		
		try {
			client.connect(options);
		} catch (MqttSecurityException e) {
			log.error("publish failed, error:{}", e.getMessage());
		} catch (MqttException e) {
			log.error("publish failed, error:{}", e.getMessage());
		}
	}
	
	public static SocketFactory getSocketFactory() throws Exception {
        KeyStore ks = KeyStore.getInstance("jks");
        ks.load(new FileInputStream(CLIENT_KEY_STORE), CLIENT_KEY_STORE_PASSWORD.toCharArray());
        KeyManagerFactory kf = KeyManagerFactory.getInstance("SunX509");
        kf.init(ks, CLIENT_KEY_STORE_PASSWORD.toCharArray());
        TrustManagerFactory tf = TrustManagerFactory.getInstance("SunX509");
        tf.init(ks);
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(kf.getKeyManagers(), tf.getTrustManagers(), null);

        SocketFactory f = context.getSocketFactory();
        return f;
    }
}
