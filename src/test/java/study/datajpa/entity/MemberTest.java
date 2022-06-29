package study.datajpa.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.repository.MemberRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@SpringBootTest
@Transactional
class MemberTest {

    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    MemberRepository memberRepository;

    @Test
    public void testEntity() throws Exception {
        // given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        entityManager.persist(teamA);
        entityManager.persist(teamB);

        // when
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);

        entityManager.persist(member1);
        entityManager.persist(member2);
        entityManager.persist(member3);
        entityManager.persist(member4);

        // 초기화
        entityManager.flush();
        entityManager.clear();

        // then
        List<Member> members = entityManager.createQuery("select m from Member m", Member.class).getResultList();

        for (Member member : members) {
            System.out.println("member = " + member);
            System.out.println("-> member.team = " + member.getTeam());
        }
    }

    @Test
    public void JpaEventBaseEntity() throws InterruptedException {
        // given
        Member member = new Member("member1");
        memberRepository.save(member);

        Thread.sleep(1000); // 테스트 확인을 위해..
        member.setUsername("member2");

        entityManager.flush();
        entityManager.clear();

        // when
        Member findMember = memberRepository.findById(member.getId()).get();

        // then
        System.out.println("findMember.getCreatedDate = " + findMember.getCreatedDate());
        System.out.println("findMember.getLastModifiedDate = " + findMember.getLastModifiedDate());
        System.out.println("findMember.getCreatedBy = " + findMember.getCreatedBy());
        System.out.println("findMember.getLastModifiedBy = " + findMember.getLastModifiedBy());
    }
}