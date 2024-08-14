package org.egov.dx.util;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class Utilities {

    public static String getStringFromInputStream(InputStream is) throws Exception {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            throw e;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    throw e;
                }
            }
        }
        return sb.toString();
    }

    public static Document convertStringToDocument(String xmlStr) throws Exception {
        Document doc = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;
            builder = factory.newDocumentBuilder();
            doc = builder.parse(new InputSource(new StringReader(xmlStr)));
        } catch (Exception e) {
            throw e;
        }
        return doc;
    }

    public static String GetXpathValue(XPath xPath, String RequestPath, Document doc) throws XPathExpressionException {
        String XpathValue = xPath.compile(RequestPath).evaluate(doc);
        xPath.reset();
        return XpathValue;
    }
}
