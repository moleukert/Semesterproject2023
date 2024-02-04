package examples;


import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

import org.json.simple.JSONValue;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.ContentHandler;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.pholser.junit.quickcheck.From;
import examples.JsonGenerator;
import edu.berkeley.cs.jqf.fuzz.JQF;
import edu.berkeley.cs.jqf.fuzz.Fuzz;
import org.junit.Assume;
import org.junit.Assert;
import org.junit.runner.RunWith;

import java.io.StringWriter;


@RunWith(JQF.class)
public class json_simple_test {

    @Fuzz
    public void fuzzJSONParser(@From(JsonGenerator.class) String input) {
        
        // Object, JSONArray, List, Map aus input generieren
        Object obj=JSONValue.parse(input);
        JSONArray array=(JSONArray)obj;
        List list = new ArrayList();
		list.add(input);
        Map map = new HashMap();
		map.put("array1", (List)obj);

        // zurueck in Strings
        String x = array.toString();
        String y = obj.toString();
        String z = JSONObject.toJSONString(map);
        String a = JSONArray.toJSONString(list);
        

        JSONParser parser = new JSONParser();
		try{
			parser.parse(input);

		}
		catch(ParseException e){
            Assume.assumeNoException(e);
		}
        
        // Mit custom contenthandler parsen
        ContentHandler myHandler = new ContentHandler() {
            
            public boolean endArray() throws ParseException {return true;}
			public void endJSON() throws ParseException {}
			public boolean endObject() throws ParseException {return true;}
			public boolean endObjectEntry() throws ParseException {return true;}
			public boolean primitive(Object value) throws ParseException {return true;}
            public boolean startArray() throws ParseException {return true;}
			public void startJSON() throws ParseException {}
			public boolean startObject() throws ParseException {return true;}
			public boolean startObjectEntry(String key) throws ParseException {return true;}	
		};
		try{
            parser.parse(input, myHandler);
		}
		catch(ParseException e){
            Assume.assumeNoException(e);
		}
        
        // ContainerFactory containerFactory = new ContainerFactory(){
        // 	public List creatArrayContainer() {
        // 		return new LinkedList();
        // 	}
        
        // 	public Map createObjectContainer() {
        // 		return new LinkedHashMap();
        // 	}
            
        // };
        
        // try{
        // 	Map json = (Map)parser.parse(input, containerFactory);
        // 	Iterator iter = json.entrySet().iterator();
            
        // }
        // catch(ParseException e){
        //     Assume.assumeNoException(e);
        // }
    }
}
