package ca.ualberta.cmput301w17t09.mood9.mood9;

import junit.framework.TestCase;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ConcurrentMap;

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
        assertEquals((emos.get("0")).getName(), "Anger");
        assertEquals((emos.get("1")).getName(), "Confusion");
        assertEquals((emos.get("2")).getName(), "Disgust");
        assertEquals((emos.get("3")).getName(), "Fear");
        assertEquals((emos.get("4")).getName(), "Happiness");
        assertEquals((emos.get("5")).getName(), "Sadness");
        assertEquals((emos.get("6")).getName(), "Shame");
        assertEquals((emos.get("7")).getName(), "Surprise");
        Emotion emo = emos.get("8");
        assertNull(emo);
    }
}
