package sn.sdley.springbootstarter0.repositories;

import org.springframework.data.repository.CrudRepository;
import sn.sdley.springbootstarter0.entities.User;

public interface UserRepository extends CrudRepository<User, Long> {

}
