package study.datajpa.repository;

import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    /**
     * 기본 규칙(interface + Impl): MemberRepository + Impl
     * 기본 규칙대로 이름을 지어줘야 spring-data-jpa가 적용해준다.
     * config 파일을 통해서 바꿀 수 있지만.. 굳이..
     * */

    private final EntityManager entityManager;

//    public MemberRepositoryImpl(EntityManager entityManager) {
//        this.entityManager = entityManager;
//    }
// -> @RequiredArgsConstructor

    @Override
    public List<Member> findMemberCustom() {
        return entityManager.createQuery("select m from Member m")
                .getResultList();
    }
}
