package com.obase.mail;

public class Envelope {

	public String host; // smtp host
	public Integer port; // smtp port
	public Boolean auth; // ssl auth
	public String user; // user name, may be same as from
	public String pass; // password
	public String from;
	public String personal;
	public String[] replyTo;

}
