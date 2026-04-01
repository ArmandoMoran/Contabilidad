package com.contabilidad.payments;

import com.contabilidad.shared.PageResponse;
import com.contabilidad.shared.SecurityContextUtils;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentDto create(@Valid @RequestBody CreatePaymentRequest request) {
        Payment payment = paymentService.registerPayment(SecurityContextUtils.currentCompanyId(), request);
        return PaymentMapper.toDto(payment);
    }

    @PostMapping("/{id}/apply")
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentApplicationDto apply(
            @PathVariable java.util.UUID id,
            @Valid @RequestBody ApplyPaymentRequest request) {
        PaymentApplication application = paymentService.applyPayment(SecurityContextUtils.currentCompanyId(), id, request);
        return PaymentMapper.toApplicationDto(application);
    }

    @GetMapping
    public PageResponse<PaymentDto> list(Pageable pageable) {
        var companyId = SecurityContextUtils.currentCompanyId();
        Page<Payment> page = paymentService.listPayments(companyId, pageable);
        return PageResponse.of(
            page.getContent().stream().map(PaymentMapper::toDto).toList(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements()
        );
    }

    @GetMapping("/{id}")
    public PaymentDto get(@PathVariable java.util.UUID id) {
        Payment payment = paymentService.getPayment(SecurityContextUtils.currentCompanyId(), id);
        return PaymentMapper.toDto(payment);
    }
}
