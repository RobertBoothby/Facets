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
package uk.co.fvdl.facet;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * <p>&#169; 2017 Robert Boothby.</p>
 *
 * @author robertboothby
 */
public class FacetedPerson extends JsonFaceted<FacetedPerson, ObjectNode> implements Person {


    @Override
    public String getName() {
        return this.baseNode.get("name").asText();
    }

    @Override
    public void setName(String name) {
        this.baseNode.put("name", name);
    }
}