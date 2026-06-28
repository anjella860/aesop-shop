package com.aesop.shop.service;

import com.aesop.shop.dto.qna.QnaRequestDto;
import com.aesop.shop.entity.Qna;
import com.aesop.shop.repository.QnaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QnaService {

    private final QnaRepository qnaRepository;

    // QnA 목록 조회
    public List<Qna> getQnaList(Long productId) {
        return qnaRepository.findByProductIdOrderByCreatedAtDesc(productId);
    }
    // QnA 작성
    public Qna addQna(Long memberId, Long productId, QnaRequestDto dto) {
        Qna qna = Qna.builder()
                .memberId(memberId)
                .productId(productId)
                .title(dto.getTitle())
                .content(dto.getContent())
                .build();
        return qnaRepository.save(qna);
    }

    // QnA 수정
    public Qna updateQna(Long id, Long memberId, QnaRequestDto dto) {
        Qna qna = qnaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("QnA가 존재하지 않습니다."));
        if (!qna.getMemberId().equals(memberId)) {
            throw new RuntimeException("수정 권한이 없습니다.");
        }
        qna.setTitle(dto.getTitle());
        qna.setContent(dto.getContent());
        return qnaRepository.save(qna);
    }


    // QnA 삭제
    public void deleteQna(Long id, Long memberId) {
        Qna qna = qnaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("QnA가 존재하지 않습니다."));
        if (!qna.getMemberId().equals(memberId)) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }
        qnaRepository.deleteById(id);
    }

    // 관리자 답변 등록
    public void addAnswer(Long id, String answer) {
        Qna qna = qnaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("QnA가 존재하지 않습니다."));
        qna.setAnswer(answer);
        qna.setIsAnswered(true);
        qnaRepository.save(qna);
    }

    // 관리자 답변 삭제
    public void deleteAnswer(Long id) {
        Qna qna = qnaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("QnA가 존재하지 않습니다."));
        qna.setAnswer(null);
        qna.setIsAnswered(false);
        qnaRepository.save(qna);
    }

    // 전체 QnA 목록 (관리자)
    public List<Qna> findAllForAdmin() {
        return qnaRepository.findAllByOrderByCreatedAtDesc();
    }

    // 미답변 QnA 목록 (관리자)
    public List<Qna> findUnanswered() {
        return qnaRepository.findByIsAnswered(false);
    }
}