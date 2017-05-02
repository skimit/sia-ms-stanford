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
        DateTime relativeTo = new DateTime(2010, 10, 10, 10, 10, 10);

        String[] texts = new String[]{
                "yesterday",
                "last year",
                "a minute ago",
                "since last week",
                "this week",
                "a week ago",
                "in the past 2 days", // TODO enable and fix
//                "since Tuesday"
        };
        String[] expected = new String[]{
                "2010-10-09T10:10:10.000", // same time yesterday
                "2009-01-01", // start of last year, no time specified
                "2010-10-10T10:09:10.000",
                "2010-09-27",// it is a Sunday, this goes back to start of previous week
                "2010-10-04", // the Monday of this week
                "2010-10-03T10:10:10.000", // exactly 7 day ago, same time last Sunday
                "2010-10-08T10:10:10.000",
//                "2010-10-03", // known failure: CoreNLP think this refers to next Tuesday (12 Oct)
        };

        for (int i = 0; i < texts.length; i++) {
            System.out.println(texts[i] + " --> " + expected[i]);
            List<Main.ResolvedTimePojo> res = Main.annotate(texts[i], relativeTo);
            assertTrue(res.size() == 1);
            assertTrue(res.get(0).resolved.startsWith(expected[i]));// ignore time zones
        }
    }
}
