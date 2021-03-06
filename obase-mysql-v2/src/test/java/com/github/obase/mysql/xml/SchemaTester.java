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
import com.github.obase.mysql.JdbcMeta;
import com.github.obase.mysql.PstmtMeta;

public class SchemaTester {

	public static void main(String[] args) throws Exception {
		File xsdFile = new File("D:\\Workspace\\git\\obase.github.io\\schema\\obase-webc-1.2.xsd");
		File xmlFile = new File("D:\\Workspace\\obase\\test-web\\src\\main\\resources\\META-INF\\webc.xml");
		validateXMLWithXSD(xmlFile, xsdFile);

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
	@SuppressWarnings("unused")
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
