package study.datajpa.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;
    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    MemberQueryRepository memberQueryRepository;

    @Test
    public void testMember() {
        // given
        Member member = new Member("memberA");

        // when
        Member saveMember = memberRepository.save(member);
        Optional<Member> temp = memberRepository.findById(saveMember.getId());
        Member findMember = temp.get();

        // then
        Assertions.assertEquals(findMember.getId(), member.getId());
        Assertions.assertEquals(findMember.getUsername(), member.getUsername());
        Assertions.assertEquals(findMember, member);
    }

    @Test
    public void basicCRUD() {

        // given
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        // when
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        // then
        /**
         * 단건 조회 검증
         * */
        Assertions.assertEquals(findMember1, member1);
        Assertions.assertEquals(findMember2, member2);

        /**
         * 리스트 조회 검증
         * */
        List<Member> all = memberRepository.findAll();
        Assertions.assertEquals(all.size(), 2);

        /**
         * 카운트 검증
         * */
        long count = memberRepository.count();
        Assertions.assertEquals(count, 2);

        /**
         * 삭제 검증
         * */
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        Assertions.assertEquals(deletedCount, 0);
    }

    @Test
    void findByUsernameAndAgeGreaterThen() {

        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);
        // where username = 'AAA' and age > 15

        Assertions.assertEquals(result.get(0).getUsername(), "AAA");
        Assertions.assertEquals(result.get(0).getAge(), 20);
        Assertions.assertEquals(result.size(), 1);
    }

    @Test
    void testQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        Member findMember = result.get(0);
        Assertions.assertEquals(findMember, m1);
    }

    @Test
    void findUsernameList() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> usernameList = memberRepository.findUsernameList();
        for (String s : usernameList) {
            System.out.println(s);
        }
    }

    @Test
    void findMemberDto() {

        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA", 10);
        m1.setTeam(team);
        memberRepository.save(m1);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }
    }

    @Test
    void findByNames() {

        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> usernameList = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        for (Member member : usernameList) {
            System.out.println("member = " + member);
        }
    }

    @Test
    void returnType() {

        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        Member m3 = new Member("CCC", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);
        memberRepository.save(m3);

        List<Member> resultA = memberRepository.findListByUsername("AAA");
        System.out.println("resultA = " + resultA);

        Member resultB = memberRepository.findMemberByUsername("BBB");
        System.out.println("resultB = " + resultB);

        Optional<Member> resultC = memberRepository.findOptionalByUsername("CCC");
        System.out.println("resultC = " + resultC.get());

        List<Member> resultEmptyList = memberRepository.findListByUsername("DDD"); // return empty collection;
        System.out.println("resultEmptyList = " + resultEmptyList);

        Member resultSingle = memberRepository.findMemberByUsername("EEE");
        System.out.println("resultSingle = " + resultSingle); // null

        Optional<Member> resultOptional = memberRepository.findOptionalByUsername("FFF"); // 있을 수도 있고 없을 수도 있다면.
        System.out.println("resultOptional = " + resultOptional); // Optional.empty

    }


    @Test
    void paging() {

        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);
//        Slice<Member> page = memberRepository.findByAge(age, pageRequest); // limit + 1, (더보기)
//        List<Member> page = memberRepository.findByAge(age, pageRequest);

        // dto로 변환
        Page<MemberDto> toMap = page.map(m -> new MemberDto(m.getId(), m.getUsername(), null));


        // then
        List<Member> content = page.getContent(); // data
        for (Member member : content) {
            System.out.println("member = " + member);
        }
        System.out.println("totalElements = " + page.getTotalElements());

        Assertions.assertEquals(content.size(), 3);
        Assertions.assertEquals(page.getTotalElements(), 5);
        Assertions.assertEquals(page.getNumber(), 0);
        Assertions.assertEquals(page.getTotalPages(), 2);
        Assertions.assertTrue(page.isFirst());
        Assertions.assertTrue(page.hasNext());

    }

    @Test
    void bulkUpdate() {

        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        // when
        int resultCount = memberRepository.bulkAgePlus(20);

        Member member5 = null;
        member5 = memberRepository.findMemberByUsername("member5");
        System.out.println(member5);

        /** 벌크 연산 이후에는 영속성 컨텍스트를 날려야 한다.
         *
         * entityManager.clear();
         * 또는
         * @Modifying(clearAutomatically = true) */
//        entityManager.flush();
//        entityManager.clear();

        member5 = memberRepository.findMemberByUsername("member5");
        System.out.println(member5);

        // then
        Assertions.assertEquals(resultCount, 3);

    }

    @Test
    public void findMemberLazy() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        entityManager.flush();
        entityManager.clear();

        List<Member> members = memberRepository.findAll(); // @EntityGraph(attributePaths = {"team"})
//        List<Member> members = memberRepository.findMemberFetchJoin();

        /** N + 1 문제 -> fetch join으로 해결
         *
         * member를 조회할 때, 지연로딩 설정 때문에
         * member 조회할 때는 team을 프록시 객체로만 받아온다.
         *
         * 그리고, team이 사용될 때 team을 조회하는데,
         * 아래 반복문이 n번 반복할 때마다, team을 조회하는 퀴리가 n번 실행된다.
         *
         * -> fetch join으로 해결한다.
         * -> @EntityGraph(attributePaths = {"team"})
         *
         * */

        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.teamClass = " + member.getTeam().getClass());
            System.out.println("member.team = " + member.getTeam().getName());
        }
    }

    @Test
    void queryHint() {

        // given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        entityManager.flush(); // -> 쿼리 실행
        entityManager.clear(); // -> 영속성 컨텍스트 clear (캐시 날리는 것과 같은 개념)

        // when
        Member findMember = memberRepository.findById(member1.getId()).get();
        findMember.setUsername("member2"); // 변경 감지 기능으로 인해 update 된다. 쿼리는 flush() 할 때 실행한다.
        entityManager.flush();
        entityManager.clear();

        Member hintsMember = memberRepository.findReadOnlyByUsername("member2");
        hintsMember.setUsername("member3");
        entityManager.flush();

    }

    @Test
    void lock() {

        // given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        entityManager.flush();
        entityManager.clear();

        // when
        List<Member> result = memberRepository.findLockByUsername("member1");

    }

    @Test
    public void callCustom() {
        List<Member> result = memberRepository.findMemberCustom();
    }
}