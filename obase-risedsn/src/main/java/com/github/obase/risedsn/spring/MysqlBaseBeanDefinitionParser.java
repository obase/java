package com.github.obase.risedsn.spring;

import static com.github.obase.risedsn.spring.DsnKit.DEF_DRIVER_CLASS;
import static com.github.obase.risedsn.spring.DsnKit.DEF_JDBC_URL;
import static com.github.obase.risedsn.spring.DsnKit.getDwenvElement;
import static com.github.obase.risedsn.spring.DsnKit.getElementProperty;
import static com.github.obase.risedsn.spring.DsnKit.getMysqlProperty;
import static com.github.obase.risedsn.spring.DsnKit.getValue;
import static com.github.obase.risedsn.spring.DsnKit.isEmpty;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public abstract class MysqlBaseBeanDefinitionParser extends DsnBaseBeanDefinitionParser {

	protected final String jdbcUrlProp;
	protected final String driverClassProp;
	protected final String usernameProp;
	protected final String passwordProp;

	protected MysqlBaseBeanDefinitionParser(String driverClassProp, String jdbcUrlProp, String usernameProp, String passwordProp) {
		this.driverClassProp = driverClassProp;
		this.jdbcUrlProp = jdbcUrlProp;
		this.usernameProp = usernameProp;
		this.passwordProp = passwordProp;
	}

	@Override
	protected final void doParse(Element element, BeanDefinitionParserDelegate delegate, BeanDefinitionBuilder builder) {

		/******************************** 读取参数 ***************************************/
		String slaveStr = getElementProperty(element, "slave", false);
		boolean slave = isEmpty(slaveStr) ? false : Boolean.valueOf(slaveStr);

		Element dwenvElement = getDwenvElement(element, true);
		String dsn = getElementProperty(dwenvElement, "dsn", true);

		String key = getElementProperty(dwenvElement, "key", false);
		String[] props = getMysqlProperty(dsn, slave, key);
		String host = props[0];
		String port = props[1];
		String user = props[2];
		String pass = props[3];
		String db = props[4];
		/******************************** 注入属性 ***************************************/
		AbstractBeanDefinition beanDefinition = builder.getRawBeanDefinition();

		/* API属性 */
		Object driverClass = null;
		Object jdbcUrlObj = null;
		NodeList nodeList = dwenvElement.getElementsByTagNameNS("*", "property");
		for (int i = 0, n = nodeList.getLength(); i < n; i++) {
			Element propElem = (Element) nodeList.item(i);
			String name = propElem.getAttribute("name");
			if (jdbcUrlProp.equals(name)) {
				jdbcUrlObj = delegate.parsePropertyValue(propElem, beanDefinition, name);
			} else if (driverClassProp.equals(name)) {
				driverClass = delegate.parsePropertyValue(propElem, beanDefinition, name);
			} else {
				Object value = delegate.parsePropertyValue(propElem, beanDefinition, name);
				builder.addPropertyValue(name, value);
			}
		}

		if (driverClass == null) {
			driverClass = DEF_DRIVER_CLASS;
		}

		String jdbcUrl = jdbcUrlObj == null ? DEF_JDBC_URL : getValue(jdbcUrlObj).toString();

		jdbcUrl = jdbcUrl.replace("${host}", host);
		jdbcUrl = jdbcUrl.replace("${port}", port);
		jdbcUrl = jdbcUrl.replace("${default_db}", db);

		builder.addPropertyValue(driverClassProp, driverClass);
		builder.addPropertyValue(jdbcUrlProp, jdbcUrl);
		builder.addPropertyValue(usernameProp, user);
		builder.addPropertyValue(passwordProp, pass);

		builder.setDestroyMethodName("close");
	}

}
