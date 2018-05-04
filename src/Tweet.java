import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import twitter4j.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;

public class Tweet {
    // classes used as structs to hold user, media and mention data
    public class TweetUser {
        public long userId;
        public String name;
        public String screenname;
        public String description;
        public String location;
        public int followerCount;
        public int friendsCount;
        public int favoritesCount;
        public int statusCount;
        public String timezone;

        public TweetUser(User u) {
            userId = u.getId();
            name = u.getName();
            screenname = u.getScreenName();
            description = u.getDescription();
            location = u.getLocation();
            followerCount = u.getFollowersCount();
            friendsCount = u.getFriendsCount();
            favoritesCount = u.getFavouritesCount();
            statusCount = u.getStatusesCount();
            timezone = u.getTimeZone();
        }
    }
    public class TweetMedia {
        public long mediaId;
        public String secureMediaUrl;
        public String type;

        public TweetMedia(MediaEntity me) {
            mediaId = me.getId();
            secureMediaUrl = me.getMediaURLHttps();
            type = me.getType();
        }
    }
    public class TweetMentions {
        public long mentionId;
        public String mentionName;

        public TweetMentions(UserMentionEntity mention) {
            mentionId = mention.getId();
            mentionName = mention.getScreenName();
        }
    }
    public class TweetPlace {
        public String placeId;
        public String placeName;
        public String placeType;

        public TweetPlace(Place p) {
            placeId = p.getId();
            placeName = p.getFullName();
            placeType = p.getPlaceType();
        }
    }

    // information we store of each tweet

    public long statusId;
    public String inReplyToName;
    public long inReplyToStatusId;
    public long inReplyToUserId;
    public String language;
    public TweetPlace place;
    public long quotedStatusId;
    // scopes?
    public long[] contributors;
    public TweetUser userOfTweet;
    public String text;
    public Date timeStamp;
    public GeoLocation geolocation;
    public ArrayList<String> links;
    public ArrayList<String> titles;
    public ArrayList<String> hashtags;
    public ArrayList<TweetMedia> media;
    public ArrayList<TweetMentions> mentions;

    public Tweet() {
    }



    public Tweet(Status status) {
        this.links = new ArrayList<String>();
        this.titles = new ArrayList<String>();
        this.hashtags = new ArrayList<String>();
        this.media = new ArrayList<TweetMedia>();
        this.mentions = new ArrayList<TweetMentions>();

        /*if (status.isRetweet()) {
            return;
        }*/
        inReplyToName = status.getInReplyToScreenName();
        inReplyToStatusId = status.getInReplyToStatusId();
        inReplyToUserId = status.getInReplyToUserId();
        language = status.getLang();
        if (status.getPlace() != null) {
            place = new TweetPlace(status.getPlace());
        } else {
            place = null;
        }
        quotedStatusId = status.getQuotedStatusId();
        statusId = status.getId();
        contributors = status.getContributors();
        userOfTweet = new TweetUser(status.getUser());
        text = status.getText();
        timeStamp = status.getCreatedAt();
        geolocation = status.getGeoLocation();


        // for each url add to links and find their titles for later use
        for (URLEntity url : status.getURLEntities()) {
            try {
                Document doc;
                if ((url.getExpandedURL() != null)) {
                    links.add(url.getExpandedURL());
                    doc = Jsoup.connect(url.getExpandedURL()).userAgent("Mozilla/5.0").ignoreHttpErrors(true).timeout(0).get();
                } else {
                    links.add(url.getURL());
                    doc = Jsoup.connect(url.getURL()).userAgent("Mozilla/5.0").ignoreHttpErrors(true).timeout(0).get();
                }
                titles.add(doc.title());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (HashtagEntity hashtag : status.getHashtagEntities()){
            hashtags.add(hashtag.getText());
        }

        for (MediaEntity media : status.getMediaEntities()){
            this.media.add(new TweetMedia(media));
        }

        for (UserMentionEntity mention : status.getUserMentionEntities()){
            mentions.add(new TweetMentions(mention));
        }

    }
}

