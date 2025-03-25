package sweet.dh.sweetshop.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sweet.dh.sweetshop.entity.Payment;
import sweet.dh.sweetshop.entity.requset.PaymentReq;
import sweet.dh.sweetshop.service.PaymentService;

import java.util.List;


@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 결제 정보 저장
     * @param paymentRequest
     * @return
     */
    @PostMapping("/portone")
    public ResponseEntity<String> savePortone(@RequestBody PaymentReq paymentRequest) {
        paymentService.savePayment(Payment.of(paymentRequest));
        return ResponseEntity.ok("Payment processed successfully.");
    }

    /**
     * 모든 결제 내역 조회
     *
     * @return 모든 Payment 엔티티 리스트
     */
    @GetMapping("/list")
    public ResponseEntity<List<Payment>> getAllPayments() {
        List<Payment> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

}