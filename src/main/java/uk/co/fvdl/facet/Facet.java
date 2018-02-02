/*
Copyright 2017 Robert Boothby

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

/**
 * Interface for all facets of a Faceted type. A facet adds data and / or functionality to the Faceted type dynamically.
 * <p>&#169; 2017 Robert Boothby.</p>
 *
 * @author robertboothby
 */

import java.util.function.Consumer;

public interface Facet<T extends Facet<T, U>, U> {


    /**
     * Key method used to access the facet related data to be used in the default methods of a Facet. When a facet is
     * accessed from a Faceted a the dynamic proxy generated will provide the implementation.
     * @return the facet related data.
     */
    U getFacetData();

    /**
     * Method invoked during creation of a Facet to establish the data associated with a facet roughly equivalent to a
     * constructor - it is expected to be implemented as a default method in the specific facet.
     * @param setup The setup instructions for a facet as a Consumer.
     * @return This returns the facet once it has been constructed for storage within the Faceted instance.
     */
    U setupFacet(Consumer<U> setup);

    /**
     * Default implementation of how a facet is identified for storage and caching purposes and can be superseded in the
     * sub-types to overcome the limitation of only one facet of a particular class per Faceted. For example if we want
     * a person to belong to two football teams using the same identifier we can override this to provide a qualifier
     * based on the related Facet data.
     * @return a qualifying identifier for the facet..
     */
    String getFacetIdentifier();

}
