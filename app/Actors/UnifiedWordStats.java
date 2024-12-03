/*package Actors;

import org.apache.pekko.actor.AbstractActor;
import org.apache.pekko.actor.ActorContext;
import org.apache.pekko.actor.typed.ActorRef;
import org.apache.pekko.actor.typed.ActorSystem;
import org.apache.pekko.actor.typed.javadsl.AbstractBehavior;
import org.apache.pekko.actor.typed.javadsl.Behaviors;
import org.apache.pekko.actor.typed.javadsl.Receive;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UnifiedWordStatsActor extends AbstractActor {

    // Constructor
    private UnifiedWordStatsActor(ActorContext<ProcessVideoDescriptions> context) {
        super(context);
    }

    // Message class to process the video descriptions
    public static class ProcessVideoDescriptions {
        private final List<String> descriptions;

        public ProcessVideoDescriptions(List<String> descriptions) {
            this.descriptions = descriptions;
        }

        public List<String> getDescriptions() {
            return descriptions;
        }
    }

    // Message class to return the word statistics
    public static class WordStatsResult {
        private final Map<String, Long> wordStats;

        public WordStatsResult(Map<String, Long> wordStats) {
            this.wordStats = wordStats;
        }

        public Map<String, Long> getWordStats() {
            return wordStats;
        }
    }

    // Factory method to create the actor
    public static Behaviors.Receive<ProcessVideoDescriptions> create() {
        return Behaviors.setup(UnifiedWordStatsActor::new);
    }

    // The actor's receive method
    @Override
    public Receive<ProcessVideoDescriptions> createReceive() {
        return newReceiveBuilder()
                .onMessage(ProcessVideoDescriptions.class, this::onProcessVideoDescriptions)
                .build();
    }

    // Method to process video descriptions and calculate word statistics
    private Behavior<ProcessVideoDescriptions> onProcessVideoDescriptions(ProcessVideoDescriptions message) {
        List<String> descriptions = message.getDescriptions();

        // Process the descriptions to calculate word stats
        Map<String, Long> partialResult = descriptions.stream()
                .flatMap(description -> Stream.of(description.toLowerCase().split("\\W+")))
                .filter(word -> !word.isEmpty())
                .collect(Collectors.groupingBy(word -> word, Collectors.counting()));

        // Sort the word statistics by count in descending order
        List<Map.Entry<String, Long>> entryList = new ArrayList<>(partialResult.entrySet());
        entryList.sort((entry1, entry2) -> Long.compare(entry2.getValue(), entry1.getValue()));

        // Maintain insertion order by using LinkedHashMap
        LinkedHashMap<String, Long> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Long> entry : entryList) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        // Return the sorted word statistics
        getContext().getLog().info("Word Statistics: {}", sortedMap);
        // Assuming the word stats are being returned to the sender (the actor system)
        ActorRef<WordStatsResult> sender = getContext().getSelf();  // This can be any sender actor
        sender.tell(new WordStatsResult(sortedMap));

        return this;
    }
}*/

