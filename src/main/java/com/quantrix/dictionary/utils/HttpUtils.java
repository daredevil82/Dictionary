package com.quantrix.dictionary.utils;

import com.quantrix.dictionary.init.Configuration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.text.html.parser.Parser;
import javax.xml.parsers.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * Created by jasonjohns on 12/5/14.
 */
public class HttpUtils {
    private String urlQuery, contentType;

    /**
     * Zero argument constructor, must set urlQuery and contentType properties
     */
    public HttpUtils(){

    }

    /**
     *
     * @param urlQuery String URL for service to access
     * @param contentType String Charset/XML-JSON settings for request
     */
    public HttpUtils(String urlQuery, String contentType){
        this.urlQuery = urlQuery;
        this.contentType = contentType;
    }


    /**
     *
     * @param urlQuery String URL of request service
     */
    public void setUrlQuery(String urlQuery) {
        this.urlQuery = urlQuery;
    }

    /**
     *
     * @param contentType String content type of request
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     *
     * @param query String
     * @return HttpURLConnection or null if exception occurred.
     *
     * Queries an open dictionary service for definitions matching the provided query.  Returns a valid HttpURLConnection
     * if request succeeded, or null if error occurred
     *
     */
    public HttpURLConnection sendGet(String query) {

        HttpURLConnection connection;

        try {

            URL url = new URL(urlQuery + query);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", contentType);
            return connection;

        } catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }

    public String processDictionaryResults(HttpURLConnection connection){
        StringBuilder definitions = new StringBuilder();

        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse(connection.getInputStream());
            document.getDocumentElement().normalize();

            NodeList nodeList = document.getElementsByTagName("Definitions");
            NodeList definitionList;
            Node node;
            Element element;

            for (int i= 0; i < nodeList.getLength(); i++){
                node = nodeList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE){
                    element = (Element) node;

                    definitionList = element.getElementsByTagName("Definition");

                    for (int j = 0; j < definitionList.getLength(); j++){
                        definitions.append(definitionList.item(j).getTextContent() + "\n\n");
                    }
                }
            }

            return definitions.toString();

        } catch (IOException | SAXException | ParserConfigurationException e){
            e.printStackTrace();
            return null;
        }
    }

    //private test method
    private void testDictionaryRequest(HttpURLConnection connection, String query){

        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document;

            int responseCode = connection.getResponseCode();

            System.out.println("Sending request with query " + query + ".\n Response code: " + responseCode);

            document = documentBuilder.parse(connection.getInputStream());
            document.getDocumentElement().normalize();

            NodeList defintionList = document.getElementsByTagName("Definitions");
            NodeList definitions;
            Node node;
            Element element;

            StringBuilder stringBuilder = new StringBuilder();

            for (int i = 0; i < defintionList.getLength(); i++){
                node = defintionList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE){
                    element = (Element) node;

                    stringBuilder.append("Word:\t" + element.getElementsByTagName("Word").item(0).getTextContent() + "\n");
                    stringBuilder.append("Dictionary:\t" + element.getElementsByTagName("Name").item(0).getTextContent() + "\n");
                    stringBuilder.append("Definition:\t\n");

                    definitions = element.getElementsByTagName("Definition");

                    for (int definition = 0; definition < definitions.getLength(); definition++){
                        stringBuilder.append("\t" + definitions.item(definition).getTextContent() + "\n\n");
                    }
                }
            }

            System.out.println(stringBuilder.toString());
        } catch (IOException | SAXException | ParserConfigurationException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args){
        Configuration config = new Configuration();
        HttpUtils httpUtils = new HttpUtils(config.getRequestQueryString(), config.getRequestContentType());

        String testQuery = "stain";
        HttpURLConnection testConnection = httpUtils.sendGet(testQuery);
        httpUtils.testDictionaryRequest(testConnection, testQuery);
    }


}
