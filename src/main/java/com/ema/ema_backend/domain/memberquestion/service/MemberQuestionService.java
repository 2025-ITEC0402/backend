package com.ema.ema_backend.domain.memberquestion.service;

import com.ema.ema_backend.domain.memberquestion.repository.MemberQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberQuestionService {
    private final MemberQuestionRepository memberQuestionRepository;
}
