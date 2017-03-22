package ca.ualberta.cmput301w17t09.mood9.mood9;

import android.os.AsyncTask;
import android.util.Log;
import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;
import com.searchly.jestdroid.JestDroidClient;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
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


public class ElasticSearchMOodController {
    private static JestDroidClient client;
    private static String index_name = "cmput301w17t09";
    private static String mood_type = "mood9";
    private static String user_type = "user9";


    public static class ResetElasticSearch extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String...params) {
            verifySettings();

            // Delete existing index
            DeleteIndex deleteIndex = new DeleteIndex.Builder(index_name).build();
            try{
                JestResult result = client.execute(deleteIndex);
                if (result.isSucceeded()) {
                    int x = 1;
                }

            } catch (Exception e) {
                String test = e.getMessage();
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

    public static class SearchMoodsTask extends AsyncTask<String, Void, ArrayList<Mood>> {
        @Override
        protected ArrayList<Mood> doInBackground(String...searchParameters) {
            verifySettings();

            int toField = 0;
            String field = "";
            String fieldQuery = "";
            if (searchParameters[0].indexOf(':') > -1) {
                toField = 1;
                field = searchParameters[0].substring(0, searchParameters[0].indexOf(':'));
                fieldQuery = searchParameters[0].substring(searchParameters[0].indexOf(':')+1);
            } else {
                toField = 0;
            }
            ArrayList<Mood> moods = new ArrayList<Mood>();
            String query;
            if (toField == 1) {
                query = "{\n" +
                        "    \"query\": {\n" +
                        "        \"query_string\" : {\n" +
                        "            \"fields\" : [\""+field+"\"],\n" +
                        "            \"query\" : \"" + fieldQuery + "\"\n" +
                        "        }\n" +
                        "    }\n" +
                        "}";
            } else {
                /*query = "{\n" +
                        "   \"query\": {\n" +
                        "       \"query_string\" : {\n" +
                        "           \"query\" : \"" + searchParameters[0] + "\"\n" +
                        "       }\n" +
                        "   }\n" +
                        "}";*/
                query = "{\n" +
                        "   \"size\" : 200,\n" +
                        "   \"query\" : {\n" +
                        "    \"match_all\" : {}\n" +
                        "  }\n" +
                        "}";
            }
            if (searchParameters[0].equals("") && toField == 1){
                query = "";
            }

            Search search = new Search.Builder(query)
                    .addIndex(index_name)
                    .addType(mood_type)
                    .build();

            try {
                SearchResult result = client.execute(search);
                if (result.isSucceeded()){
                    List<Mood> foundsMoods = result.getSourceAsObjectList(Mood.class);
                    moods.addAll(foundsMoods);
                }
            }
            catch (Exception e) {
                Log.i("Error", "Something went wrong when we tried to communicate with the elasticsearch server!");
            }

            return moods;
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
    }

    public static void verifySettings() {
        if (client == null) {
            String elasticSearchServer = "http://cmput301.softwareprocess.es:8080";
            DroidClientConfig.Builder builder = new DroidClientConfig.Builder(elasticSearchServer);
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

    public static class GetUsersTaskName extends AsyncTask<String, Void, User> {
        @Override
        protected User doInBackground(String...params) {
            verifySettings();

            User user = new User("");

            String query = "{\n" +
                    "    \"query\": {\n" +
                    "        \"query_string\" : {\n" +
                    "            \"fields\" : [\"name\"],\n" +
                    "            \"query\" : \"" + params[0] + "\"\n" +
                    "        }\n" +
                    "    }\n" +
                    "}";

            if (params[0].equals("")){
                query = "";
            }

            Search search = new Search.Builder(query)
                    .addIndex(index_name)
                    .addType(user_type)
                    .build();

            try {
                SearchResult result = client.execute(search);
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

    public static class AddUsersTask extends AsyncTask<User, Void, String> {

        @Override
        protected String doInBackground(User...users) {
            verifySettings();

            for (User user : users) {
                Index index = new Index.Builder(user).index(index_name).type(user_type).build();

                try {
                    DocumentResult result = client.execute(index);
                    if (result.isSucceeded()){
                        String id = result.getId();
                        user.setId(result.getId());
                    }
                }
                catch (Exception e) {
                    Log.i("Error", "The application failed to build and send the tweets");
                }
                return user.getId();
            }
            return null;
        }
    }

    private static void createUserMapping() {
        PutMapping putMapping = new PutMapping.Builder(
                index_name,
                user_type,
                "{\n" +
                        "  \"user9\": {\n" +
                        "    \"properties\": {\n" +
                        "      \"id\": {\"type\": \"string\", \"store\" : \"yes\"},\n" +
                        "      \"name\": {\"type\": \"string\", \"store\" : \"yes\"},\n" +
                        "      \"followees\" : { \"type\": \"string\", \"store\" : \"yes\" , \"index\":\"not_analyzed\" }\n" +
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
                        "      \"offlineid\": {\n" +
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
                        "        \"format\": \"yyyy-MM-dd'T'HH:mm:ss\",\n" +
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
