package examples;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.pholser.junit.quickcheck.From;
import examples.JsonGenerator;
import edu.berkeley.cs.jqf.fuzz.JQF;
import edu.berkeley.cs.jqf.fuzz.Fuzz;
import org.junit.Assume;
import org.junit.runner.RunWith;

@RunWith(JQF.class)
public class gson_test {

    private Gson gson = new Gson();

    @Fuzz
    public void fuzzJSONParser(@From(JsonGenerator.class) String input) {
        try {
            gson.fromJson(input, Object.class);
        } catch (JsonSyntaxException e) {
            Assume.assumeNoException(e);
        } catch (JsonIOException e) {
            Assume.assumeNoException(e);
        }
    }
}
