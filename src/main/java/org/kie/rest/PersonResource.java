package org.kie.rest;

import io.quarkus.hibernate.orm.rest.data.panache.PanacheRepositoryResource;
import io.quarkus.rest.data.panache.ResourceProperties;
import org.kie.domain.Person;
import org.kie.persistence.PersonRepository;

@ResourceProperties(path = "persons")
public interface PersonResource  extends PanacheRepositoryResource<PersonRepository, Person, Long> {

}
