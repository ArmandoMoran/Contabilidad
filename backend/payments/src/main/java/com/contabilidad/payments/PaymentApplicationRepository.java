package com.contabilidad.payments;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface PaymentApplicationRepository extends JpaRepository<PaymentApplication, UUID> {

    List<PaymentApplication> findByPaymentId(UUID paymentId);

    List<PaymentApplication> findByDocumentTypeAndDocumentId(String documentType, UUID documentId);
}
