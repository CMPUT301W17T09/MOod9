package ca.ualberta.cmput301w17t09.mood9.mood9;

import junit.framework.TestCase;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dannick on 3/9/17.
 */

public class SocialSituationTest extends TestCase {
    SocialSituationModel ssm;

    @Override
    protected void setUp()
    {
        ClassLoader classLoader = this.getClass().getClassLoader();
        URL url = classLoader.getResource("social_situations.xml");
        InputStream is = null;
        try {
            is = url.openStream();
        } catch(IOException e) {
            fail(e.getMessage());
        }

        ssm = new SocialSituationModel(is);
    }

    @Test
    public void testGetSocialSituationExist() {
        SocialSituation ss = ssm.getSocialSituation("0");
        assertEquals(ss.getId(), "0");
        assertEquals(ss.getName(), "Alone");
    }


    @Test
    public void testGetSocialSituationnDoesNotExist() {
        SocialSituation ss = ssm.getSocialSituation("abc");
        assertNull(ss);
    }

    @Test
    public void testGetSocialSituations() {
        ConcurrentHashMap<String, SocialSituation> sss = ssm.getSocialSituations();
        assertEquals((sss.get("3")).getName(), "With Parents");
        assertEquals((sss.get("5")).getName(), "Just got dumped");
        assertEquals((sss.get("7")).getName(), "The day after");
        assertEquals((sss.get("10")).getName(), "Driving Home");

        assertNull(sss.get(":("));
    }
}
