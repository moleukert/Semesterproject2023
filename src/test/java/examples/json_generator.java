
import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public class json_generator extends Generator<String> {
    public json_generator() {
        super(String.class);
    }

    private static final int MAX_DEPTH = 100;
    private Set<String> indentifiers;

    private int depth;

    @Override
    public String generate(SourceOfRandomness random, GenerationStatus __status__) {
        this.depth = 0;
        this.indentifiers = new HashSet<>();
        return generate_element(random).toString();
    }

    public String generate_element(SourceOfRandomness random) {
        return generate_ws(random) + generate_value(random) + generate_ws(random);
    }

    public String generate_value(SourceOfRandomness random) {
        String result;

        result = random.choose(Arrays.<Function<SourceOfRandomness, String>>asList(
                this::generate_object,
                this::generate_array,
                this::generate_string,
                this::generate_number,
                this::generate_TRUE,
                this::generate_FALSE,
                this::generate_NULL)).apply(random);

        return result;
    }

    public String generate_object(SourceOfRandomness random) {
        String result;

        result = random.choose(Arrays.<Function<SourceOfRandomness, String>>asList(
                this::generate_members,
                this::generate_ws)).apply(random);

        return '{' + result + '}';

    }

    public String generate_array(SourceOfRandomness random) {
        String result;

        result = random.choose(Arrays.<Function<SourceOfRandomness, String>>asList(
                this::generate_elements,
                this::generate_ws)).apply(random);

        return '[' + result + ']';

    }

    public String generate_string(SourceOfRandomness random) {
        String result;

        result = generate_characters(random);

        return '"' + result + '"';

    }

    public String generate_number(SourceOfRandomness random) {
        String result;

        result = generate_integer(random) +
                generate_fraction(random) +
                generate_exponent(random);

        return result;

    }

    public String generate_members(SourceOfRandomness random) {
        String result;

        result = generate_member(random);

        if (random.nextBoolean()) {
            result += ',' + generate_members(random);
        }
        return result;
    }

    public String generate_member(SourceOfRandomness random) {

        return generate_ws(random) +
                generate_string(random) +
                generate_ws(random) + ':' +
                generate_element(random);
    }

    public String generate_elements(SourceOfRandomness random) {
        String result;

        result = generate_element(random);

        if (random.nextBoolean()) {
            result += ',' + generate_elements(random);
        }
        return result;
    }

    public String generate_element(SourceOfRandomness random) {

        return generate_ws(random) +
                generate_value(random) +
                generate_ws(random);
    }

    public String generate_characters(SourceOfRandomness random) {
        String result;

        if (random.nextBoolean()) {
            result = generate_character(random) + generate_characters(random);
        } else {
            result = "";
        }
        return result;
    }

    public String generate_character(SourceOfRandomness random) {
        String result;

        if (random.nextBoolean()) {
            // generate any char except " or / or cntrl chars
            result = "";
        } else {
            result = "\\" + generate_escape(random);
        }
        return result;
    }

}
