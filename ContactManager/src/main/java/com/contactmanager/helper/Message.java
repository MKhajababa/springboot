package com.contactmanager.helper;

public class Message {
	private String type;
	public Message(String type, String msg) {
		super();
		this.type = type;
		this.msg = msg;
	}
	@Override
	public String toString() {
		return "Message [type=" + type + ", msg=" + msg + "]";
	}
	private String msg;
	public Message() {
		super();
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
}
