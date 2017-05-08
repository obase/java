package com.obase.mail;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

public class SmtpSender {

	static final String Charset = "UTF8";
	static final String Encoding = "B";
	static final String HtmlContentType = "text/html;charset=utf8";

	/**
	 * 获取发信Session.
	 * 
	 * @return
	 */
	public static Session getSession(final Envelope env) {
		Properties props = new Properties();
		props.setProperty("mail.transport.protocol", "smtp");
		if (env.host != null) {
			props.setProperty("mail.smtp.host", env.host);
		}
		if (env.port != null) {
			props.setProperty("mail.smtp.port", env.port.toString());
		}
		if (env.auth != null) {
			props.setProperty("mail.smtp.auth", env.auth.toString());
		}
		if (env.from != null) {
			props.setProperty("mail.smtp.from", env.from);
		}
		if (env.personal != null) {
			props.setProperty("mail.smtp.personal", env.personal);
		}
		if (env.replyTo != null) {
			props.setProperty("mail.smtp.reply-to", join(env.replyTo));
		}

		if (Boolean.TRUE.equals(env.auth)) {
			return Session.getInstance(props, new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(env.user, env.pass);
				}
			});
		}

		return Session.getInstance(props);
	}

	static void setReplyTo(Session session, MimeMessage mimeMsg) throws MessagingException {
		String[] replyTo = split(session.getProperty("mail.smtp.reply-to"));
		if (replyTo == null || replyTo.length == 0) {
			return;
		}

		Address[] addresses = new Address[replyTo.length];
		for (int i = 0; i < addresses.length; i++) {
			addresses[i] = new InternetAddress(replyTo[i]);
		}
		mimeMsg.setReplyTo(addresses);
	}

	static String join(String[] items) {
		if (items == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (String item : items) {
			if (item != null && item.trim().length() > 0) {
				sb.append(item).append(';');
			}
		}
		return sb.toString();
	}

	static String[] split(String items) {
		if (items == null || items.length() == 0) {
			return null;
		}
		return items.split(";");
	}

	/**
	 * 发送简单的Text邮件.
	 * 
	 * @param email
	 *            收件人
	 * @param subject
	 *            邮件主题
	 * @param content
	 *            邮件内容
	 * @throws UnsupportedEncodingException
	 * @throws MessagingException
	 */
	public static void sendSimpleEmail(Session session, String email, String subject, String content) throws UnsupportedEncodingException, MessagingException {
		sendSimpleEmail(session, Arrays.asList(email), null, subject, content);
	}

	/**
	 * 发送简单的Text邮件.
	 * 
	 * @param session
	 *            发送邮件的会话
	 * @param to
	 *            收件人列表
	 * @param cc
	 *            抄送人列表
	 * @param subject
	 *            邮件主题
	 * @param content
	 *            邮件内容
	 * @throws UnsupportedEncodingException
	 * @throws MessagingException
	 */
	public static void sendSimpleEmail(Session session, List<String> to, List<String> cc, String subject, String content) throws UnsupportedEncodingException, MessagingException {

		if (to == null || to.size() == 0) {
			return;
		}

		Transport port = null;
		try {

			port = session.getTransport();
			port.connect();
			MimeMessage mimeMsg = new MimeMessage(session);
			setReplyTo(session, mimeMsg);
			String from = session.getProperty("mail.smtp.from");
			String personal = session.getProperty("mail.smtp.personal");
			mimeMsg.setFrom(new InternetAddress(from, personal));
			mimeMsg.setSubject(MimeUtility.encodeText(subject, Charset, Encoding));
			mimeMsg.setSentDate(new java.util.Date());
			mimeMsg.setText(content, Charset);

			Set<Address> allAddr = new HashSet<Address>();

			Address[] toAddr = new Address[to.size()];
			for (int i = 0; i < toAddr.length; i++) {
				toAddr[i] = new InternetAddress(to.get(i));
			}
			mimeMsg.setRecipients(Message.RecipientType.TO, toAddr);
			Collections.addAll(allAddr, toAddr);

			Address[] ccAddr = null;
			if (cc != null && cc.size() > 0) {
				ccAddr = new Address[cc.size()];
				for (int i = 0; i < ccAddr.length; i++) {
					ccAddr[i] = new InternetAddress(cc.get(i));
				}
				mimeMsg.setRecipients(Message.RecipientType.CC, ccAddr);

				Collections.addAll(allAddr, ccAddr);
			}

			port.sendMessage(mimeMsg, allAddr.toArray(new Address[0]));

		} finally {
			if (port != null) {
				try {
					port.close();
				} catch (MessagingException e) {
					throw e;
				}
			}
		}

	}

	/**
	 * 发送html邮件
	 * 
	 * @param to
	 *            收件人列表
	 * @param cc
	 *            抄送人列表
	 * @param subject
	 *            邮件主题
	 * @param html
	 *            邮件内容
	 * @throws MessagingException
	 * @throws UnsupportedEncodingException
	 */
	public static void sendHtmlEmail(Session session, List<String> to, List<String> cc, String subject, String html) throws MessagingException, UnsupportedEncodingException {

		MimeMultipart multi = new MimeMultipart();
		MimeBodyPart body = new MimeBodyPart();
		body.setContent(html, HtmlContentType);
		multi.addBodyPart(body);

		sendMultiEmail(session, to, cc, subject, multi);

	}

	public static void sendHtmlEmail(Session session, String to, String subject, String html) throws MessagingException, UnsupportedEncodingException {

		MimeMultipart multi = new MimeMultipart();
		MimeBodyPart body = new MimeBodyPart();
		body.setContent(html, HtmlContentType);
		multi.addBodyPart(body);

		sendMultiEmail(session, Arrays.asList(to), null, subject, multi);

	}

	/**
	 * 发送带附件的Html邮件
	 * 
	 * @param recipient
	 *            收件人
	 * @param subject
	 *            邮件主题
	 * @param html
	 *            邮件html内容
	 * @param attachData
	 *            附件内容
	 * @param attachName
	 *            附件名称
	 * @throws UnsupportedEncodingException
	 * @throws MessagingException
	 */
	public static void sendHtmlEmailWithAttach(Session session, String recipient, String subject, String html, DataSource attachData, String attachName) throws UnsupportedEncodingException,
			MessagingException {
		sendHtmlEmailWithAttach(session, Arrays.asList(recipient), null, subject, html, attachData, attachName);
	}

	/**
	 * 发送带附件的html邮件
	 * 
	 * @param to
	 *            发件人列表
	 * @param cc
	 *            抄送人列表
	 * @param subject
	 *            邮件主题
	 * @param html
	 *            邮件html内容
	 * @param attachData
	 *            附件数据
	 * @param attachName
	 *            附件名称
	 * @throws MessagingException
	 * @throws UnsupportedEncodingException
	 */
	public static void sendHtmlEmailWithAttach(Session session, List<String> to, List<String> cc, String subject, String html, DataSource attachData, String attachName) throws MessagingException,
			UnsupportedEncodingException {

		MimeMultipart multi = new MimeMultipart();

		MimeBodyPart body = new MimeBodyPart();
		body.setContent(html, HtmlContentType);
		multi.addBodyPart(body);

		body = new MimeBodyPart();
		body.setDataHandler(new DataHandler(attachData));
		body.setFileName(MimeUtility.encodeText(attachName, Charset, Encoding));
		multi.addBodyPart(body);

		sendMultiEmail(session, to, cc, subject, multi);

	}

	/**
	 * 发送带图片的html邮件. 注意, 如果收件人列表与抄送人列表有交叉,某些邮件服务器将报告错误.
	 * 
	 * @param to
	 *            收件人列表
	 * @param cc
	 *            抄送人列表
	 * @param subject
	 *            邮件主题
	 * @param html
	 *            邮件html内容
	 * @param imgData
	 *            图片数据
	 * @param cid
	 *            图片引用id,在html中通过 <img src="cid:IMG1" ...>形式引用图片
	 * @throws MessagingException
	 * @throws UnsupportedEncodingException
	 */
	public static void sendHtmlEmailWithImage(Session session, String recipient, String subject, String html, DataSource imgData, String cid) throws UnsupportedEncodingException, MessagingException {
		MimeMultipart multi = new MimeMultipart("related");

		MimeBodyPart body = new MimeBodyPart();
		body.setContent(html, HtmlContentType);
		multi.addBodyPart(body);

		body = new MimeBodyPart();
		body.setDataHandler(new DataHandler(imgData));
		body.setContentID(cid);
		multi.addBodyPart(body);

		sendMultiEmail(session, Arrays.asList(recipient), null, subject, multi);
	}

	/**
	 * 发送带图片的html邮件. 注意, 如果收件人列表与抄送人列表有交叉,某些邮件服务器将报告错误.
	 * 
	 * @param to
	 *            收件人列表
	 * @param cc
	 *            抄送人列表
	 * @param subject
	 *            邮件主题
	 * @param html
	 *            邮件html内容
	 * @param imgData
	 *            图片数据
	 * @param cid
	 *            图片引用id,在html中通过 <img src="cid:IMG1" ...>形式引用图片
	 * @throws MessagingException
	 * @throws UnsupportedEncodingException
	 */
	public static void sendHtmlEmailWithImage(Session session, List<String> to, List<String> cc, String subject, String html, DataSource imgData, String cid) throws MessagingException,
			UnsupportedEncodingException {

		MimeMultipart multi = new MimeMultipart("related");

		MimeBodyPart body = new MimeBodyPart();
		body.setContent(html, HtmlContentType);
		multi.addBodyPart(body);

		body = new MimeBodyPart();
		body.setDataHandler(new DataHandler(imgData));
		body.setContentID(cid);
		multi.addBodyPart(body);

		sendMultiEmail(session, to, cc, subject, multi);

	}

	public static void sendMultiEmail(Session session, List<String> to, List<String> cc, String subject, Multipart content) throws UnsupportedEncodingException, MessagingException {

		if (to == null || to.size() == 0) {
			return;
		}

		Transport port = null;
		try {

			port = session.getTransport();
			port.connect();

			MimeMessage mimeMsg = new MimeMessage(session);
			setReplyTo(session, mimeMsg);
			mimeMsg.setSentDate(new Date());
			String from = session.getProperty("mail.smtp.from");
			String personal = session.getProperty("mail.smtp.personal");
			mimeMsg.setFrom(new InternetAddress(from, personal));
			mimeMsg.setSubject(MimeUtility.encodeText(subject, Charset, Encoding));
			mimeMsg.setSentDate(new Date());
			mimeMsg.setContent(content);
			mimeMsg.saveChanges();

			Set<Address> allAddr = new HashSet<Address>();

			Address[] toAddr = new Address[to.size()];
			for (int i = 0; i < toAddr.length; i++) {
				toAddr[i] = new InternetAddress(to.get(i));
			}
			mimeMsg.setRecipients(Message.RecipientType.TO, toAddr);
			Collections.addAll(allAddr, toAddr);

			Address[] ccAddr = null;
			if (cc != null && cc.size() > 0) {
				ccAddr = new Address[cc.size()];
				for (int i = 0; i < ccAddr.length; i++) {
					ccAddr[i] = new InternetAddress(cc.get(i));
				}
				mimeMsg.setRecipients(Message.RecipientType.CC, ccAddr);

				Collections.addAll(allAddr, ccAddr);
			}

			port.sendMessage(mimeMsg, allAddr.toArray(new Address[0]));

		} finally {
			try {
				port.close();
			} catch (MessagingException e) {
				throw e;
			}
		}
	}

}
