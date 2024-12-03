package Actors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import org.apache.pekko.actor.AbstractActor;
import org.apache.pekko.actor.Props;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class UserActor extends AbstractActor {
    public ArrayList<String> queries = new ArrayList<>();

    private final List<String> happyList = Arrays.asList(
            "happy", "joy", "cheerful", "delighted", "excited", "thrilled", "fun", "fantastic", "awesome", "wonderful",
            "amazing", "smile", "love", "bliss", "grateful", "blessed", "content", "lucky", "positive", "laugh",
            "giggle", "upbeat", "bright", "harmony", "winning", "celebration", "jubilant", "elated", "proud", "entertained",
            "successful", "charming", "glowing", "peaceful", "hopeful", "lively", "bubbly", "sunny", "sparkle", "terrific",
            "victorious", "optimistic", "festive", "pleasure", "serene", "admiration", "empowered", "precious", "lovely",
            "vibrant", "heartwarming", "laughter", "joyfully", "hilarious", "magical", "adventurous", "triumphant",
            "generous", "dazzling", "spirited", "pride", "relaxing", "incredible", "blessing", "unity", "beautiful",
            "satisfaction", "excitement", "enjoy", "accomplish", "achieve", "win", "succeed", "engaged", "appreciate",
            "reward", "celebrate", "admire", "rejoice", "fantasy", "relaxed", "fulfilled", "passionate", "enthusiastic",
            "energized", "motivated", "captivated", "smiling", "glad", "playful", "cheer", "uplifting", "loveable", "friendly",
            "joyous", "ecstatic", "amused", "pleasant", "secure", "prosper", "inspired", "strength", "bold", "creative",
            "harmonious", "charmed", "fascinating", "play", "comfort", "admiring", "cheering", "eager", "grace",
            "celebrating", "contentment", "admiration", "hopefulness", "confidence", "helpful", "supportive", "thrill",
            "appreciated", "peace", "rested", "joyous", "witty", "giggling", "uplifted", "charming", "savor",
            "compassionate", "exhilarated", "rejuvenated", "satisfying", "elation", "glee", "generosity", "delight",
            "wondrous", "serenity", "invigorated", "refreshed", "dreamy", "fortunate", "enlightened", "heroic",
            "brilliant", "victory", "enthusiasm", "pleased", "rewarded", "exalted", "jovial", "marvelous", "revered", "glory",
            "charmed", "vibrancy", "heartening", "liberated", "trust", "calm", "affectionate", "playful",
            "acclaim", "mirth", "exuberant", "gracious", "harmony", "courageous", "cherish", "celebration", "enchanted",
            "fascinated", "gratitude", "wonder", "lighthearted", "relish", "majestic", "benefit", "freedom", "rewarding",
            "splendid", "aspiring", "splendor", "cherished", "elated", "glorified", "friendship", "relieved", "cherishing",
            "sincere", "devotion", "appreciating", "joyfulness", "joyful","admired", "sociable", "jolly", "entertaining",
            "adored", "pure", "sweet", "hero", "genuine", "pleasure", "positive", "sunny", "outgoing",
            "affection", "reassured", "brightened", "vital", "brave", "hope", "spirited", "hugged", "mindful",
            "faithful", "euphoric", "valued", "moved", "empower", "insightful", "reverence", "romantic", "thankful",
            "optimism", "sunshine", "laughing", "enthused", "boldness", "captivating", "energetic",
            "heartfelt", "special", "vivacious", "rich", "dear", "fame", "innocent", "cheerful", "energize", "awesome",
            "secure", "wholesome", "contented", "kindness", "merry", "blissful", "praise", "honor", "inspired",
            "trusted", "glow", "endearing", "empathy", "brave", "victorious", "sacred", "favored", "splendid",
            "rejuvenate", "adorable", "integrity", "cheerfulness", "gorgeous", "pride", "refreshing", "healthy", "laughs",
            "heavenly", "pleasurable", "patience", "caring", "delightful", "radiant", "passion", "graceful", "wellness",
            "humor", "courage", "fellowship", "gleeful", "powerful", "attractive", "invincible", "joy", "whimsy",
            "empowered", "abundant", "reliable", "genuine", "relish", "wonderful", "refreshed", "enchanting", "blissfully",
            "unique", "prosperity", "thankfulness", "cherished", "excellence", "loyalty", "fulfillment", "well-being",
            "vibrance", "passionate", "ecstasy", "happy", "smiles", "radiance", "laugh", "contentment", "optimistic",
            "hopeful", "charity", "peacefulness", "warmth", "support", "cheeriness", "fortuitous", "positive", "charming",
            "illuminated", "meaningful", "breezy", "joyous", "innocent", "cheerfully", "lighthearted", "tranquil",
            "steadfast", "integrity", "appreciative", "merriment", "steady", "illuminate", "carefree", "enjoyment",
            "mirthful", "profoundly", "trusting", "companionably", "nurture", "wholesomeness", "cordial", "well-meaning",
            "sociable", "enthuse", "refreshing", "wholeness", "endurance", "stability", "upliftment", "elevated",
            "uplifted", "reliable", "attentive", "trustworthiness", "amicable", "heartwarming", "resilience", "heartfelt",
            "resilient", "giddy", "upliftment", "humorous", "supportively", "helpfulness", "joyously", "sturdy", "faith",
            "fulfilled", "charm", "heartiness", "fulfilled", "tenacity", "bubbly", "gleeful", "genuine", "zest", "appreciated",
            "adulation", "optimistic", "cheerily", "gleaming", "positively", "humorously", "assured", "appreciative",
            "laughter-filled", "delighting", "good-hearted", "uplift", "radiantly", "adoring", "brightest", "effervescent",
            "celebratory", "enlivened", "mirthful", "gleaming", "sun-kissed", "glowing", "upliftment", "upbeat",
            "excitable", "gleefulness", "cherishing", "vitality", "enthuse", "fortuitous", "fulfilled", "brilliant",
            "delicious", "nourishing", "festivity", "joyous", "heartening", "enthralling", "winsome", "heart-warming",
            "ecstatic", "stunning", "wonder-struck", "winning", "witty", "vibrant", "appreciatively", "satisfying", "joyous",
            "amity", "fascination", "dazzle", "sympathetic", "treasured", "cherished", "relaxing", "wondrous", "empathy",
            "giddy", "rejoicing", "melodious", "spellbinding", "tender-hearted", "fulfillment", "serenity", "favorably",
            "serenely", "untroubled", "playfully", "beaming", "encouraging", "inspired", "animated", "magnificent", "secure",
            "thrill-filled", "optimistic", "hopefulness", "dream-filled", "encouragement", "sociability", "communal",
            "growing", "connection", "inspiring", "lightheartedly", "loved", "cherishing", "exaltation", "serene", "impressive",
            "cheery", "dreamer", "smilingly", "boundless", "nurturing", "giving", "peaceably", "elevating", "courage-filled",
            "freed", "daring", "fulfilled", "zestful", "exuberant", "accomplished", "spectacular", "spellbound", "impressive",
            "pleasurable", "ambitious", "trustworthy", "assuredly", "benevolent", "supportive", "elegant", "purposeful",
            "winning", "well-meaning", "effortless", "well-being", "dazzling", "warm-hearted", "joyously", "belonging",
            "motivating", "compassion-filled", "admiration", "lightness", "confidence", "wise", "uplift", "meaningful",
            "satisfactorily", "accomplishment", "comfortably", "fondness", "sunny", "steadfast", "independent", "lovable",
            "faithful", "enthused", "happily", "chipper", "gleaming", "aspiration", "peaceful", "motivated", "glistening",
            "bubbly", "favorable", "assuring", "sunlight", "jubilantly", "authentic", "contentedly", "fortified", "glee-filled",
            "valued", "adventurous", "genuine-hearted", "laughter-rich", "prosperous", "proudly", "blissfully", "heartfelt",
            "steadily", "gallant", "cheery-hearted", "nurtured", "fond", "nourished", "enthusiastic", "affirmation",
            "inviting", "dreaminess", "admiration", "enthusing", "affirmed", "contentment", "gracious", "soothing", "companionable",
            "health-giving", "adored", "confidence-filled", "gracefully", "genuine-hearted", "purpose-filled", "trusted",
            "pure-hearted", "inspired", "healing", "adventure", "promising", "glorified", "cherished", "innate", "devoted",
            "compassionate", "courageous", "uplifting", "friendship", "positivism", "restored", "compassion-filled",
            "courage-filled", "adored", "generosity", "purposeful", "motivated", "passionate", "positive-hearted"
    );


    private final List<String> sadList = Arrays.asList(
            "sad", "unhappy", "miserable", "down", "upset", "grief", "sorrow", "pain", "hurt", "tearful", "lonely",
            "broken", "tragic", "devastated", "tired", "regret", "drained", "cried", "heartache", "downcast", "somber",
            "hopeless", "despondent", "glum", "anguish", "sorrowful", "fearful", "weary", "grief-stricken", "disappointed",
            "anxious", "nervous", "crushed", "distress", "nightmare", "bleak", "despair", "rejected", "depressed",
            "gloomy", "ashamed", "betrayed", "loss", "mourning", "shame", "tear", "desperate", "heartbroken",
            "discouraged", "frustrated", "sorry", "mournful", "embarrassed", "worthless", "abandoned", "melancholy",
            "powerless", "aching", "dreadful", "empty", "hurtful", "isolated", "longing", "pessimistic", "remorseful",
            "somber", "subdued", "pathetic", "restless", "struggle", "fail", "afraid", "loneliness", "helpless", "despairing",
            "afraid", "dislike", "suffering", "mourn", "cry", "uncomfortable", "painful", "stressed", "overwhelmed",
            "fear", "missed", "offended", "unappreciated", "worry", "hopelessness", "trapped", "scream", "displeased",
            "grieving", "agonize", "abandon", "insecure", "fearful", "disillusioned", "numb", "doubt", "isolated", "resent",
            "loss", "horrified", "injustice", "dissatisfied", "helplessness", "sadness", "vulnerable", "inadequate", "weak",
            "fearful", "defeated", "resentful", "timid", "ashamed", "painful", "mistrust", "humiliated", "self-doubt", "fear",
            "burden", "anxiety", "dejected", "rejection", "hurt", "lacking", "burdened", "pity", "regrettable", "distrust",
            "struggled", "worn", "unresolved", "overwhelmed", "deprived", "lonesome", "disheartened", "distressed",
            "alienated", "hopelessness", "void", "regretted", "grief-stricken", "depressing", "nervousness", "grief",
            "apathetic", "dismal", "miserable", "worthless", "exhausted", "useless", "pessimism", "worn-out", "detached",
            "scared", "resentment", "forlorn", "distraught", "dejection", "fearful", "humiliated", "bleakness", "pathetic",
            "pitiable", "bruised", "worried", "unhappy", "rejected", "lonely", "sorrow", "depression", "despair",
            "grief", "tormented", "anxiously", "isolated", "frustrating", "damaged", "feared", "weakness", "misery",
            "resentful", "paralyzed", "anxious", "hopeless", "broken", "dreaded", "displeasure", "hardened", "fail",
            "offended", "disgust", "faltered", "uneasy", "discouraged", "lost", "discouragement", "insecure", "afraid",
            "grieving", "grieved", "regretful", "fragile", "withdrawn", "rejected", "weakly", "fragility", "dispirited",
            "hesitant", "desolate", "mournfully", "detachment", "guilt", "doubtful", "uneasiness", "sore", "fearful",
            "tired", "inconsolable", "melancholic", "futility", "hopeless", "mournful", "upset", "depressed", "stressed",
            "pitiful", "regretfully", "isolated", "unworthy", "suffering", "frustrating", "dejection", "inadequate",
            "despairingly", "heartbroken", "tearfully", "doubts", "pained", "unwanted", "hopeless", "apologetic", "vulnerable",
            "dissatisfaction", "exhaustion", "dejection", "loss", "isolated", "sorrowfully", "rejected", "lonesome", "anguish",
            "sickly", "lacking", "misery", "regrets", "trapped", "unhappily", "isolated", "failing", "tears", "inferior",
            "tortured", "deplorable", "destitute", "depressed", "withdrew", "ashamed", "frustration", "inferiority",
            "strained", "seclusion", "pitied", "loneliness", "pessimistic", "agony", "timid", "hurt", "nervously", "abandoned",
            "forlornly", "weakened", "disheartened", "isolated", "desolate", "fearfulness", "sorrowfully", "persecuted",
            "dejected", "distressingly", "pained", "disdain", "regretfully", "weary", "helplessly", "frustrations",
            "woefully", "discouragement", "regretting", "despondency", "drained", "suffering", "bitterness", "grievance",
            "painstakingly", "woeful", "disheartened", "bleakly", "dreadfully", "lamentable", "separation", "mournfulness",
            "unsure", "discomfort", "melancholy", "forfeited", "stressful", "neglected", "regrettable", "depressing",
            "despairingly", "weakening", "demoralized", "pained", "crying", "sadden", "regretting", "self-pity", "abandoned",
            "ashamedly", "depressively", "subdued", "tragedy", "resigned", "self-pitying", "heartache", "fears",
            "disillusioned", "burdening", "unsatisfied", "regretful", "futile", "disappointment", "upset", "grief-struck",
            "mourned", "tormented", "resignation", "troubled", "pain", "powerless", "diminished", "helpless", "unsuccessful",
            "ailing", "weaken", "woe", "desperation", "isolating", "remorse", "distressed", "miserably", "pitifully",
            "regretful", "self-doubts", "self-conscious", "fear-stricken", "pessimism", "regrettably", "doubting",
            "darkness", "teary", "painfully", "depressed", "withdraw", "defeated", "misguided", "mournfulness", "heartache",
            "distrustful", "insecurity", "alienate", "grieving", "hurtful", "troubled", "longing", "melancholy", "discouraged",
            "withdrawn", "detest", "confusion", "regrettable", "sorrowful", "unconvinced", "fearing", "pitied", "agonizing",
            "dread", "withdrawing", "defenseless", "vulnerability", "regrets", "distressed", "dreadful", "isolation", "feared",
            "dead", "death", "die", "murder", "rape", "assault", "tragedy", "loss", "flood", "earthquake", "disaster",
            "catastrophe", "displaced", "homeless", "victimized", "trauma", "calamity", "unrest", "conflict", "violence",
            "genocide", "war", "terrorism", "attack", "injured", "hospitalized", "fatality", "disease", "epidemic",
            "pandemic", "outbreak", "virus", "plague", "illness", "diagnosis", "hospital", "surgery", "infection",
            "incurable", "chronic", "terminal", "hospice", "contagion", "quarantine", "suicide", "self-harm", "overdose",
            "loss", "fatal", "casualties", "emergency", "displaced", "orphaned", "widowed", "orphan", "massacre",
            "victims", "accident", "crash", "wreck", "collision", "injury", "fall", "disaster", "collapse", "explosion",
            "blast", "volcano", "eruption", "fire", "wildfire", "blaze", "burn", "scorched", "evacuated", "drought",
            "starvation", "famine", "malnutrition", "hunger", "destitution", "poverty", "abuse", "neglect", "exploitation",
            "kidnapping", "hostage", "arson", "theft", "robbery", "burglary", "looting", "fraud", "embezzlement", "cheated",
            "betrayal", "deception", "misery", "sorrow", "crying", "heartbroken", "tears", "sobbing", "sorrowful",
            "forlorn", "abandoned", "overwhelmed", "shattered", "fears", "vulnerability", "hurt", "scarred", "betrayed",
            "lost", "trapped", "alone", "despair", "hopeless", "anguish", "regret", "bitterness", "sorrowing", "helpless",
            "demoralized", "hardened", "embittered", "tormented", "exhausted", "feeling-down", "shattered", "agony",
            "traumatized", "misunderstood", "unfulfilled", "disrespected", "abandoned", "mournfulness", "unsympathetic",
            "anguished", "torment", "desperately", "uncomfortable", "forbidden", "agonizing", "devastation", "victimhood",
            "offended", "sorrowfulness", "agonizingly", "miserably", "achingly", "suffering", "anguishedly", "disturbed",
            "haunted", "consoled", "conflicted", "disrespected", "feeling-pain", "disconnected", "lost-cause", "heartache",
            "grieving", "heartbrokenly", "burdened", "grieved", "pain-stricken", "pained", "lamentation", "tormentedly",
            "darkness", "tormenting", "desolate", "morose", "pitying", "mournful", "solitude", "dreadfully", "regretfully",
            "bitterly", "struggling", "severe", "emptiness", "lost-faith", "haunted", "forlornly", "barren", "hauntingly",
            "crippled", "bleakly", "darkened", "aching", "grievously", "depressingly", "comfortless", "barrenly",
            "apathetically", "misfortune", "aggrieved", "tormentedly", "burdeningly", "forsaken", "gloom-filled",
            "distraughtly", "sorrow-ridden", "bruisingly", "mournfulness", "painful", "feeling-loss", "teary", "darkened",
            "shadowed", "shamefully", "barren-hearted", "hauntedly", "bereaved", "loss-filled", "darkening", "lone", "abused",
            "solitary", "scarred-hearted", "pain-strickenly", "bleak-hearted", "scarred-soul", "dark-hearted", "anguished-heart",
            "loss-ridden", "faded", "regret-filled", "forsakenly", "bitter-hearted", "cold-hearted", "misunderstoodly",
            "feeling-abandoned", "shattered-heart", "bleakly-hearted", "darkly", "sorrowfully-hearted", "cry-filled", "cold-heartedly",
            "aching-heart", "lonely-hearted", "empty-hearted", "sorrow-riddenly", "heartache-filled", "forsaken-heart", "grim-hearted"
    );

    private final List<String> happyEmojis = Arrays.asList("😊", "😁", "😄", "😃", "😀", "😆", "🤗", "😍", "🥰", "😎", "🙌", "💖", "💕", "🌞", "🎉", "🎊", "🥳", "👏", "🏆", "💪", "🎂", "🍰", "☀", "🌈", "❤");
    private final List<String> sadEmojis = Arrays.asList("😞", "😟", "😔", "😢", "😭", "😣", "😫", "😩", "😓", "😥", "💔", "☹", "😿", "💧", "🙁", "😧", "😕", "🌧", "🖤");

    // Happy and Sad Emoticons
    private final List<String> happyEmoticons = Arrays.asList(":-)", ":)", ":D", "^_^", ":'D");
    private final List<String> sadEmoticons = Arrays.asList(":-(", ":(", ":'(", "D:", ";(", "T_T", "(;;)", "(..)", ";_;");

    public static class FetchSearchData {
        public final String query;
        public final YouTube youtube;
        public final String apiKey;

        public FetchSearchData(YouTube youtube, String query, String apiKey) {
            this.query = query;
            this.youtube = youtube;
            this.apiKey = apiKey;
        }
    }

    public static Props props() {
        return Props.create(UserActor.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(UserActor.FetchSearchData.class, this::handleFetchSearchData)
                .build();
    }

    private void handleFetchSearchData(UserActor.FetchSearchData request) {
        try {
            if (request.apiKey.isEmpty())
                throw new NullPointerException();
            else if (request.apiKey.length() < 39)
                throw new IllegalArgumentException("API key length too short");
            else if (request.apiKey.length() > 39)
                throw new IllegalArgumentException("API key length too long");

            boolean f = true;

            for (int i = 0; i < request.apiKey.length(); i++)
                if (!Character.isLetterOrDigit(request.apiKey.charAt(i)) && request.apiKey.charAt(i) != '_' && request.apiKey.charAt(i) != '-')
                    f = false;

            if (!f)
                throw new IllegalArgumentException("API key must only contain alphanumeric characters with - and _");

            String query = URLEncoder.encode(request.query, StandardCharsets.UTF_8);

            queries.add(query);

            YouTube.Search.List search = request.youtube.search().list(Collections.singletonList("snippet"));
            search.setQ(query);
            search.setMaxResults(50L);
            search.setKey(request.apiKey);

            // Execute the search request and get the response
            SearchListResponse response = search.execute();

            // Filter and map the response to extract video details
            List<List<String>> videos = response.getItems().stream()
                    .filter(video -> "youtube#video".equals(video.getId().getKind()))
                    .map(video -> Arrays.asList(
                            video.getSnippet().getTitle(),
                            "https://www.youtube.com/watch?v=" + video.getId().getVideoId(),
                            "https://www.youtube.com/@" + video.getSnippet().getChannelTitle(),
                            video.getSnippet().getDescription(),
                            video.getSnippet().getThumbnails().getHigh().getUrl(),
                            "/channel/" + video.getSnippet().getChannelId()))
                    .limit(10)
                    .collect(Collectors.toList());

            // Directly extract descriptions from the 'videos' list
            ArrayList<String> descriptions = new ArrayList<>();
            for (List<String> videoData : videos) {
                descriptions.add(videoData.get(3));  // Description is at index 3
            }
            // Perform sentiment analysis on the descriptions
            String sentiment = getSentimentAnalysis(descriptions);

            // Prepare the response with both video data and sentiment
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("data", videos);
            responseData.put("senti", sentiment);

            // Send back the response with both the data and sentiment
            getSender().tell(new SearchData(responseData), getSelf());


        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public static class SearchData
    {
        private final Map<String, Object> responseData;

        public SearchData(Map<String, Object> responseData)
        {
            this.responseData = responseData;

        }

        public String getVideos()
        {
            try {
                return new ObjectMapper().writeValueAsString(responseData);
            } catch (Exception e) {
                e.printStackTrace();
                return "{\"error\":\"Failed to serialize data\"}";
            }
        }
    }

    private int happyCount = 0;
    private int sadCount = 0;

    // Sentiment Analysis method
    public String getSentimentAnalysis(List<String> descriptions) {
        this.happyCount = 0;
        this.sadCount = 0;// Calculate the sentiment based on the counts
        double total = 0;

// Count happy and sad words/emojis/emoticons in each description
        for (String description : descriptions) {
            // Convert to lowercase for consistent matching
            String lowerDescription = description.toLowerCase();

            // Count words in happy and sad lists
            List<String> words = Arrays.asList(lowerDescription.split("\\s+")); // Split by whitespace to get words

            System.out.println(words);

            happyCount += (int) words.stream().filter(happyList::contains).count();
            sadCount += (int) words.stream().filter(sadList::contains).count();

            // Count happy and sad emojis
            happyCount += (int) happyEmojis.stream().filter(lowerDescription::contains).count();
            sadCount += (int) sadEmojis.stream().filter(lowerDescription::contains).count();

            happyCount += (int) happyEmoticons.stream().filter(lowerDescription::contains).count();
            sadCount += (int) sadEmoticons.stream().filter(lowerDescription::contains).count();
        }

        System.out.println(happyCount);
        System.out.println(sadCount);

        total = happyCount + sadCount;

        if (happyCount == 0 && sadCount == 0) {
            return ":-|"; // Neutral if no sentiment words found
        } else if ((happyCount / total) >= 0.7) {
            return ":-)"; // Happy
        } else if ((sadCount / total) >= 0.7) {
            return ":-("; // Sad
        } else {
            return ":-|"; // Neutral
        }
    }


}

