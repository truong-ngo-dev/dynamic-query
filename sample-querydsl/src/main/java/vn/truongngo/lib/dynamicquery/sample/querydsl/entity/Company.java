package vn.truongngo.lib.dynamicquery.sample.querydsl.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "companies")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "industry")
    private String industry;
    @Column(name = "address")
    private String address;
    @Column(name = "website")
    private String website;
}
