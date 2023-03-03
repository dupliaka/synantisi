package org.kie.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.kie.domain.Person;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PersonRepository  implements PanacheRepository<Person> {
}
