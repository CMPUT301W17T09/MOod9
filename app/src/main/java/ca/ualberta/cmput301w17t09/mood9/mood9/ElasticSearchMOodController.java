package ca.ualberta.cmput301w17t09.mood9.mood9;

import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;

import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;
import com.searchly.jestdroid.JestDroidClient;

import java.util.ArrayList;
import java.util.List;

import io.searchbox.client.JestResult;
import io.searchbox.core.Delete;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.Update;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.mapping.PutMapping;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

/**
 * Created by romansky on 10/20/16.
 */
public class ElasticSearchMOodController {
    private static JestDroidClient client;
    private static String ElasticSearchServer = "http://cmput301.softwareprocess.es:8080";
    private static String index_name = "cmput301w17t09";
    private static String mood_type = "mood9";
    private static String user_type = "user9";


    public static class UpdateMoodsTask {

        public UpdateMoodsTask() {}

        public void execute(Mood... moods) {
            verifySettings();

            for (Mood mood : moods) {
                try {

                    // Delete the MOod with this specific id
                    DeleteMoodTask deleteMoodTask = new DeleteMoodTask();
                    deleteMoodTask.execute(mood);

                    // Add the updated mood to ElasticSearch
                    AddMoodsTask addMoodsTask = new AddMoodsTask();
                    addMoodsTask.execute(mood);

                } catch (Exception e) {
                    Log.i("Error", "Update to ElasticSearch failed failed");
                }
            }
        }

    }

    public static class AddMoodsTask extends AsyncTask<Mood, Void, Void> {

        @Override
        protected Void doInBackground(Mood... moods) {
            verifySettings();

            for (Mood mood : moods) {
                Index index = new Index.Builder(mood).index(index_name).type(mood_type).build();

                try {
                    DocumentResult result = client.execute(index);
                    if (result.isSucceeded()){
                        String id = result.getId();
                        mood.setId(result.getId());
                    }
                }
                catch (Exception e) {
                    Log.i("Error", "The application failed to build and send the tweets");
                }
            }
            return null;
        }
    }

    public static class DeleteMoodTask extends AsyncTask<Mood, Void, Void> {
        @Override
        protected Void doInBackground(Mood...deletedMoods) {
            verifySettings();

            for (Mood mood : deletedMoods) {
                try {
                    String id = mood.getId();
                    JestResult result = client.execute(new Delete.Builder(id).index(index_name).type(mood_type).build());
                } catch (Exception e) {
                    Log.i("Error", "Elastic Search failed to delete");
                }
            }
            return null;
        }
    }

    /**
     * This only filters by user_id
     */
    public static class GetMoodsTask extends AsyncTask<String, Void, ArrayList<Mood>> {
        @Override
        protected ArrayList<Mood> doInBackground(String...searchParameters) {
            verifySettings();

            ArrayList<Mood> moods = new ArrayList<Mood>();
            String query = "{\n  \"query\" : {\n   \"term\" : {\"user_id\":\"" + searchParameters[0] + "\"}\n  }\n}";
            if (searchParameters[0].equals("")){
                query = "";
            }

            Search search = new Search.Builder(query)
                    .addIndex(index_name)
                    .addType(mood_type)
                    .build();

            try {
                SearchResult result = client.execute(search);
                if (result.isSucceeded()){
                    List<Mood> foundMoods = result.getSourceAsObjectList(Mood.class);
                    moods.addAll(foundMoods);
                }
            }
            catch (Exception e) {
                Log.i("Error", "Something went wrong when we tried to communicate with the elasticsearch server!");
            }

            return moods;
        }
    }

    public static void verifySettings() {
        if (client == null) {
            DroidClientConfig.Builder builder = new DroidClientConfig.Builder(ElasticSearchServer);
            DroidClientConfig config = builder.build();

            JestClientFactory factory = new JestClientFactory();
            factory.setDroidClientConfig(config);
            client = (JestDroidClient) factory.getObject();

            // Already created and ready to use
            // Create a new index, and mapping for our purpose

            /*
            try {
                JestResult result = client.execute(new CreateIndex.Builder(index_name).build());

                if (result.isSucceeded()) {
                    createMOodMapping();
                    createUserMapping();
                }

            } catch (Exception e) {
                Log.i("Error", "Can't create new index or mapping");
            }
            */
        }
    }

    private static void createUserMapping() {
        PutMapping putMapping = new PutMapping.Builder(
                index_name,
                mood_type,
                "{\n" +
                        "  \"mood9\": {\n" +
                        "    \"properties\": {\n" +
                        "      \"id\": {\n" +
                        "        \"type\": \"string\",\n" +
                        "        \"store\": \"yes\"\n" +
                        "      },\n" +
                        "      \"name\": {\n" +
                        "        \"type\": \"string\",\n" +
                        "        \"store\": \"yes\"\n" +
                        "      }\n" +
                        "    }\n" +
                        "  }\n" +
                        "}"
        ).build();

        try {
            client.execute(putMapping);
        } catch (Exception e) {
            Log.i("ElasticSearch Error", "Can't create mapping for User.");
        }
    }

    private static void createMOodMapping() {
        PutMapping putMapping = new PutMapping.Builder(
                index_name,
                mood_type,
                "{\n" +
                        "  \"mood9\": {\n" +
                        "    \"properties\": {\n" +
                        "      \"id\": {\n" +
                        "        \"type\": \"string\",\n" +
                        "        \"store\": \"yes\"\n" +
                        "      },\n" +
                        "      \"latitude\": {\n" +
                        "        \"type\": \"double\",\n" +
                        "        \"store\": \"yes\"\n" +
                        "      },\n" +
                        "      \"longitude\": {\n" +
                        "        \"type\": \"double\",\n" +
                        "        \"store\": \"yes\"\n" +
                        "      },\n" +
                        "      \"trigger\": {\n" +
                        "        \"type\": \"string\",\n" +
                        "        \"store\": \"yes\"\n" +
                        "      },\n" +
                        "      \"emotionId\": {\n" +
                        "        \"type\": \"string\",\n" +
                        "        \"store\": \"yes\"\n" +
                        "      },\n" +
                        "      \"socialSituationId\": {\n" +
                        "        \"type\": \"string\",\n" +
                        "        \"store\": \"yes\"\n" +
                        "      },\n" +
                        "      \"imageTriggerId\": {\n" +
                        "        \"type\": \"string\",\n" +
                        "        \"store\": \"yes\"\n" +
                        "      },\n" +
                        "      \"date\": {\n" +
                        "        \"type\": \"date\",\n" +
                        "        \"store\": \"yes\"\n" +
                        "      },\n" +
                        "      \"user_id\": {\n" +
                        "        \"type\": \"string\",\n" +
                        "        \"store\": \"yes\"\n" +
                        "      }\n" +
                        "    }\n" +
                        "  }\n" +
                        "}"
        ).build();

        try {
            client.execute(putMapping);
        } catch (Exception e) {
            Log.i("ElasticSearch Error", "Can't create mapping for MOod.");
        }
    }
}
