package ca.ualberta.cmput301w17t09.mood9.mood9;

import org.junit.Test;

import java.util.concurrent.ConcurrentMap;

import static org.junit.Assert.*;

/**
 * Created by Rohit on 2017-02-23.
 */

public class EmotionModelTest {

    @Test
    public void testInitEmotions(){
        EmotionModel test = new EmotionModel();
        Emotion e1 = new Emotion("1","Test","Red","Happy","Happy.png");
        Emotion e2  = new Emotion("2","Test1","blue","sad","sad.png");
        test.setOnXMLResource(e1);
        test.setOnXMLResource(e2);
        test.initEmotions();
        assertEquals(test.getEmotion("1"),e1);
        assertEquals(test.getEmotion("2"),e2);
    }

    @Test
    public void testXMLResource(){
        EmotionModel test = new EmotionModel();
        Emotion e1 = new Emotion("1","Test","Red","Happy","Happy.png");
        Emotion e2  = new Emotion("2","Test1","blue","sad","sad.png");
        test.setOnXMLResource(e1);
        test.setOnXMLResource(e2);
        Emotion teste = test.getFromXMLRessource("1");
        Emotion teste2 = test.getFromXMLRessource("2");
        assertEquals(e1,teste);
        assertEquals(e2,teste2);
    }

    @Test
    public void testGetEmotions(){
        EmotionModel test = new EmotionModel();
        Emotion e1 = new Emotion("1","Test","Red","Happy","Happy.png");
        Emotion e2  = new Emotion("2","Test1","blue","sad","sad.png");
        test.setOnXMLResource(e1);
        test.setOnXMLResource(e2);
        test.initEmotions();
        ConcurrentMap<String, Emotion> testing = test.getEmotions();
        assertEquals(testing.size(),2);
        assertEquals(testing.get("1"),e1);
        assertEquals(testing.get("2"),e2);
    }
}
