package examples;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

public class JsonGenerator extends Generator<String> {

    public JsonGenerator() {
        super(String.class);
    }

    private GenerationStatus status;

    private static final int MAX_WS_DEPTH = 3;
    private static final int MAX_RECURSION_DEPTH = 85;
    private static final int MIN_MEMBERS_DEPTH = 10;
    private static final int MIN_CHAR_DEPTH = 10;
    private static final int MIN_ELEMENTS_DEPTH = 10;

    private int currentDepth;
    private int currentwsDepth;

    private static final String[] whitespacevariants = {
            " ", "\n", "\r", "\t"
    };

    private static final String[] escapevariants = {
            "\"", "\\", "/", "b", "f", "n", "r", "t", "u"
    };

    @Override
    public String generate(SourceOfRandomness random, GenerationStatus status) {
        this.status = status;
        this.currentDepth = 0;
        this.currentwsDepth = 0;
        String input = generateElement(random).toString();
        double zufall;
        while (true) {
            zufall = random.nextDouble(0.0, 1.0);
            if (zufall <= 0.2) {
                input = mutateStringRandomly(input, random);
            } else {
                break;
            }
        }
        // System.out.println("Input: " + input);
        return input;
    }

    private String mutatateStringRandomly(String input, SourceOfRandomness random) {
        char[] chars = input.toCharArray();
        int rndpos = random.nextInt(chars.length);
        char rndchar = (char) (32 + random.nextInt(95));
        chars[rndpos] = rndchar;
        return new String(chars);
    }

    private String generateElement(SourceOfRandomness random) {

        String element = generateValue(random);
        String ws1 = generateWhitespace(random);
        String ws2 = generateWhitespace(random);
        return ws1 + element + ws2;

    }

    private String generateValue(SourceOfRandomness random) {
        String value;
        value = random.choose(Arrays.<Function<SourceOfRandomness, String>>asList(
                this::generateObject,
                this::generateArray,
                this::generateString,
                this::generateNumber,
                this::generateTRUE,
                this::generateFALSE,
                this::generateNULL)).apply(random);
        return value;
    }

    private String generateObject(SourceOfRandomness random) {

        String object;
        object = random.choose(Arrays.<Function<SourceOfRandomness, String>>asList(
                this::generateWhitespace,
                this::generateMembers)).apply(random);

        return "{" + object + "}";
    }

    private String generateArray(SourceOfRandomness random) {

        String array;
        array = random.choose(Arrays.<Function<SourceOfRandomness, String>>asList(
                this::generateWhitespace,
                this::generateElements)).apply(random);

        return "[" + array + "]";
    }

    private String generateString(SourceOfRandomness random) {

        String resultString;
        resultString = generateCharacters(random);
        return '"' + resultString + '"';
    }

    private String generateNumber(SourceOfRandomness random) {
        String zahl = generateInteger(random);
        String fraction = generateFraction(random);
        String exponent = generateExponent(random);
        return zahl + fraction + exponent;

    }

    private String generateTRUE(SourceOfRandomness random) {
        return "true";
    }

    private String generateFALSE(SourceOfRandomness random) {
        return "false";
    }

    private String generateNULL(SourceOfRandomness random) {
        return "null";
    }

    private String generateWhitespace(SourceOfRandomness random) {
        String whitespace;
        if (currentwsDepth >= MAX_WS_DEPTH || random.nextBoolean()) {
            whitespace = "";
        } else {
            currentwsDepth++;
            whitespace = random.choose(whitespacevariants) + generateWhitespace(random);
            currentwsDepth--;
        }
        return whitespace;
    }

    private String generateMembers(SourceOfRandomness random) {
        String member;
        if ((currentDepth >= MAX_RECURSION_DEPTH || random.nextBoolean()) && currentDepth >= MIN_MEMBERS_DEPTH) {
            member = generateMember(random);
        }

        else {
            currentDepth++;
            member = generateMember(random) + "," + generateMembers(random);
            currentDepth--;
        }
        return member;
    }

    private String generateMember(SourceOfRandomness random) {
        String ws1 = generateWhitespace(random);
        String string = generateString(random);
        String ws2 = generateWhitespace(random);
        String element = generateElement(random);
        return ws1 + string + ws2 + ":" + element;
    }

    private String generateElements(SourceOfRandomness random) {
        String elements;
        if ((currentDepth >= MAX_RECURSION_DEPTH || random.nextBoolean()) && currentDepth >= MIN_ELEMENTS_DEPTH) {
            elements = generateElement(random);
        } else {
            currentDepth++;
            elements = generateElement(random) + "," + generateElements(random);
            currentDepth--;
        }
        return elements;
    }

    private String generateCharacters(SourceOfRandomness random) {
        String character;
        if ((currentDepth >= MAX_RECURSION_DEPTH || random.nextBoolean()) && currentDepth >= MIN_CHAR_DEPTH) {
            character = "";
        } else {
            currentDepth++;
            character = generateCharacter(random) + generateCharacters(random);
            currentDepth--;
        }
        return character;
    }

    private String generateCharacter(SourceOfRandomness random) {
        String character;
        character = random.choose(Arrays.<Function<SourceOfRandomness, String>>asList(
                this::generateUnicode,
                this::generateBackslashEscape)).apply(random);
        return character;
    }

    private String generateUnicode(SourceOfRandomness random) {
        char randomChar;
        do {
            randomChar = (char) (random.nextInt(126 - 32) + 32);
        } while (randomChar == '"' || randomChar == '\\');
        return String.valueOf(randomChar);
    }

    private String generateBackslashEscape(SourceOfRandomness random) {
        String escape = generateEscape(random);
        return "\\" + escape;
    }

    private String generateEscape(SourceOfRandomness random) {
        String escape = random.choose(escapevariants);
        if ("u".equals(escape)) {
            String hex1 = generateHex(random);
            String hex2 = generateHex(random);
            String hex3 = generateHex(random);
            String hex4 = generateHex(random);
            return escape + hex1 + hex2 + hex3 + hex4;
        }
        return escape;

    }

    private String generateHex(SourceOfRandomness random) {
        String hex;
        hex = random.choose(Arrays.<Function<SourceOfRandomness, String>>asList(
                this::generateDigit,
                this::generateUpperCaseHex,
                this::generateLowerCaseHex)).apply(random);
        return hex;
    }

    private String generateDigit(SourceOfRandomness random) {
        int digit;
        digit = random.nextInt(10);
        return String.valueOf(digit);
    }

    private String generateUpperCaseHex(SourceOfRandomness random) {
        char randomChar;
        randomChar = (char) ('A' + random.nextInt(6));
        return String.valueOf(randomChar);
    }

    private String generateLowerCaseHex(SourceOfRandomness random) {
        char randomChar;
        randomChar = (char) ('a' + random.nextInt(6));
        return String.valueOf(randomChar);
    }

    private String generateOneNine(SourceOfRandomness random) {
        int randomnumber;
        randomnumber = 1 + random.nextInt(9);
        return String.valueOf(randomnumber);
    }

    private String generateInteger(SourceOfRandomness random) {

        int choice = random.nextInt(4);

        switch (choice) {
            case 0:
                return generateDigit(random);
            case 1:
                return generateOneNine(random) + generateDigits(random);
            case 2:
                return "-" + generateDigit(random);
            case 3:
                return "-" + generateOneNine(random) + generateDigits(random);
            default:
                return "";
        }

    }

    private String generateDigits(SourceOfRandomness random) {
        if (currentDepth >= MAX_RECURSION_DEPTH) {
            return generateDigit(random);
        }
        String digits;
        if (random.nextBoolean()) {

            digits = generateDigit(random);
        } else {
            currentDepth++;
            digits = generateDigit(random) + generateDigits(random);
            currentDepth--;
        }
        return digits;
    }

    private String generateFraction(SourceOfRandomness random) {
        String fraction;
        if (random.nextBoolean()) {
            fraction = "";
        } else {
            fraction = "." + generateDigits(random);
        }
        return fraction;
    }

    private String generateExponent(SourceOfRandomness random) {
        int choice = random.nextInt(3);

        switch (choice) {
            case 0:
                return "";
            case 1:
                return "E" + generateSign(random) + generateDigits(random);
            case 2:
                return "e" + generateSign(random) + generateDigits(random);
            default:
                return "";
        }
    }

    private String generateSign(SourceOfRandomness random) {
        int choice = random.nextInt(3);

        switch (choice) {
            case 0:
                return "";
            case 1:
                return "+";
            case 2:
                return "-";
            default:
                return "";
        }
    }

}
