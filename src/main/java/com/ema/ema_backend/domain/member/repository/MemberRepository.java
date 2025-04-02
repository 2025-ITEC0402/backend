package com.ema.ema_backend.domain.member.repository;

import com.ema.ema_backend.domain.member.entity.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberRepository {
    private final EntityManager em;

    public Long save(Member member) {
        em.persist(member);
        return member.getId();
    }

    public Optional<Member> findByEmail(String email) {
        String jpql = "select m from Member m where m.email = :email";
        Optional<Member> findMember = em.createQuery(jpql, Member.class)
                .setParameter("email", email)
                .getResultList()
                .stream().findFirst();

        return findMember;
    }

}
