package com.microcardio.chat.po;

import java.util.Date;

/**
 * 该类用来封装socket发送出去的消息
 * @author AMOBBS
 *
 */
public class Message{
	private int cmd;  //指令
	private String content;  //内容
	private User sender;     //发送者
	private User received;   //接收者
	private Date sendDate;   //发送时间
	public float time;//时间长度
	public Message() {
		
	}
	
	
	public Message(int cmd, String content, User sender, User received, Date sendDate) {
		super();
		this.cmd = cmd;
		this.content = content;
		this.sender = sender;
		this.received = received;
		this.sendDate = sendDate;
	}


	public int getCmd() {
		return cmd;
	}
	public void setCmd(int cmd) {
		this.cmd = cmd;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public User getSender() {
		return sender;
	}
	public void setSender(User sender) {
		this.sender = sender;
	}
	public User getReceived() {
		return received;
	}
	public void setReceived(User received) {
		this.received = received;
	}
	public Date getSendDate() {
		return sendDate;
	}
	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}
	@Override
	public String toString() {
		return "Message [cmd=" + cmd + ", content=" + content + ", sender=" + sender + ", received=" + received
				+ ", sendDate=" + sendDate + "]";
	}
	
	
	
}
