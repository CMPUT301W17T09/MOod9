package ca.ualberta.cmput301w17t09.mood9.mood9;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;

import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;
import com.searchly.jestdroid.JestDroidClient;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.searchbox.client.JestResult;
import io.searchbox.core.Delete;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Get;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.Update;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;
import io.searchbox.indices.mapping.PutMapping;

import static android.content.Context.MODE_PRIVATE;

/**
 * Provides the elastic search connectivity for the app.
 * @author CMPUT301W17T09
 */
public class ElasticSearchMOodController {
    private static JestDroidClient client;
    private static String ElasticSearchServer = "http://cmput301.softwareprocess.es:8080";
    private static String index_name = "cmput301w17t09";
    private static String mood_type = "mood9";
    private static String user_type = "user9";


    /**
     * Deletes and recreates indexes on elastic search.
     */
    public static class ResetElasticSearch extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String...params) {
            verifySettings();

            // Delete existing index
            DeleteIndex deleteIndex = new DeleteIndex.Builder(index_name).build();
            try{
                JestResult result = client.execute(deleteIndex);
                if (result.isSucceeded()) {
                }

            } catch (Exception e) {
                Log.i("Error", e.getMessage());
            }

            // Rebuild index
            try {
                JestResult result = client.execute(new CreateIndex.Builder(index_name).build());

                if (result.isSucceeded()) {
                    createMOodMapping();
                    createUserMapping();
                }

            } catch (Exception e) {
                Log.i("Error", "Can't create new index or mapping");
            }

            return null;
        }
    }

    public static class AddMoodsTask extends AsyncTask<Void, Void, Void> {

        private ArrayList<Mood> Moods;

        public AddMoodsTask(ArrayList<Mood> moodList) {
            Moods = moodList;
        }

        /**
         * Deletes a mood
         * @param moods
         */
        protected void DeleteMoods(ArrayList<Mood> moods) {
            verifySettings();

            for (Mood mood : moods) {
                try {
                    String id = mood.getId();
                    JestResult result = client.execute(new Delete.Builder(id).index(index_name).type(mood_type).build());
                } catch (Exception e) {
                    Log.i("Error", "Elastic Search failed to delete");
                }
            }
        }

        /**
         * Gets the mooods using the search parameters
         * @param searchParameters
         * @return
         */
        protected ArrayList<Mood> GetMoods(String...searchParameters) {
            verifySettings();

            ArrayList<Mood> moods = new ArrayList<Mood>();
            String query = "{\n" +
                    "    \"query\": {\n" +
                    "        \"query_string\" : {\n" +
                    "            \"fields\" : [\"user_id\"],\n" +
                    "            \"query\" : \"" + searchParameters[0] + "\"\n" +
                    "        }\n" +
                    "    }\n" +
                    "}";

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

        /**
         * Adds a new mood to elasticsearch
         */
        protected void addNewMoods() {
            for (Mood mood: Moods) {
                Index index = new Index.Builder(mood).index(index_name).type(mood_type).build();

                try {
                    DocumentResult result = client.execute(index); // TODO: this always fails when adding new moods
                    System.out.println(result.toString());
                    if (result.isSucceeded()){
                        System.out.println("PUSH");
                        String id = result.getId();
                        mood.setId(result.getId());
                    }
                }
                catch (Exception e) {
                    Log.i("Error", "The application failed to build and send the tweets");
                }
            }
        }

        @Override
        protected Void doInBackground(Void... v) {
            verifySettings();

            // Get moods online
            ArrayList<Mood> OnlineMood = GetMoods(Moods.get(0).getUser_id());

            // Delete those moods
            DeleteMoods(OnlineMood);

            // Add new updated moods from Offline soure
            addNewMoods();

            return null;
        }
    }

    /**
     * Deletes moods
     */
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
    public static class GetMoodsTask extends AsyncTask<Void, Void, ArrayList<Mood>> {

        private HashMap<String, String> query_params;

        public GetMoodsTask(HashMap<String, String> qp) {
            query_params = qp;
        }

        @Override
        protected ArrayList<Mood> doInBackground(Void...v) {
            verifySettings();

            ArrayList<Mood> moods = new ArrayList<Mood>();

            for ( HashMap.Entry<String, String> entry : query_params.entrySet() ) {
                String query = "{\n" +
                        "   \"size\" : 200,\n" +
                        "   \"query\": {\n" +
                        "        \"query_string\" : {\n" +
                        "            \"fields\" : [\"" + entry.getKey() + "\"],\n" +
                        "            \"query\" : \"" + entry.getValue() + "\"\n" +
                        "        }\n" +
                        "    }\n" +
                        "}";

                if (entry.getKey() == ""){
                    query = "{\n" +
                            "   \"size\" : 200,\n" +
                            "   \"query\" : {\n" +
                            "    \"match_all\" : {}\n" +
                            "  }\n" +
                            "}";
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
                Log.d("ESTEST3", "Inside Loop");
            }
            Log.d("ESTEST2", "Inside async task");
            return moods;
        }
    }

    /**
     * Creates a JestDroid client if it has not been connected.
     */
    public static void verifySettings() {
        if (client == null) {
            DroidClientConfig.Builder builder = new DroidClientConfig.Builder(ElasticSearchServer);
            DroidClientConfig config = builder.build();

            JestClientFactory factory = new JestClientFactory();
            factory.setDroidClientConfig(config);
            client = (JestDroidClient) factory.getObject();

            // Already created and ready to use
            // Create a new index, and mapping for our purpose
        }
    }

    /**
     * Adds a followees to a user.
     */
    public static class UpdateUsersTask extends AsyncTask<User, Void, Void> {

        @Override
        protected Void doInBackground(User...users) {
            for (User user : users) {
                String followees = "[";

                for (String id : user.getFollowees())
                    followees += "\"" + id+ "\",";

                followees = followees.substring(0, followees.length()-1) + "]";

                String update_query =   "{\n" +
                        "   \"doc\" : {\n" +
                        "      \"followees\": " + followees + "\n" +
                        "   }\n" +
                        "}";

                try {
                    JestResult result = client.execute(new Update.Builder(update_query).index(index_name).type(user_type).id(user.getId()).build());
                } catch (Exception e) {

                }
            }
            return null;
        }
    }

    /**
     * Deletes a user
     */
    public static class DeleteUsersTask extends AsyncTask<User, Void, Void> {
        @Override
        protected Void doInBackground(User...users) {
            verifySettings();

            for (User user: users) {
                try {
                    String id = user.getId();
                    JestResult result = client.execute(new Delete.Builder(id).index(index_name).type(user_type).build());
                } catch (Exception e) {
                    Log.i("Error", "Elastic Search failed to delete user");
                }
            }

            return null;
        }
    }

    /**
     * Gets the username of the user.
     */
    public static class GetUsersTaskName extends AsyncTask<String, Void, ArrayList<User>> {
        @Override
        protected ArrayList<User> doInBackground(String...params) {
            verifySettings();

            ArrayList<User> user_list = new ArrayList<User>();

            String query = "{\n" +
                    "    \"query\": {\n" +
                    "        \"query_string\" : {\n" +
                    "            \"fields\" : [\"name\"],\n" +
                    "            \"query\" : \"" + params[0] + "\"\n" +
                    "        }\n" +
                    "    }\n" +
                    "}";

            if (params[0].equals("")){
                query = "{\n" +
                        "   \"size\" : 200,\n" +
                        "   \"query\" : {\n" +
                        "    \"match_all\" : {}\n" +
                        "  }\n" +
                        "}";
            }

            Search search = new Search.Builder(query)
                    .addIndex(index_name)
                    .addType(user_type)
                    .build();

            try {
                SearchResult result = client.execute(search);
                if (result.isSucceeded()){
                    List<User> foundUser = result.getSourceAsObjectList(User.class);
                    user_list.addAll(foundUser);
                }
            }
            catch (Exception e) {
                Log.i("Error", "Something went wrong when we tried to communicate with the elasticsearch server!");
            }

            return user_list;
        }
    }

    public static class GetUsersTaskID extends AsyncTask<String, Void, User> {
        @Override
        protected User doInBackground(String...params) {
            verifySettings();

            User user = new User("");

            Get get = new Get.Builder(index_name, params[0]).type(user_type).build();

            try {
                JestResult result = client.execute(get);
                if (result.isSucceeded()){
                    user = result.getSourceAsObject(User.class);
                }
            }
            catch (Exception e) {
                Log.i("Error", "Something went wrong when we tried to communicate with the elasticsearch server!");
            }

            return user;
        }
    }

    /**
     * Adds a user to elastic search
     */
    public static class AddUsersTask extends AsyncTask<User, Void, String> {
        @Override
        protected String doInBackground(User...users) {
            verifySettings();
            String id = "";
            for (User user : users) {
                Index index = new Index.Builder(user).index(index_name).type(user_type).build();

                try {
                    DocumentResult result = client.execute(index);
                    if (result.isSucceeded()){
                        id = result.getId();
                        user.setId(result.getId());
                    }
                }
                catch (Exception e) {
                    Log.i("Error", "The application failed to build and send the tweets");
                }
            }
            return id;
        }
    }

    /**
     * Creates the user map from JSON
     */
    private static void createUserMapping() {
        PutMapping putMapping = new PutMapping.Builder(
                index_name,
                user_type,
                "{\n" +
                        "  \"user9\": {\n" +
                        "    \"properties\": {\n" +
                        "      \"id\": {\"type\": \"string\", \"store\" : \"yes\"},\n" +
                        "      \"name\": {\"type\": \"string\", \"store\" : \"yes\"},\n" +
                        "      \"followees\" : { \"type\": \"string\", \"store\" : \"yes\" , \"index\":\"not_analyzed\" },\n" +
                        "      \"requests\" : { \"type\": \"string\", \"store\" : \"yes\" , \"index\":\"not_analyzed\" }\n" +
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

    /**
     * Creates the mood map from JSON.
     */
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
                        "      \"offlineid\": {\n" +
                        "        \"type\": \"string\",\n" +
                        "        \"store\": \"yes\"\n" +
                        "      },\n" +
                        "      \"socialSituationId\": {\n" +
                        "        \"type\": \"string\",\n" +
                        "        \"store\": \"yes\"\n" +
                        "      },\n" +
                        "      \"image\": {\n" +
                        "        \"type\": \"string\",\n" +
                        "        \"store\": \"yes\"\n" +
                        "      },\n" +
                        "      \"imageTriggerId\": {\n" +
                        "        \"type\": \"string\",\n" +
                        "        \"store\": \"yes\"\n" +
                        "      },\n" +
                        "      \"date\": {\n" +
                        "        \"type\": \"string\",\n" +
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
