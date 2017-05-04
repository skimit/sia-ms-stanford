import it.skim.Main;
import org.joda.time.DateTime;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by miro on 20/04/2017.
 */
public class TestTemporalExressionsParser {
    @Test
    public void testParsingOfTemporalExpressions() throws IOException {
        // Sunday, 10 Oct 2010
        DateTime relativeTo = new DateTime(2010, 10, 10, 10, 10, 10);

        String[] texts = new String[]{
                "yesterday",
                "last year",
                "a minute ago",
                "since last week",
                "this week",
                "a week ago",
                "in the past 2 days",
                "since Tuesday",
                "since 6 Oct",
                "this morning",
                "yesterday afternoon",
                "since Tuesday night",
                "next Tuesday night"
        };
        String[] expected = new String[]{
                "2010-10-09T10:10:10", // same time on previous day
                "2009-01-01", // start of last year, no time specified
                "2010-10-10T10:09:10",
                "2010-09-27",// it is a Sunday, this goes back to start of previous week
                "2010-10-04", // the Monday of that week
                "2010-10-03T10:10:10", // exactly 7 day ago, same time last Sunday
                "2010-10-08T10:10:10",
                "2010-10-12", // TODO known failure: CoreNLP think this refers to the following Tuesday (12 Oct),
                //whereas it should be the previous Tuesday (5 Oct)
                "2010-10-06",
                "2010-10-10T06:00:00",
                "2010-10-09T10:10:10", // TODO known failure: "afternoon" is ignored, refers to same time on previous day
                "2010-10-05T19:00:00", // this correctly resolved to the Tuesday past, 5 Oct
                "2010-10-12T19:00:00",
        };

        for (int i = 0; i < texts.length; i++) {
            List<Main.ResolvedTimePojo> res = Main.annotate(texts[i], relativeTo);
            assertTrue(res.size() == 1);
            String actual = res.get(0).resolved;
            System.out.println(texts[i] + " --> " + expected[i] + " --> " + actual);
            assertTrue(actual.startsWith(expected[i]));// ignore time zones
        }
    }
}
