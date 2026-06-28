package com.aesop.shop.controller;

import com.aesop.shop.dto.member.MemberResponseDto;
import com.aesop.shop.dto.notice.NoticeRequestDto;
import com.aesop.shop.dto.notice.NoticeResponseDto;
import com.aesop.shop.dto.product.ProductRequestDto;
import com.aesop.shop.dto.product.ProductResponseDto;
import com.aesop.shop.dto.order.OrderResponseDto;
import com.aesop.shop.entity.OrderStatus;
import com.aesop.shop.entity.Product;
import com.aesop.shop.entity.Role;
import com.aeshop.shop.service.*;
import com.aesop.shop.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final MemberService memberService;
    private final ProductService productService;
    private final OrderService orderService;
    private final NoticeService noticeService;
    private final QnaService qnaService;

    // ========== 회원 관리 ==========

    // 전체 회원 목록
    @GetMapping("/members")
    public ResponseEntity<List<MemberResponseDto>> getMembers() {
        List<MemberResponseDto> list = memberService.findAll()
                .stream()
                .map(MemberResponseDto::new)
                .toList();
        return ResponseEntity.ok(list);
    }

    // 회원 등급 변경
    @PutMapping("/members/{id}/role")
    public ResponseEntity<String> changeRole(
            @PathVariable Long id,
            @RequestParam Role role) {
        memberService.changeRole(id, role);
        return ResponseEntity.ok("등급 변경 완료");
    }

    // 회원 강퇴
    @DeleteMapping("/members/{id}")
    public ResponseEntity<String> deleteMember(@PathVariable Long id) {
        memberService.deleteMemberByAdmin(id);
        return ResponseEntity.ok("강퇴 완료");
    }

    // ========== 상품 관리 ==========

    // 상품 등록
    @PostMapping("/products")
    public ResponseEntity<ProductResponseDto> addProduct(
            @RequestPart("product") ProductRequestDto dto,
            @RequestPart(value = "image", required = false) MultipartFile image) throws Exception {
        Product product = Product.builder()
                .categoryId(dto.getCategoryId())
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .volume(dto.getVolume())
                .skinType(dto.getSkinType())
                .ingredients(dto.getIngredients())
                .isBestseller(dto.getIsBestseller() != null && dto.getIsBestseller())
                .build();
        return ResponseEntity.ok(new ProductResponseDto(
                productService.addProduct(product, image)));
    }

    // 상품 수정
    @PutMapping("/products/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct(
            @PathVariable Long id,
            @RequestPart("product") ProductRequestDto dto,
            @RequestPart(value = "image", required = false) MultipartFile image) throws Exception {
        Product updateProduct = Product.builder()
                .categoryId(dto.getCategoryId())
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .volume(dto.getVolume())
                .skinType(dto.getSkinType())
                .ingredients(dto.getIngredients())
                .isBestseller(dto.getIsBestseller() != null && dto.getIsBestseller())
                .build();
        return ResponseEntity.ok(new ProductResponseDto(
                productService.updateProduct(id, updateProduct, image)));
    }

    // 상품 삭제
    @DeleteMapping("/products/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("삭제 완료");
    }

    // ========== 주문 관리 ==========

    // 전체 주문 목록
    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponseDto>> getOrders() {
        List<OrderResponseDto> list = orderService.findAll()
                .stream()
                .map(OrderResponseDto::new)
                .toList();
        return ResponseEntity.ok(list);
    }

    // 주문 상태 변경
    @PutMapping("/orders/{id}/status")
    public ResponseEntity<String> changeOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {
        orderService.changeStatus(id, status);
        return ResponseEntity.ok("상태 변경 완료");
    }

    // ========== QnA 관리 ==========

    // QnA 답변 등록
    @PutMapping("/qna/{id}/answer")
    public ResponseEntity<String> answerQna(
            @PathVariable Long id,
            @RequestParam String answer) {
        qnaService.addAnswer(id, answer);
        return ResponseEntity.ok("답변 등록 완료");
    }

    // ========== 공지사항 관리 ==========

    // 공지 등록
    @PostMapping("/notice")
    public ResponseEntity<NoticeResponseDto> addNotice(
            @RequestBody NoticeRequestDto dto) {
        return ResponseEntity.ok(new NoticeResponseDto(
                noticeService.addNotice(dto)));
    }

    // 공지 수정
    @PutMapping("/notice/{id}")
    public ResponseEntity<NoticeResponseDto> updateNotice(
            @PathVariable Long id,
            @RequestBody NoticeRequestDto dto) {
        return ResponseEntity.ok(new NoticeResponseDto(
                noticeService.updateNotice(id, dto)));
    }

    // 공지 삭제
    @DeleteMapping("/notice/{id}")
    public ResponseEntity<String> deleteNotice(@PathVariable Long id) {
        noticeService.deleteNotice(id);
        return ResponseEntity.ok("삭제 완료");
    }
}
