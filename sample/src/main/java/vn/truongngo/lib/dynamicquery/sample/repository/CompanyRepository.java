package vn.truongngo.lib.dynamicquery.sample.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import vn.truongngo.lib.dynamicquery.sample.entity.Company;

public interface CompanyRepository extends JpaRepository<Company, Long>, QuerydslPredicateExecutor<Company> {
}
