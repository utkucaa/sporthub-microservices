package com.sporthub.payment_service.service;

import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PaymentIntentConfirmParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

// stripe api işlemlerini yöneten servis
@Service
public class StripeService {

    private static final Logger logger = LoggerFactory.getLogger(StripeService.class);

    // stripe'da ödeme niyeti oluştur
    public PaymentIntent createPaymentIntent(BigDecimal amount, String currency, Map<String, String> metadata) throws StripeException {
        try {
            // MOCK/TEST MODE - Create a fake payment intent for testing
            logger.info("MOCK MODE: Creating fake payment intent for amount={}, currency={}", amount, currency);
            
            // Create a mock PaymentIntent object
            // In real Stripe integration, this would call Stripe API
            String fakePaymentIntentId = "pi_mock_" + System.currentTimeMillis();
            String fakeClientSecret = fakePaymentIntentId + "_secret_" + System.currentTimeMillis();
            
            logger.info("MOCK: Created fake payment intent: id={}, amount={}, currency={}", 
                       fakePaymentIntentId, amount, currency);
            
            // For now, return null and handle in service layer
            // In real implementation, this would return actual PaymentIntent from Stripe
            return createMockPaymentIntent(fakePaymentIntentId, fakeClientSecret, amount, currency);
            
        } catch (Exception e) {
            logger.error("Failed to create mock payment intent: amount={}, currency={}, error={}", 
                        amount, currency, e.getMessage(), e);
            throw new StripeException("Mock payment intent creation failed", null, null, 0, e) {};
        }
    }
    
    private PaymentIntent createMockPaymentIntent(String id, String clientSecret, BigDecimal amount, String currency) {
        // This is a simplified mock - in real implementation you'd use Stripe objects
        logger.info("Creating mock PaymentIntent with id={}, clientSecret={}", id, clientSecret);
        
        // For testing, we'll create a basic mock that satisfies the interface
        // Note: This is a simplified approach for demo purposes
        return new PaymentIntent() {
            @Override
            public String getId() { return id; }
            
            @Override  
            public String getClientSecret() { return clientSecret; }
            
            @Override
            public String getStatus() { return "requires_payment_method"; }
            
            @Override
            public Long getAmount() { return amount.multiply(BigDecimal.valueOf(100)).longValue(); }
            
            @Override
            public String getCurrency() { return currency.toLowerCase(); }
        };
    }

    /**
     * Confirm payment intent
     */
    public PaymentIntent confirmPaymentIntent(String paymentIntentId, String paymentMethodId) throws StripeException {
        try {
            logger.info("MOCK MODE: Confirming fake payment intent: id={}, paymentMethodId={}", 
                       paymentIntentId, paymentMethodId);
            
            // Create a mock confirmed payment intent
            return new PaymentIntent() {
                @Override
                public String getId() { return paymentIntentId; }
                
                @Override  
                public String getClientSecret() { return paymentIntentId + "_secret_confirmed"; }
                
                @Override
                public String getStatus() { return "succeeded"; } // Mock successful payment
                
                @Override
                public Long getAmount() { return 10000L; } // Mock amount
                
                @Override
                public String getCurrency() { return "usd"; }
            };
            
        } catch (Exception e) {
            logger.error("Failed to confirm mock payment intent: paymentIntentId={}, error={}", 
                        paymentIntentId, e.getMessage(), e);
            throw new StripeException("Mock payment confirmation failed", null, null, 0, e) {};
        }
    }

    /**
     * Retrieve payment intent
     */
    public PaymentIntent retrievePaymentIntent(String paymentIntentId) throws StripeException {
        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            
            logger.debug("Retrieved payment intent: id={}, status={}", 
                        paymentIntent.getId(), paymentIntent.getStatus());
            
            return paymentIntent;
        } catch (StripeException e) {
            logger.error("Failed to retrieve payment intent: paymentIntentId={}, error={}", 
                        paymentIntentId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Create customer in Stripe
     */
    public Customer createCustomer(String email, String name) throws StripeException {
        try {
            CustomerCreateParams params = CustomerCreateParams.builder()
                    .setEmail(email)
                    .setName(name)
                    .build();

            Customer customer = Customer.create(params);
            
            logger.info("Created Stripe customer: id={}, email={}", customer.getId(), email);
            
            return customer;
        } catch (StripeException e) {
            logger.error("Failed to create customer: email={}, error={}", email, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Create refund
     */
    public Refund createRefund(String paymentIntentId, BigDecimal amount, String reason) throws StripeException {
        try {
            RefundCreateParams.Builder paramsBuilder = RefundCreateParams.builder()
                    .setPaymentIntent(paymentIntentId);

            if (amount != null) {
                // Convert amount to cents
                Long amountInCents = amount.multiply(BigDecimal.valueOf(100)).longValue();
                paramsBuilder.setAmount(amountInCents);
            }

            if (reason != null && !reason.isEmpty()) {
                paramsBuilder.setReason(RefundCreateParams.Reason.REQUESTED_BY_CUSTOMER);
                paramsBuilder.putMetadata("reason", reason);
            }

            Refund refund = Refund.create(paramsBuilder.build());
            
            logger.info("Created refund: id={}, paymentIntentId={}, amount={}", 
                       refund.getId(), paymentIntentId, amount);
            
            return refund;
        } catch (StripeException e) {
            logger.error("Failed to create refund: paymentIntentId={}, amount={}, error={}", 
                        paymentIntentId, amount, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Retrieve refund
     */
    public Refund retrieveRefund(String refundId) throws StripeException {
        try {
            Refund refund = Refund.retrieve(refundId);
            
            logger.debug("Retrieved refund: id={}, status={}", refund.getId(), refund.getStatus());
            
            return refund;
        } catch (StripeException e) {
            logger.error("Failed to retrieve refund: refundId={}, error={}", refundId, e.getMessage(), e);
            throw e;
        }
    }
}
