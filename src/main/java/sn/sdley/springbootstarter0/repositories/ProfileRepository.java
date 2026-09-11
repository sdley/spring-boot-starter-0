package sn.sdley.springbootstarter0.repositories;

import org.springframework.data.repository.CrudRepository;
import sn.sdley.springbootstarter0.entities.Profile;

public interface ProfileRepository extends CrudRepository<Profile, Long> {
}
