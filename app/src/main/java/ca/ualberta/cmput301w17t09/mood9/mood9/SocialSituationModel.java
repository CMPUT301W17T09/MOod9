package ca.ualberta.cmput301w17t09.mood9.mood9;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by dannick on 2/22/17.
 */

public class SocialSituationModel {
    private ConcurrentHashMap<String, SocialSituation> socialSituations;

    public SocialSituationModel(InputStream socialSituationStream) {
        //Get the DOM Builder Factory
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        //Get the DOM Builder
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Unable to Configure Parser");
        }
        //Load and Parse the XML document
        //document contains the complete XML as a Tree.
        Document socialSituationDocument;
        try {
            socialSituationDocument = builder.parse(socialSituationStream);
        } catch (SAXException e) {
            throw new RuntimeException("Unable to parse xml file");
        } catch (IOException e){
            throw new RuntimeException("Unable to read xml file");
        }

        socialSituations = new ConcurrentHashMap<String, SocialSituation>();

        // Get all the socialsituations nodes
        NodeList nList = socialSituationDocument.getElementsByTagName("SocialSituation");

        // Loop over them and add them to the emotions map
        for (int i = 0; i < nList.getLength(); i++) {
            Node nNode = nList.item(i);
            SocialSituation ss = new SocialSituation();
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) nNode;
                ss.setId(element.getElementsByTagName("id")
                        .item(0)
                        .getTextContent());
                ss.setName(element.getElementsByTagName("name")
                        .item(0)
                        .getTextContent());
                ss.setDescription(element.getElementsByTagName("description")
                        .item(0)
                        .getTextContent());
            }
            //socialSituations.put(ss.getId(), ss);
            socialSituations.put(String.valueOf(i), ss);
        }
    }

    public SocialSituation getSocialSituation(String id){
        //Iterating through the nodes and extracting the data.
        return  socialSituations.get(id);
    }

    public ConcurrentHashMap<String, SocialSituation>getSocialSituations(){
        return socialSituations;
    }
}
