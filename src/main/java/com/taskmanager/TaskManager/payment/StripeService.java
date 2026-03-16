package com.taskmanager.TaskManager.payment;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.taskmanager.TaskManager.users.Status;
import com.taskmanager.TaskManager.users.controller.UserController;
import com.taskmanager.TaskManager.users.entity.User;
import com.taskmanager.TaskManager.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StripeService {

    private final UserRepository uRepository;

    @Value("${stripe.price.id}")
    private String priceId;

    public String createUpgradeSession(String userEmail) throws StripeException {

        User cUser = uRepository.findByEmail(userEmail).orElseThrow();

        if (cUser.getStatus() == Status.PREMIUM) {
            throw new IllegalStateException("Current user is already Premium");
        }

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setCustomerEmail(userEmail)
                .setSuccessUrl("http://localhost:3000/success")
                .setCancelUrl("http://localhost:3000/cancel")
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setPrice(priceId)
                        .setQuantity(1L)
                        .build())
                .build();

        Session session = Session.create(params);

        return session.getUrl();
    }
}

