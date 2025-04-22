package vn.truongngo.lib.dynamicquery.sample.jooq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.truongngo.lib.dynamicquery.sample.jooq.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
