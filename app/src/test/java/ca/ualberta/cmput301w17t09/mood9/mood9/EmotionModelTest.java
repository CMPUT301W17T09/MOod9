package ca.ualberta.cmput301w17t09.mood9.mood9;

import junit.framework.TestCase;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ConcurrentMap;

import static org.junit.Assert.*;

/**
 * Created by Rohit on 2017-02-23.
 */

public class EmotionModelTest extends TestCase{
    EmotionModel em;

    @Override
    protected void setUp()
    {
        ClassLoader classLoader = this.getClass().getClassLoader();
        URL url = classLoader.getResource("emotions.xml");
        InputStream is = null;
        try {
            is = url.openStream();
        } catch(IOException e) {
            fail(e.getMessage());
        }

        em = new EmotionModel(is);
    }

    @Test
    public void testGetEmotionExist() {
        Emotion emo = em.getEmotion("0");
        assertEquals(emo.getId(), "0");
        assertEquals(emo.getName(), "Anger");
    }

    public void testGetEmotionDoesNotExist() {
        Emotion emo = em.getEmotion("abc");
        assertNull(emo);
    }

    public void testGetEmotions() {
        ConcurrentMap<String, Emotion> emos = em.getEmotions();
        assertEquals(((Emotion) emos.get("0")).getName(), "Anger");
        assertEquals(((Emotion) emos.get("1")).getName(), "Confusion");
        assertEquals(((Emotion) emos.get("2")).getName(), "Disgust");
        assertEquals(((Emotion) emos.get("3")).getName(), "Fear");
        assertEquals(((Emotion) emos.get("4")).getName(), "Happiness");
        assertEquals(((Emotion) emos.get("5")).getName(), "Sadness");
        assertEquals(((Emotion) emos.get("6")).getName(), "Shame");
        assertEquals(((Emotion) emos.get("7")).getName(), "Surprise");
        Emotion emo = (Emotion) emos.get("8");
        assertNull(emo);
    }
}
