package com.qkwl.common.framework.mq;

import com.alibaba.fastjson.JSON;
import com.aliyun.openservices.ons.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentLinkedQueue;

import javax.annotation.PostConstruct;

public class MQSendHelper {

	/**
	 * 日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(MQSendHelper.class);

	/**
	 * 委单队列
	 */
	private ConcurrentLinkedQueue<Message> mqQueue = new ConcurrentLinkedQueue<Message>();
	
	/**
	 * 发送
	 */
	private Producer producer;

	public void setProducer(Producer producer) {
		this.producer = producer;
	}

	@PostConstruct
	public void init() {
		Thread thread = new Thread(new Work());
		thread.setName("MQSendHelper");
		thread.start();
	}
	
	public void offer(String topic, String tags, String key, Object object) {
		if(!mqQueue.offer(new Message(topic, tags, key, JSON.toJSONString(object).getBytes()))) {
			logger.error("queue offer failed : " + topic);
		}
	}

	public boolean send(String topic, String tags, String key, Object object) {
		Message message = new Message(topic, tags, key, JSON.toJSONString(object).getBytes());
		try {
			producer.send(message);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	class Work implements Runnable {
		public void run() {
			while (true) {
				if (mqQueue.isEmpty()) {
					continue;
				}
				final Message msg = mqQueue.poll();
				if (msg == null) {
					continue;
				}
				producer.sendAsync(msg, new SendCallback() {
        			@Override
        			public void onSuccess(SendResult sendResult) {
        			}
        			@Override
        			public void onException(OnExceptionContext context) {
        				if (!mqQueue.offer(msg)){
            				logger.error("queue onException failed : {}_{}", msg.getMsgID(), msg.getBody());
        				}
        			}
        		});
			}
		}
	}
}
