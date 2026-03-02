package com.taskmanager.TaskManager.payment;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.Invoice;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.taskmanager.TaskManager.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StripeWebhookService {

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    private final UserService userService;

    public void processWebhook(String payload, String sigHeader) {

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            throw new RuntimeException("Webhook signature verification failed", e);
        }

        try {
            switch (event.getType()) {
                case "checkout.session.completed":
                    Session session = (Session) event.getDataObjectDeserializer().getObject().orElseThrow(() -> new RuntimeException("Failed to deserialize session"));

                    String subscriptionId = session.getSubscription();
                    String customerEmail = session.getCustomerEmail();

                    Subscription subscription = (Subscription) event.getDataObjectDeserializer()
                            .getObject()
                            .orElseThrow(() -> new RuntimeException("Failed to deserialize subscription"));

                    Long periodEnd = subscription.getRawJsonObject().get("current_period_end").getAsLong();

                    userService.activateSubscription(customerEmail, subscriptionId, periodEnd);
                    break;

                case "invoice.paid":
                    Invoice invoice = (Invoice) event.getDataObjectDeserializer().getObject().orElseThrow();
                    userService.updateSubscription(invoice.getCustomer(), invoice.getLines().getData().get(0).getPeriod().getEnd(), "ACTIVE");
                    break;

                case "invoice.payment_failed":
                    Invoice failedInvoice = (Invoice) event.getDataObjectDeserializer().getObject().orElseThrow();
                    userService.updateSubscriptionStatus(failedInvoice.getCustomer(), "PAST_DUE");
                    break;

                case "customer.subscription.deleted":
                    Subscription sub = (Subscription) event.getDataObjectDeserializer().getObject().orElseThrow();
                    userService.downgradeUser(sub.getCustomer());
                    break;
            }
        } catch (RuntimeException e){
            throw new RuntimeException("Webhook processing error", e);
        }

    }

}
