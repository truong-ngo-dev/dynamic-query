package vn.truongngo.lib.dynamicquery.sample.querydsl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import vn.truongngo.lib.dynamicquery.sample.querydsl.entity.Company;

public interface CompanyRepository extends JpaRepository<Company, Long>, QuerydslPredicateExecutor<Company> {
}
