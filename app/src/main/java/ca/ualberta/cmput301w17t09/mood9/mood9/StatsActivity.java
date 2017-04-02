package ca.ualberta.cmput301w17t09.mood9.mood9;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

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
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This is the stats activity. This is what presents those awesome charts!
 *
 * @author CMPUT301W17T09
 */
public class StatsActivity extends AppCompatActivity {
    private ConcurrentHashMap<String, Emotion> emotions;
    private List<Mood> moods;
    private Mood9Application mAppllication;

    /**
     * Date Format used for parsing the dates stored in the moods.
     */
    private final String myFormat = "yyyy-MM-dd";
    private final SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

    /**
     * Initializes class instances of the emotions and mApplication
     * Also gets the last viewed moods
     * Then updates the charts
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        getSupportActionBar().setTitle("My Stats");
        mAppllication = (Mood9Application) getApplication();
        moods = mAppllication.getMoodLinkedList();
        emotions = mAppllication.getEmotionModel().getEmotions();

        updateContent();
    }

    /**
     * Gets the last list of viewed moods.
     * Then updates the chart.
     */
    @Override
    protected void onResume() {
        super.onResume();
        moods = mAppllication.getMoodLinkedList();
        updateContent();
    }

    /**
     * Method that updates the charts.
     */
    private void updateContent(){
        // If there are moods to chart
        if(moods.size() > 0) {
            // Update the charts
            HashMap<String, Float> emotionHistogram = createEmotionHistogram(moods);
            updateEmotionChart(emotionHistogram);
            updateTimelineChart();

            // Set the popular emotion to the emotion that is felt the most.
            Emotion e = emotions.get(getHistogramMax(emotionHistogram).getKey());

            ImageView emoticon = (ImageView) findViewById(R.id.emotion_image);
            TextView emotion = (TextView) findViewById(R.id.emotion_name);
            TextView statsmessage = (TextView) findViewById(R.id.emotion_statsmessage);

            int resID = getResources().getIdentifier(e.getName().toLowerCase().trim(), "drawable", getPackageName());
            emoticon.setImageResource(resID);
            emotion.setText(e.getName());
            statsmessage.setText(e.getStatsMessage());
        }
    }

    /**
     * Method that updates the updates the TimelineChart and makes it look good!
     */
    private void updateTimelineChart(){
        ScatterChart chart = (ScatterChart) findViewById(R.id.time_chart);
        List<String> yLabels = new ArrayList<>();
        List<Entry> entries = new ArrayList<>();

        // Sorts the dates oldest first
        moods.sort(new MoodDateComparator());
        // Creates the entry for the scatter plot
        for(Mood mood: moods) {
            Entry e = new Entry();
            Date d = null;
            try {
                d = sdf.parse(mood.getDate());
            } catch (ParseException re) {
                throw new RuntimeException();
            }
            e.setX(d.getTime());
            e.setY(Float.parseFloat(mood.getEmotionId()));
            entries.add(e);
        }

        // Sets the dataset for the chart
        ScatterDataSet scatterDataSet = new ScatterDataSet(entries, "Moods");
        scatterDataSet.setDrawValues(false);
        // Sets the dots in the chart to black
        scatterDataSet.setColor(ColorTemplate.rgb("#000000")); // Black

        // Sets the axises for the chart
        XAxis xAxis = chart.getXAxis();
        YAxis yAxis = chart.getAxisLeft();
        xAxis.setValueFormatter(new DateAxisValueFormatter());
        yAxis.setValueFormatter(new EmotionAxisFormatter());
        chart.getAxisRight().setDrawLabels(false);

        // Sets the data for the chart
        ScatterData scatterData = new ScatterData(scatterDataSet);
        chart.setData(scatterData);

        // Update the label and view port of the chart
        chart.getDescription().setText("Emotion Timeline");
        chart.getDescription().setTextSize(9f);
        chart.setViewPortOffsets(-10f, 0f, -10f, 0f);

        // Draw the new chart
        chart.notifyDataSetChanged();
        chart.invalidate();

    }

    /**
     * Method that updates the emotion pie chart, also making it pretty.
     * @param emotionHistogram
     */
    private void updateEmotionChart(HashMap<String, Float> emotionHistogram){
        PieChart chart = (PieChart) findViewById(R.id.emotion_chart);
        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        Set<String> ekeys = emotions.keySet();

        float hl = getHistogramLength(emotionHistogram);

        // Create the pie entries and set the colors in the aproriate order.
        for(String s: ekeys){
            Float f = emotionHistogram.get(s);
            if (f == null){
                continue;
            }
            PieEntry pe = new PieEntry(f/hl);
            pe.setLabel(emotions.get(s).getName());
            entries.add(pe);

            String cs = emotions.get(s).getColor();
            colors.add(ColorTemplate.rgb(cs));
        }
        // Create the data set and style it
        PieDataSet dataSet = new PieDataSet(entries, "Emotions");
        dataSet.setSliceSpace(Float.valueOf(5));
        dataSet.setColors(colors);
        dataSet.setDrawValues(false);
        PieData pieData = new PieData(dataSet);

        // This is the only way I know to not draw the slice text
        chart.setDrawSliceText(false);

        // Set the data & labels for the chart
        chart.setData(pieData);
        chart.getDescription().setText("Emotion Circle");
        chart.getDescription().setTextSize(9f);

        // Update the chart
        chart.notifyDataSetChanged();
        chart.invalidate();
    }

    /**
     * Gets the number of moods represented in the histogram
     * @param histogram
     * @return
     */
    private float getHistogramLength(HashMap<String, Float> histogram){
        float sum = 0;
        for(float i: histogram.values()){
            sum = sum + i;
        }
        return sum;
    }

    /**
     * Gets the histogram value with the highest entry
     * @param histogram
     * @return
     */
    private Map.Entry<String, Float> getHistogramMax(HashMap<String, Float> histogram){
        // http://stackoverflow.com/questions/5911174/finding-key-associated-with-max-value-in-a-java-map
        // Accessed 2017-03-29
        Map.Entry<String, Float> maxEntry = null;
        for (Map.Entry<String, Float> entry : histogram.entrySet())
        {
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
            {
                maxEntry = entry;
            }
        }

        return maxEntry;
    }

    /**
     * Creates a histogram of mood counts with each entry being an emotion
     * @param moods
     * @return
     */
    private HashMap<String, Float> createEmotionHistogram(List<Mood> moods){
        HashMap<String, Float> emotionHistogram = new HashMap<String, Float>();
        for (Mood m : moods) {
            Float i = emotionHistogram.get(m.getEmotionId());
            if (i == null) {
                i = Float.valueOf(1);
            } else {
                i = i + 1;
            }
            emotionHistogram.put(m.getEmotionId(), i);
        }
        return emotionHistogram;
    }

    /**
     * Sets the axis to display the date
     */
    public class DateAxisValueFormatter implements IAxisValueFormatter {
        @Override
        public String getFormattedValue(float value, AxisBase axis){
            Date d = new Date((long) value);
            return sdf.format(d);
        }

        public int getDecimalDigits(){return 0;}
    }

    /**
     * Sets the axis to display the emotion name
     */
    public class EmotionAxisFormatter implements IAxisValueFormatter {

        @Override
        public String getFormattedValue(float value, AxisBase axis){
            int id = (int) value;
            Emotion e = emotions.get(Integer.toString(id));
            return e.getName();
        }

        public int getDecimalDigits(){return 0;}
    }

    /**
     * Comparator used to sort moods by date
     */
    public class MoodDateComparator implements Comparator<Mood>{
        public int compare(Mood m1, Mood m2){
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
}
