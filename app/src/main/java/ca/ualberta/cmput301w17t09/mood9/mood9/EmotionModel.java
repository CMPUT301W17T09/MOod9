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
 * This class holds all the different types of emotions.
 *
 * @author CMPUT301W17T09
 */

public class EmotionModel {
    private ConcurrentHashMap<String, Emotion> emotions;

    /**
     * Parses an XML file containing all the emotions and saves the parsed emotions into a map.
     * @param emotionsStream: The filestream to the XML file
     */
    public EmotionModel(InputStream emotionsStream) {
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
        Document emotionsDocument;
        try {
            emotionsDocument = builder.parse(emotionsStream);
        } catch (SAXException e) {
            throw new RuntimeException("Unable to parse xml file");
        } catch (IOException e){
            throw new RuntimeException("Unable to read xml file");
        }

        emotions = new ConcurrentHashMap<String, Emotion>();

        // Get all the emotions nodes
        NodeList nList = emotionsDocument.getElementsByTagName("Emotion");

        // Loop over them and add them to the emotions map
        for (int i = 0; i < nList.getLength(); i++) {
            Node nNode = nList.item(i);
            Emotion e = new Emotion();
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) nNode;
                e.setId(element.getElementsByTagName("id")
                        .item(0)
                        .getTextContent());
                e.setName(element.getElementsByTagName("name")
                        .item(0)
                        .getTextContent());
                e.setColor(element.getElementsByTagName("color")
                        .item(0)
                        .getTextContent());
                e.setDescription(element.getElementsByTagName("description")
                        .item(0)
                        .getTextContent());
                e.setImageName(element.getElementsByTagName("image")
                        .item(0)
                        .getTextContent());
            }
            emotions.put(e.getId(), e);
        }
    }

    public Emotion getEmotion(String id){
        //Iterating through the nodes and extracting the data.
        return  emotions.get(id);
    }

    /**
     * Get all the emotions
     * @return
     */
    public ConcurrentHashMap<String, Emotion>getEmotions(){
        return emotions;
    }
}
