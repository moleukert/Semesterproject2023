package examples;


import mjson.Json;
import static mjson.Json.*;


import org.junit.Assert;
import java.util.Iterator;
import java.util.Map;
import org.junit.runner.notification.Failure;

import com.pholser.junit.quickcheck.From;
import examples.JsonGenerator;
import edu.berkeley.cs.jqf.fuzz.JQF;
import edu.berkeley.cs.jqf.fuzz.Fuzz;
import org.junit.Assume;
import org.junit.runner.RunWith;

@RunWith(JQF.class)
public class mjson_test {

    @Fuzz
    public void fuzzJSONParser(@From(JsonGenerator.class) String input) {

        
    
        try {
            Json t = Json.make(input);
            Json t2 = Json.factory().string(input);
            Json tdup = t.dup();
            Assert.assertEquals(t, tdup);
            Assert.assertEquals(t, t2);
            
            Json.read(input);

            // Json obj  = object();
            // Json s = make(input);
            // obj.set("test", s);
            // Assert.assertTrue(obj == s.up());
            

        } catch (MalformedJsonException e) {
            Assume.assumeNoException(e);
        }

    }
}