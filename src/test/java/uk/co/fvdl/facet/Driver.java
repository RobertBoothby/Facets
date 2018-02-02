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

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.function.Supplier;

/**
 * <p>&#169; 2017 Robert Boothby.</p>
 *
 * @author robertboothby
 */
public interface Driver extends UniqueFacet<Driver, ObjectNode>, Person {

    //TODO automate creation of getters and setters.

    default String getLicenceNumber(){
        return getFacetData().get("licenceNumber").asText();
    }

    default void setLicenceNumber(String licenceNumber) {
        getFacetData().put("licenceNumber", licenceNumber);
    }

    default void driving() {
        System.out.println(getName() + " : " + getLicenceNumber() + " is driving...");
    }

    static Supplier<ObjectNode> initialise(String licenceNumber){

        return () ->  {
            return JsonNodeFactory.instance.objectNode().put("licenceNumber", licenceNumber);
        };

    }
}
