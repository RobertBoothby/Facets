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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This is the base class for all faceted types. A faceted type is one that has one or more facets.
 * Sub-implementation define the mechanisms for storing and manipulating facet data. Initially we'll be working with
 * Java Objects and a TypedMap although others can be added. I'm also going to introduce Serializable specialisations
 * too.
 * <p>&#169; 2017 Robert Boothby.</p>
 *
 * @author robertboothby
 */
public abstract class Faceted<T extends Faceted<T, V>, V> {


    //Cache only as facets can always be recreated.
    private final transient Map<FacetKey, Object> facetCache = new WeakHashMap<>();

    @SuppressWarnings("unchecked")
    public <U extends Facet<U, V>> U addFacet(Class<U> facetClass, Supplier<V> facetData) {

        V initialFacetData = facetData.get();

        //Need to create a setup proxy so that we can invoke the getFacetIdentifier method as part of initialisation.
        String facetIdentifier = getFacetSetupProxy(facetClass, initialFacetData).getFacetIdentifier();

        // Now we have the facet identifier we can complete initialisation and create the actual facet instance.
        return initialiseFacet(facetClass, initialFacetData, facetIdentifier);
    }

    @SuppressWarnings("unchecked")
    private <U extends Facet<U, V>> U getFacetSetupProxy(Class<U> facetClass, V initialFacetData) {
        return (U) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{facetClass}, (proxy, method, args) -> {
            if (method.getName().equals("getFacetData")) { //This allows us to access the facet data if needed for a facet identifier.
                return initialFacetData;
            } else if (method.isDefault()) {
                return bindDefaultMethod(proxy, method, args); // this lets us call whatever default methods we want.
            } else {
                throw new UnsupportedOperationException( // this stops us calling non-default methods which might cause problems before initialisation is complete.
                        "Can only call default methods during initialisation - make sure that you have a " +
                                "default implementation of getFacetIdentifioer() and any methods it invokes directly or " +
                                "indirectly such as: " + method.getName());
            }
        });
    }


    private <U extends Facet<U, V>> U initialiseFacet(Class<U> facetClass, V initialFacetData, String facetIdentifier) {

        //TODO add further initialisation checks that all necessary methods exist etc.
        if (hasFacetData(facetClass, facetIdentifier)) {
            throw new UnsupportedOperationException("This facet already exists.");
        }

        U facet = getFacetProxy(facetClass, facetIdentifier);

        addFacetData(facetClass, facetIdentifier, initialFacetData);

        return facet;
    }

    @SuppressWarnings("unchecked")
    private <U extends Facet<U, V>> U getFacetProxy(Class<U> facetClass, String facetIdentifier) {
        return (U) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{facetClass}, (proxy, method, args) -> {
                if (method.getName().equals("getFacetData")) {
                    return getFacetData(facetClass, facetIdentifier).orElseThrow(RuntimeException::new);
                } else if (method.isDefault()) {
                    return bindDefaultMethod(proxy, method, args);
                } else {
                    return this.getClass().getMethod(method.getName(), method.getParameterTypes()).invoke(this, args);
                }
            });
    }

    private static Object bindDefaultMethod(Object proxy, Method method, Object[] args) throws Throwable {
        final Class<?> declaringClass = method.getDeclaringClass();
        Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);
        constructor.setAccessible(true);
        return
                constructor.newInstance(declaringClass, MethodHandles.Lookup.PRIVATE)
                        .unreflectSpecial(method, declaringClass)
                        .bindTo(proxy)
                        .invokeWithArguments(args);
    }

    public <U extends UniqueFacet<U, V>> Optional<U> getFacet(Class<U> facetClass){
        return getFacet(facetClass, new FacetKey(facetClass));
    }

    public <U extends Facet<U, V>> Optional<U> getFacet(Class<U> facetClass, String facetIdentifier)   {
        return getFacet(facetClass, new FacetKey(facetClass, facetIdentifier));
    }

    @SuppressWarnings("unchecked")
    private <U extends Facet<U, V>> Optional<U> getFacet(Class<U> facetClass, FacetKey key) {
        return Optional.ofNullable((
                U)facetCache.computeIfAbsent(key,
                (facetKey -> this.getFacetProxy(facetClass, facetKey.getFacetIdentifier()))));
    }

    public <U extends UniqueFacet<U, V>> boolean hasFacet(Class<U> facetClass) {
        return hasFacet(facetClass, new FacetKey(facetClass));
    }

    public <U extends UniqueFacet<U, V>> boolean hasFacet(Class<U> facetClass, String facetIdentifier) {
        return hasFacet(facetClass, new FacetKey(facetClass, facetIdentifier));
    }

    private <U extends Facet<U, V>> boolean hasFacet(Class<U> facetClass, FacetKey facetKey) {
        return Optional.ofNullable(facetCache.get(facetKey))
                .map(Objects::nonNull)
                .orElse(hasFacetData(facetClass, facetKey.getFacetIdentifier()));
    }

    protected abstract <U extends Facet<U, V>> boolean hasFacetData(Class<U> facetClass, String facetIdentifier);

    protected abstract <U extends Facet<U, V>> Optional<V> getFacetData(Class<U> facetClass, String facetIdentifier);

    protected abstract <U extends Facet<U, V>> void addFacetData(Class<U> facetClass, String facetIdentifier, V facetData);

    @SuppressWarnings("unchecked")
    public T getBaseFaceted(){
        return (T)this;
    }

    /**
     * Key used to identify a facet within a Faceted instance. This should only be the concern of sub-types of
     * Faceted.
     */
    protected final class FacetKey {
        private final String facetClass;
        private final String facetIdentifier;

        /**
         * Default constuctor for unique facets.
         *
         * @param facetClass the class of the facet.
         */
        public FacetKey(Class<? extends UniqueFacet> facetClass) {
            this.facetClass = facetClass.toString();
            this.facetIdentifier = facetClass.toString();
        }

        /**
         * More general constructor for non-unique facets.
         *
         * @param facetClass      the class of the facet.
         * @param facetIdentifier the identifier of the facet.
         */
        public FacetKey(Class<? extends Facet> facetClass, String facetIdentifier) {
            this.facetClass = facetClass.toString();
            this.facetIdentifier = facetIdentifier;
        }

        public String getFacetClass() {
            return facetClass;
        }

        public String getFacetIdentifier() {
            return facetIdentifier;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            FacetKey facetKey = (FacetKey) o;
            return Objects.equals(facetClass, facetKey.facetClass) &&
                    Objects.equals(facetIdentifier, facetKey.facetIdentifier);
        }

        @Override
        public int hashCode() {
            return Objects.hash(facetClass, facetIdentifier);
        }

        /**
         * Convenience toString() method for debugging purposes, do not attempt to use this as a key in any concrete
         * implementations of Faceted as it includes the Object Identity and will not generate a key that will work as
         * might be expeceted. The reason foe this is that the implementation details of FacetKey may need to change
         * over time, adding extra fields and this would break any implementation relying on this method. Faceted
         * Implementations need to use the FacetKey and its data directly to minimise the chance of a breaking change.
         *
         * @return
         */
        @Override
        public final String toString() {
            final StringBuilder sb = new StringBuilder("FacetKey{");
            sb.append("facetClass='").append(facetClass).append('\'');
            sb.append(", facetIdentifier='").append(facetIdentifier).append('\'');
            sb.append(", instanceId='").append(super.toString()).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }

}
