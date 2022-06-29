package study.datajpa.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberQueryRepository {

    /**
     * '커맨드'와 '쿼리'를 분리
     * '핵심 비즈니스'와 '그렇지 않은 비즈니스'를 분리
     * */

    private final EntityManager entityManager;

    List<Member> findAllMembers() {
        return entityManager.createQuery("select m from Member m")
                .getResultList();
    }
}
