package org.opendaylight.controller.yaon.yaonCFENorthbound;

import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;


class JsonParsing{

        public static JSONObject Data(StringBuilder str){

                JSONObject jsonObject=null;
                try{
                	JSONParser jsonParser = new JSONParser();
                	jsonObject = (JSONObject) jsonParser.parse(str.toString());
                }
                catch(Exception e){
                	System.out.println("Exception occurred during Json Parsing!! "+e);
                }
                return jsonObject;
        }
}

