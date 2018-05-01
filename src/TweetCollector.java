import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Thread.sleep;



public class TweetCollector{

    public class TweetProcessor extends Thread {
        private Status status;
        public TweetProcessor(Status status) {
            this.status = status;
        }

        public void run() {
            collection.add(new Tweet(status));
            if (collection.size() % 100==0) {
                System.out.println("We are at " + collection.size()+"\t with " + "@" + status.getUser().getScreenName() + ":" + status.getText());
            }
            flushTweets();
        }
    }

    private static final int threadcount = 20;
    private static final int threshold = 12500;
    private static final String outfile = "tweets/tweet";
    private static int fileEnd = 1;
    private static final String fileExtension = ".json";

    private ArrayBlockingQueue<Tweet> collection;
    private JSONPacker packer;
    private TwitterStream twitterstream;

    // use keys and tokens for oauth
    private void configure() {
        ExecutorService executorService = Executors.newFixedThreadPool(threadcount);

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("qbwc0wlP0PLCVgClMzFf62cNo")
                .setOAuthConsumerSecret("RghJKOpsppG6itLmitKRZ07RiYjwjhadzppA1JYkJVphIvQq4r")
                .setOAuthAccessToken("986370491383345153-IuA7AzFUbePnIhCUgJyipmxkMaCqL6t")
                .setOAuthAccessTokenSecret("KAIoVyfbBO4JVMw9lav7vZihT9PVj6StaEe1LqfZezW8e");
        twitterstream = new TwitterStreamFactory(cb.build()).getInstance();
        StatusListener listener = new StatusListener() {
            @Override
            public void onStatus(Status status) {
                executorService.submit(new TweetProcessor(status));
                // clear the collection frequently

            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) { }

            @Override
            public void onScrubGeo(long l, long l1) { }

            @Override
            public void onStallWarning(StallWarning stallWarning) {
                System.out.println("Stall Warning found");
                try {
                    sleep(60000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onTrackLimitationNotice(int i) { }

            @Override
            public void onException(Exception e) {
                e.printStackTrace();
            }
        };
        twitterstream.addListener(listener);
        twitterstream.sample();
    }

    // constructor
    public TweetCollector() {
        collection = new ArrayBlockingQueue<Tweet>(42000);
        packer = new JSONPacker();
    }

    // if tweets go over the threshold,
    // flush out into a json file of "tweets#.json"
    public synchronized void flushTweets() {
        if (collection.size() >= threshold) {
            fileEnd++;
            while (!packer.packTweets(outfile + String.valueOf(fileEnd-1) + fileExtension, collection)) {
                fileEnd++;
            }
            collection.clear();
            collection = new ArrayBlockingQueue<Tweet>(42000);
        }
    }

    /*// query a twitter acocunt for tweets
    public void query(Twitter twitter, String squery) throws TwitterException {
        // The factory instance is re-useable and thread safe.
        Query query = new Query(squery);
        long currentId = 0L;
        QueryResult result = twitter.search(query);
        for (Status status : result.getTweets()) {
            if (!seen.containsKey(currentId = status.getUser().getId())) {
                seen.put(currentId, seen.size()); // For now use pair (userId, whenPlaced)
                frontier.add(currentId);
            }
            //System.out.println("@" + status.getUser().getScreenName() + ":" + status.getText());
        }
    }*/

    public static void main(String[] args) throws TwitterException {
        TweetCollector tc = new TweetCollector();
        tc.configure();
    }
}