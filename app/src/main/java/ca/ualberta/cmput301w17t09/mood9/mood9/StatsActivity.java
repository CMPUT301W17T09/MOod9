package ca.ualberta.cmput301w17t09.mood9.mood9;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class StatsActivity extends AppCompatActivity {
    ConcurrentHashMap<String, Emotion> emotions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        getSupportActionBar().setTitle("My Stats");

        Mood9Application mAppllication = (Mood9Application) getApplication();
        emotions = mAppllication.getEmotionModel().getEmotions();
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
        updateEmotionChart(emotionHistogram);
        updateTimelineChart(moods);

    }

    private void updateTimelineChart(ArrayList<Mood> moods){
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        ScatterChart chart = (ScatterChart) findViewById(R.id.time_chart);
        ScatterDataSet scatterDataSet;
        List<String> yLabels = new ArrayList<>();
        List<Entry> entries = new ArrayList<>();
        moods.sort(new MoodDateCompartor());
        for(Mood mood: moods) {
            Entry e = new Entry();
            Date d = null;
            try {
                sdf.parse(mood.getDate());

            } catch (ParseException re) {
                throw new RuntimeException();
            }
            e.setX(d.getTime());
            e.setY(Float.parseFloat(mood.getEmotionId()));
            entries.add(e);
        }
        scatterDataSet = new ScatterDataSet(entries, "Moods");
        XAxis xAxis = chart.getXAxis();
        YAxis yAxis = chart.getAxisLeft();
        xAxis.setValueFormatter(new DateAxisValueFormatter());
        yAxis.setValueFormatter(new EmotionAxisFormatter());


        ScatterData scatterData = new ScatterData(scatterDataSet);
        chart.setData(scatterData);
        chart.notifyDataSetChanged();
        chart.invalidate();

    }

    public class DateAxisValueFormatter implements IAxisValueFormatter {
        @Override
        public String getFormattedValue(float value, AxisBase axis){
            String myFormat = "yyyy-MM-dd";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            Date d = new Date((long) value);
            return sdf.format(d);
        }

        public int getDecimalDigits(){return 0;}
    }

    public class EmotionAxisFormatter implements IAxisValueFormatter {

        @Override
        public String getFormattedValue(float value, AxisBase axis){
            int id = (int) value;
            Emotion e = emotions.get(Integer.toString(id));
            return e.getName();
        }

        public int getDecimalDigits(){return 0;}
    }

    public class MoodDateCompartor implements Comparator<Mood>{
        public int compare(Mood m1, Mood m2){
            String myFormat = "yyyy-MM-dd";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            Date d1 = null;
            Date d2 = null;
            try {
                d1 = sdf.parse(m1.getDate());
                d2 = sdf.parse(m2.getDate());
            }catch (ParseException e) {
                throw new RuntimeException();
            }
            if(d1.getTime() < d2.getTime()){
                return -1;
            }else if(d1.getTime() > d2.getTime()){
                return 1;
            }
            return 0;
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
        chart.notifyDataSetChanged();
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
