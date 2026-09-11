package sn.sdley.springbootstarter0.repositories;

import org.springframework.data.repository.CrudRepository;
import sn.sdley.springbootstarter0.entities.Category;

public interface CategoryRepository extends CrudRepository<Category, Long> {
}
