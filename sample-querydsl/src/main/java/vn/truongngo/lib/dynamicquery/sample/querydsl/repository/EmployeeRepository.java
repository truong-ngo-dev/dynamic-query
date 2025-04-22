package vn.truongngo.lib.dynamicquery.sample.querydsl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.truongngo.lib.dynamicquery.sample.querydsl.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
