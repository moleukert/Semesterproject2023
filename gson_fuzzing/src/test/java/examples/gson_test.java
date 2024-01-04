package examples;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.ToNumberPolicy;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.JsonParseException;
import com.pholser.junit.quickcheck.From;
import examples.JsonGenerator;
import edu.berkeley.cs.jqf.fuzz.JQF;
import edu.berkeley.cs.jqf.fuzz.Fuzz;
import org.junit.Assume;
import org.junit.runner.RunWith;

@RunWith(JQF.class)
public class gson_test {

    private Gson gson = new Gson();
    private JsonParser parser = new JsonParser();
    private Gson custom = new GsonBuilder()
            .setLenient()
            .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
            .create();

    @Fuzz
    public void fuzzJSONParser(@From(JsonGenerator.class) String input) {
        // test standard deserialization with gson
        try {
            gson.fromJson(input, Object.class);
        } catch (JsonSyntaxException e) {
            Assume.assumeNoException(e);
        } catch (JsonIOException e) {
            Assume.assumeNoException(e);
        }
        // test standard parsing to Tree with JsonParser
        try {
            parser.parseString(input);
        } catch (JsonParseException e) {
            Assume.assumeNoException(e);
        }
        // test deserialization with custom gson
        try {
            custom.fromJson(input, Object.class);
        } catch (JsonSyntaxException e) {
            Assume.assumeNoException(e);
        } catch (JsonIOException e) {
            Assume.assumeNoException(e);
        }

    }
}
