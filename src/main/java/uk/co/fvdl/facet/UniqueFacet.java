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

/**
 * <p>&#169; 2017 Robert Boothby.</p>
 *
 * @author robertboothby
 */
public interface UniqueFacet<T extends UniqueFacet<T, U>, U> extends Facet<T, U> {


    /**
     * Default implementation that ensures that extensions of this facet is unique on a given faceted instance.
     * It is not recommended that you override this method without a deep understanding of the Faceted mechanisms.
     * @return a facet identifier that ensures the uniqueness.
     */
    default String getFacetIdentifier(){
        return this.getClass().toString();
    }
}
