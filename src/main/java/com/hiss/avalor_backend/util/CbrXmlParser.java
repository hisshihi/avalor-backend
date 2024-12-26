package com.hiss.avalor_backend.util;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;

@Service
public class CbrXmlParser {

    public Double getDollarRate() {
        try {
            URL url = new URL("http://www.cbr.ru/scripts/XML_daily.asp");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(url.openStream());

            NodeList valutes = doc.getElementsByTagName("Valute");
            for (int i = 0; i < valutes.getLength(); i++) {
                Element valute = (Element) valutes.item(i);
                String charCode = valute.getElementsByTagName("CharCode").item(0).getTextContent();
                if ("USD".equals(charCode)) {
                    String value = valute.getElementsByTagName("Value").item(0).getTextContent();
                    value = value.replace(',', '.'); // Заменяем запятую на точку
                    return Double.parseDouble(value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

