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
import org.junit.Test;
import org.junit.runners.model.TestTimedOutException;

import java.beans.IntrospectionException;
import java.beans.Introspector;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * <p>&#169; 2017 Robert Boothby.</p>
 *
 * @author robertboothby
 */
public class SimpleFacetTest {

    @Test
    public void shouldAddAFacet() {
        //Given
        FacetedPerson person = new FacetedPerson();

        person.setName("John");

        //When
        Driver driver =  person.addFacet(Driver.class, Driver.initialise("ABCDEF"));
        //Then
        assertThat(driver.getLicenceNumber(), is("ABCDEF")) ;
        assertThat(driver.getName(), is("John"));

        //When
        driver.setName("James");

        driver.driving();

        //Then
        assertThat(driver.getName(), is("James"));
        assertThat(person.getName(), is("James"));

    }

    @Test
    public void shouldNotAddExtraFacet() {
        //Given
        FacetedPerson person = new FacetedPerson();

        person.setName("John");

        Driver driver =  person.addFacet(Driver.class, Driver.initialise("ABCDEF"));
        //When
        try{
            person.addFacet(Driver.class, Driver.initialise("BCDEFG"));
            fail();
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
            //Then check no change to driver licence.
            assertThat(driver.getLicenceNumber(), is("ABCDEF"));
        }

        try {
            System.out.println(Introspector.getBeanInfo(Driver.class).getBeanDescriptor().toString());
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }
    }
}
