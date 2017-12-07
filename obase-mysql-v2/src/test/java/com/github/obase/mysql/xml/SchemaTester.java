package com.github.obase.mysql.xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.Arrays;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.springframework.core.io.FileSystemResource;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.github.obase.kit.MapKit;
import com.github.obase.mysql.core.JdbcMeta;
import com.github.obase.mysql.core.PstmtMeta;
import com.github.obase.mysql.stmt.Statement;

public class SchemaTester {

	public static void main(String[] args) throws Exception {
		// File xsdFile = new File("D:\\Workspace\\git\\obase.github.io\\schema\\obase-mysql-1.2.xsd");
		File xmlFile = new File("D:\\Workspace\\git\\java\\obase-mysql-v2\\src\\test\\resources\\HostsRepos.xml");
		// validateXMLWithXSD(xmlFile, xsdFile);

		ObaseMysqlParser parser = new ObaseMysqlParser();
		ObaseMysqlObject obj = parser.parse(new FileSystemResource(xmlFile));
		for (Class<?> c : obj.metaClassList) {
			System.out.println("META:" + c);
		}
		for (Class<?> c : obj.tableClassList) {
			System.out.println("META:" + c);
		}
		for (Statement s : obj.statementList) {
			System.out.println(s.getPsql());
			System.out.println(Arrays.toString(s.getParams()));

			PstmtMeta meta = null;
			long start = System.currentTimeMillis();
			for (int i = 0; i < 10000 * 100; i++) {
				meta = s.dynamicPstmtMeta(JdbcMeta.MAP, MapKit.as("module", Arrays.asList("测试模块"), "pattern", "模式"));
			}
			long end = System.currentTimeMillis();
			System.out.println("used:" + (end - start));
			System.out.println(meta);
		}
	}

	public static String validateXMLWithXSD(File xmlFile, File xsdFile) {
		XMLErrorHandler errHandler = null;
		try {
			Reader xmlReader = new BufferedReader(new FileReader(xmlFile));
			Reader xsdReader = new BufferedReader(new FileReader(xsdFile));
			Source xmlSource = new StreamSource(xmlReader);
			Source xsdSource = new StreamSource(xsdReader);
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = factory.newSchema(xsdSource);
			XMLStreamReader reader = XMLInputFactory.newFactory().createXMLStreamReader(xmlSource);
			Validator validator = schema.newValidator();
			errHandler = new XMLErrorHandler(reader);
			validator.setErrorHandler(errHandler);
			validator.validate(new StAXSource(reader));
			return errHandler.getErrorElement();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}

class XMLErrorHandler implements ErrorHandler {
	private String errorElement = null;
	private XMLStreamReader reader;

	public XMLErrorHandler(XMLStreamReader reader) {
		this.reader = reader;
	}

	@Override
	public void warning(SAXParseException e) throws SAXException {
		fatalError(e);
	}

	@Override
	public void error(SAXParseException e) throws SAXException {
		fatalError(e);
	}

	@Override
	public void fatalError(SAXParseException e) throws SAXException {
		// String lement = reader.getLocalName();
		String msg = e.getMessage();
		this.errorElement = msg;
	}

	public String getErrorElement() {
		return errorElement;
	}

	public void setErrorElement(String errorElement) {
		this.errorElement = errorElement;
	}

}
