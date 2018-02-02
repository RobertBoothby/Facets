/**
 * This package contains the core constructs of facets - functional extensions of core faceted classes. Facets are
 * implemented as interfaces with default methods that interact with one or more commonly agreed methods of the faceted
 * type. Facets are intended to be dynamic and Faceted classes can gain and lose facets through time. Facets while
 * superficially similar to Erich Gamma's Extension Objects are not the same thing. For example a Facet interface is
 * defined as an extension of a common super interface for the Faceted type and a facet can be dynamically added to the
 * Faceted object even though the definition of the Faceted Object may have no awareness.
 * @author robertboothby
 */
package uk.co.fvdl.facet;