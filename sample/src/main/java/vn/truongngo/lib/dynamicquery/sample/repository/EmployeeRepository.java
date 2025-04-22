package vn.truongngo.lib.dynamicquery.sample.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.truongngo.lib.dynamicquery.sample.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
