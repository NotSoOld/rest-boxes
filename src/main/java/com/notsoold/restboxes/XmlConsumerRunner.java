package com.notsoold.restboxes;

import com.notsoold.restboxes.dao.RestBoxDao;
import com.notsoold.restboxes.dao.RestItemDao;
import com.notsoold.restboxes.model.RestBox;
import com.notsoold.restboxes.model.RestItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Files;

@Component
public class XmlConsumerRunner implements CommandLineRunner {

    private static final String NORMALIZE_XML_REGEX = "([\\s]+)<";

    private RestBoxDao restBoxDao;
    private RestItemDao restItemDao;

    @Autowired
    public XmlConsumerRunner(RestBoxDao restBoxDao, RestItemDao restItemDao) {
        this.restBoxDao = restBoxDao;
        this.restItemDao = restItemDao;
    }

    @Override
    public void run(String...args) throws Exception {
	if (args.length == 0) {
	    throw new IllegalArgumentException("No XML file name provided!");
	}
	String xmlFilename = args[0];
	Element storageElement = xmlFileToRootElement(xmlFilename);

	processXmlTreeRecurisvely(storageElement, null);
    }

    /**
     * Adds Box'es and Item's from XML node to the database.
     */
    private void processXmlTreeRecurisvely(Node curNode, RestBox parentBox) {
	NodeList curElementNodes = curNode.getChildNodes();
	for (int i = 0; i < curElementNodes.getLength(); i++) {
	    Node childNode = curElementNodes.item(i);
	    if (childNode.getNodeType() == Node.COMMENT_NODE) {
	        continue;
	    }
	    Long id = Long.valueOf(childNode.getAttributes().getNamedItem("id").getNodeValue());
	    switch (childNode.getNodeName()) {
	    case "Box":
		RestBox newParentBox = restBoxDao.save(new RestBox(id, parentBox));
		processXmlTreeRecurisvely(childNode, newParentBox);
		break;

	    case "Item":
		if (childNode.getAttributes().getNamedItem("color") == null) {
		    restItemDao.save(new RestItem(id, parentBox));
		} else {
		    String color = childNode.getAttributes().getNamedItem("color").getNodeValue();
		    restItemDao.save(new RestItem(id, color, parentBox));
		}
	        break;
	    }
	}
    }

    /**
     * It's 'public static' because it can be reused for general XML file parsing.
     */
    public static Element xmlFileToRootElement(String xmlFilename) throws Exception {
	// Read config from file and strip all newlines and whitespaces.
	File configFile = new File(xmlFilename);
	byte[] encoded = Files.readAllBytes(configFile.getAbsoluteFile().toPath());
	String xmlFileString = new String(encoded, Charset.forName("UTF-8"));
	xmlFileString = xmlFileString.replaceAll(NORMALIZE_XML_REGEX, "<");

	// Get XML parser and parse contents of our XML file.
	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	DocumentBuilder xmlParser = factory.newDocumentBuilder();
	ParsingErrorsListener errorListener = new ParsingErrorsListener();
	xmlParser.setErrorHandler(errorListener);
	Document document = xmlParser.parse(new InputSource(new StringReader(xmlFileString)));
	if (errorListener.getNumberOfErrors() > 0 || errorListener.getNumberOfWarnings() > 0) {
	    throw new IllegalStateException(errorListener.getWarningErrorMessages());
	}

	return document.getDocumentElement();
    }

    public static class ParsingErrorsListener implements ErrorHandler {

	private StringBuffer m_strWarningErrorMessages;
	private int m_nNoOfErrors;
	private int m_nNoOfWarnings;

	public ParsingErrorsListener() {
	    this.m_strWarningErrorMessages = new StringBuffer(256);
	    this.m_nNoOfErrors = 0;
	    this.m_nNoOfWarnings = 0;
	}

	public void error(SAXParseException e) {
	    this.m_strWarningErrorMessages.append("XML parse error occurred at line ");
	    this.m_strWarningErrorMessages.append(e.getLineNumber());
	    this.m_strWarningErrorMessages.append(" column ");
	    this.m_strWarningErrorMessages.append(e.getColumnNumber());
	    this.m_strWarningErrorMessages.append(" - ");
	    this.m_strWarningErrorMessages.append(e.toString());
	    this.m_strWarningErrorMessages.append("\n");
	    ++this.m_nNoOfErrors;
	}

	public void fatalError(SAXParseException e) {
	    this.m_strWarningErrorMessages.append("XML parse fatal error occurred at line ");
	    this.m_strWarningErrorMessages.append(e.getLineNumber());
	    this.m_strWarningErrorMessages.append(" column ");
	    this.m_strWarningErrorMessages.append(e.getColumnNumber());
	    this.m_strWarningErrorMessages.append(" - ");
	    this.m_strWarningErrorMessages.append(e.toString());
	    this.m_strWarningErrorMessages.append("\n");
	    ++this.m_nNoOfErrors;
	}

	public void warning(SAXParseException e) {
	    this.m_strWarningErrorMessages.append("XML parse warning occurred at line ");
	    this.m_strWarningErrorMessages.append(e.getLineNumber());
	    this.m_strWarningErrorMessages.append(" column ");
	    this.m_strWarningErrorMessages.append(e.getColumnNumber());
	    this.m_strWarningErrorMessages.append(" - ");
	    this.m_strWarningErrorMessages.append(e.toString());
	    this.m_strWarningErrorMessages.append("\n");
	    ++this.m_nNoOfWarnings;
	}

	public int getNumberOfErrors() {
	    return this.m_nNoOfErrors;
	}

	public int getNumberOfWarnings() {
	    return this.m_nNoOfWarnings;
	}

	public String getWarningErrorMessages() {
	    return this.m_strWarningErrorMessages.toString();
	}
    }
}