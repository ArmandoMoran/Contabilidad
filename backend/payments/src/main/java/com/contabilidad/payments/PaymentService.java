package com.contabilidad.payments;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@Transactional
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final PaymentApplicationRepository paymentApplicationRepository;

    public PaymentService(PaymentRepository paymentRepository,
                          PaymentApplicationRepository paymentApplicationRepository) {
        this.paymentRepository = paymentRepository;
        this.paymentApplicationRepository = paymentApplicationRepository;
    }

    public Payment registerPayment(UUID companyId, CreatePaymentRequest request) {
        log.info("Registering payment companyId={}, direction={}, amount={}", companyId, request.paymentDirection(), request.amount());
        Payment payment = PaymentMapper.toEntity(companyId, request);
        payment = paymentRepository.save(payment);
        log.info("Registered payment id={}", payment.getId());
        return payment;
    }

    public PaymentApplication applyPayment(UUID companyId, UUID paymentId, ApplyPaymentRequest request) {
        Payment payment = getPayment(companyId, paymentId);
        PaymentApplication application = PaymentMapper.toApplicationEntity(companyId, payment.getId(), request);
        return paymentApplicationRepository.save(application);
    }

    @Transactional(readOnly = true)
    public Page<Payment> listPayments(UUID companyId, Pageable pageable) {
        log.info("Listing payments for companyId={}", companyId);
        return paymentRepository.findByCompanyId(companyId, pageable);
    }

    @Transactional(readOnly = true)
    public Payment getPayment(UUID companyId, UUID paymentId) {
        log.info("Getting payment companyId={}, paymentId={}", companyId, paymentId);
        return paymentRepository.findByCompanyIdAndId(companyId, paymentId)
            .orElseThrow(() -> {
                log.warn("Payment not found: companyId={}, paymentId={}", companyId, paymentId);
                return new EntityNotFoundException("Payment not found: " + paymentId);
            });
    }
}
