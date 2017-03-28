package ca.ualberta.cmput301w17t09.mood9.mood9;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class StatsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        getSupportActionBar().setTitle("My Stats");

        Mood9Application mAppllication = (Mood9Application) getApplication();
        ArrayList<Mood> moods = mAppllication.getMoodModel().getCurrentUserMoods();
        HashMap<String, Float> emotionHistogram = new HashMap<String, Float>();
        for(Mood m : moods){
            Float i = emotionHistogram.get(m.getEmotionId());
            if(i == null){
                i = Float.valueOf(1);
            } else {
                i = i + 1;
            }
            emotionHistogram.put(m.getEmotionId(), i);
        }

    }

    private void updateEmotionChart(HashMap<String, Float> emotionHistogram){
        PieChart chart = (PieChart) findViewById(R.id.emotion_chart);
        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        Mood9Application mAppllication = (Mood9Application) getApplication();
        ConcurrentHashMap<String, Emotion> emotions = mAppllication.getEmotionModel().getEmotions();
        Set<String> ekeys = emotions.keySet();

        float hl = getHistogramLength(emotionHistogram);

        for(String s: ekeys){
            Float f = emotionHistogram.get(s);
            PieEntry pe = new PieEntry(f/hl);
            pe.setLabel(emotions.get(s).getName());
            entries.add(pe);

            colors.add(ColorTemplate.rgb(emotions.get(s).getColor()));
        }
        PieDataSet dataSet = new PieDataSet(entries, "Emotions");
        dataSet.setSliceSpace(Float.valueOf(5));
        dataSet.setColors(colors);
        PieData pieData = new PieData(dataSet);
        chart.setData(pieData);
        chart.invalidate();
    }

    private float getHistogramLength(HashMap<String, Float> histogram){
        float sum = 0;
        for(float i: histogram.values()){
            sum = sum + i;
        }
        return sum;
    }
}
