package vn.truongngo.lib.dynamicquery.sample.jooq.service;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JOOQService {

    private final DSLContext dslContext;

    public void test() {

    }
}
