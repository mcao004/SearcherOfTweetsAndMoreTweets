
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;

public class JSONPacker {

    ObjectMapper objectmapper;

    public JSONPacker() {
        objectmapper = new ObjectMapper();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm a z");
        objectmapper.setDateFormat(df);
    }

    public String pack(Object obj) throws JsonProcessingException {
        return objectmapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
    }

    public boolean packTweets(String filename, ArrayBlockingQueue<Tweet> tweets) {
        File f = null;
        f = new File(filename);
        if (f.exists()) {
            return false;
        }
        // file yet to exist
        StringBuilder result = new StringBuilder();
        try {
            result.append('[');
            for (Iterator<Tweet> it = tweets.iterator(); ((Iterator) it).hasNext(); ) {
                result.append(objectmapper.writeValueAsString(it.next()));
                if (it.hasNext()) {
                    result.append(",\n");
                }
            }
            result.append(']');
            BufferedWriter output = new BufferedWriter(new FileWriter(filename));
            output.append(result);
            output.flush();
            System.out.println(result);
            System.out.println("Printed out tweets for file " + filename);
            System.out.println();
            return true;
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            ;
        }

        return false;

    }

    /*public static void main(String[] args) throws IOException {
        ObjectMapper objectmapper = new ObjectMapper();
        // object to store in JSON
        Tweet tweet = new Tweet();

        // to file
        objectmapper.writeValue(new File ("user.json"), tweet);

        // to string
        String jsonString = objectmapper.writeValueAsString(tweet);
    }*/

}
