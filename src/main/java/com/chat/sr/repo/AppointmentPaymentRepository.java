package com.chat.sr.repo;

import com.chat.sr.model.AppointmentPayment;
import com.chat.sr.model.Owner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentPaymentRepository extends JpaRepository<AppointmentPayment, Long> {
    List<AppointmentPayment> findByOwner(Owner owner);
}
