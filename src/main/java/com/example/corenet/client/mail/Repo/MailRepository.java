package com.example.corenet.client.mail.Repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.corenet.entity.Mail;

@Repository
public interface MailRepository extends JpaRepository<Mail, Long> {

    // 받은 메일함
    List<Mail> findByRecipientEmailOrderByCreatedAtDesc(String recipientEmail);

    // 보낸 메일함
    List<Mail> findBySenderEmailOrderByCreatedAtDesc(String senderEmail);
}
