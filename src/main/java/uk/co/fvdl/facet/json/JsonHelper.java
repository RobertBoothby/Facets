/*
Copyright 2014 Robert Boothby

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package uk.co.fvdl.facet.json;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Optional;

/**
 * Helper class for working with Jackson databind types.
 * @author robertboothby
 */
public class JsonHelper {

    public static Optional<JsonNode> getNode(JsonNode jsonNode, String ... relativeAddress){
        for (int i = 0; jsonNode != null && i < relativeAddress.length; i++){
            String nodeName = relativeAddress[i];
            jsonNode = jsonNode.get(nodeName);
            if(jsonNode != null){
                if(nodeName.contains("[")){ //Array address.
                    if(jsonNode.isArray()){
                        try {
                            int index = Integer.valueOf(nodeName.substring(nodeName.indexOf('[') + 1, nodeName.indexOf(']')));
                            jsonNode = jsonNode.get(index);
                        } catch (Exception e) {
                            //Couldn't parse the index for whatever reason.
                            jsonNode = null;
                        }
                    } else {
                        jsonNode = null; //Can't resolve array address.
                    }
                }
            }
        }
        return Optional.ofNullable(jsonNode);
    }



}
