package it.skim;

import com.google.gson.Gson;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.time.SUTime;
import edu.stanford.nlp.time.TimeAnnotations;
import edu.stanford.nlp.time.TimeAnnotator;
import edu.stanford.nlp.time.TimeExpression;
import edu.stanford.nlp.util.CoreMap;
import org.joda.time.DateTime;
import org.joda.time.Instant;

import java.util.*;

import static spark.Spark.*;

/**
 * Created by miro on 25/04/2017.
 */
public class Main {
    private static Gson gson = new Gson();

    private static AnnotationPipeline pipeline = getPipeline();

    public static class ResolvedTimePojo {
        public String original, resolved;

        public ResolvedTimePojo(String original, String resolved) {
            this.original = original;
            this.resolved = resolved;
        }
    }

    private static class Input {
        String text;

        public Input(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    private static AnnotationPipeline getPipeline() {
        Properties props = new Properties();
        AnnotationPipeline pipeline = new AnnotationPipeline();
        pipeline.addAnnotator(new TokenizerAnnotator(false));
        pipeline.addAnnotator(new WordsToSentencesAnnotator(false));
        pipeline.addAnnotator(new POSTaggerAnnotator(false));
        pipeline.addAnnotator(new TimeAnnotator("sutime", props));
        return pipeline;
    }

    public static List<ResolvedTimePojo> annotate(String text) {
        return annotate(text, new DateTime());
    }

    public static List<ResolvedTimePojo> annotate(String text, DateTime relativeTo) {
        Annotation annotation = new Annotation(text);
        // this is the date of the document- temporal expressions like "tomorrow" are resolved relative to it
        annotation.set(CoreAnnotations.DocDateAnnotation.class, relativeTo.toString("Y-M-d'T'HH:MM:ss"));
        pipeline.annotate(annotation);
        List<CoreMap> timexAnnsAll = annotation.get(TimeAnnotations.TimexAnnotations.class);

        List<ResolvedTimePojo> res = new ArrayList<>(timexAnnsAll.size());
        for (CoreMap cm : timexAnnsAll) {
            SUTime.Temporal temporal = cm.get(TimeExpression.Annotation.class).getTemporal();
            String iso;

            if (temporal.getTimexType().equals(SUTime.TimexType.DURATION)) {
                long duration = temporal.getDuration().getJodaTimeDuration().getMillis();
                iso = relativeTo.minus(duration).toString();
            } else {
                try {
                    iso = temporal.getRange().begin().toISOString();
                } catch (Exception e) {
                    iso = temporal.toISOString();
                }
            }
            res.add(new ResolvedTimePojo(cm.toString(), iso));
        }
        return res;
    }


    public static void main(String[] args) {
        port(3000);
        post("/", ((request, response) -> {
            response.header("Content-Type", "application/json");
            Input input = gson.fromJson(request.body(), Input.class);
            return annotate(input.getText());
        }), gson::toJson);

    }
}
