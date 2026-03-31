package com.contabilidad.payments;

import com.contabilidad.shared.PageResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentDto create(
            @RequestHeader("X-Company-Id") UUID companyId,
            @Valid @RequestBody CreatePaymentRequest request) {
        Payment payment = paymentService.registerPayment(companyId, request);
        return PaymentMapper.toDto(payment);
    }

    @PostMapping("/{id}/apply")
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentApplicationDto apply(
            @RequestHeader("X-Company-Id") UUID companyId,
            @PathVariable UUID id,
            @Valid @RequestBody ApplyPaymentRequest request) {
        PaymentApplication application = paymentService.applyPayment(companyId, id, request);
        return PaymentMapper.toApplicationDto(application);
    }

    @GetMapping
    public PageResponse<PaymentDto> list(
            @RequestHeader("X-Company-Id") UUID companyId,
            Pageable pageable) {
        Page<Payment> page = paymentService.listPayments(companyId, pageable);
        return PageResponse.of(
            page.getContent().stream().map(PaymentMapper::toDto).toList(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements()
        );
    }

    @GetMapping("/{id}")
    public PaymentDto get(
            @RequestHeader("X-Company-Id") UUID companyId,
            @PathVariable UUID id) {
        Payment payment = paymentService.getPayment(companyId, id);
        return PaymentMapper.toDto(payment);
    }
}
