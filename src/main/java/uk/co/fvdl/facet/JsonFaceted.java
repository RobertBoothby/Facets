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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Optional;

/**
 * <p>&#169; 2017 Robert Boothby.</p>
 *
 * @author robertboothby
 */
public class JsonFaceted<T extends JsonFaceted<T, V>, V extends JsonNode>  extends Faceted<T, V> {

    private static final String FACET_NODE = "$facets$";
    protected static final JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
    protected final ObjectNode baseNode;

    public JsonFaceted() {
        baseNode = jsonFactory.objectNode();
        baseNode.putObject(FACET_NODE);
    }

    public JsonFaceted(ObjectNode baseNode) {
        this.baseNode = baseNode;
    }

    @Override
    protected <U extends Facet<U, V>> boolean hasFacetData(Class<U> facetClass, String facetIdentifier) {
        return getFacetData(facetClass, facetIdentifier).isPresent();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <U extends Facet<U, V>> Optional<V> getFacetData(Class<U> facetClass, String facetIdentifier) {
        final JsonNode candidateNode = baseNode.path(FACET_NODE).path(facetClass.getName()).path(facetIdentifier);
        if(candidateNode.isMissingNode()){
            return Optional.empty();
        } else {
            return Optional.of((V)candidateNode);
        }
    }

    @Override
    protected <U extends Facet<U, V>> void addFacetData(Class<U> facetClass, String facetIdentifier, V facetData) {
        baseNode.with(FACET_NODE).with(facetClass.getName()).set(facetIdentifier, facetData);

    }
}
