package com.upisoundbox

import com.upisoundbox.core.model.Direction
import com.upisoundbox.validation.PaymentValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PaymentValidatorTest {

    @Test
    fun testCreditClassifications() {
        assertEquals(Direction.CREDIT, PaymentValidator.classifyDirection("received ₹500 from rahul"))
        assertEquals(Direction.CREDIT, PaymentValidator.classifyDirection("rahul sent you ₹500"))
        assertEquals(Direction.CREDIT, PaymentValidator.classifyDirection("account credited with ₹1,000"))
        assertEquals(Direction.CREDIT, PaymentValidator.classifyDirection("payment received on phonepe"))
    }

    @Test
    fun testDebitClassifications() {
        assertEquals(Direction.DEBIT, PaymentValidator.classifyDirection("you paid ₹500 to rahul"))
        assertEquals(Direction.DEBIT, PaymentValidator.classifyDirection("paid ₹100 for grocery"))
        assertEquals(Direction.DEBIT, PaymentValidator.classifyDirection("₹500 debited from your account"))
        assertEquals(Direction.DEBIT, PaymentValidator.classifyDirection("money sent to rahul"))
    }

    @Test
    fun testNonPaymentAndPromotions() {
        assertEquals(Direction.UNKNOWN, PaymentValidator.classifyDirection("your otp is 492019"))
        assertEquals(Direction.UNKNOWN, PaymentValidator.classifyDirection("win up to ₹500 cashback"))
        assertTrue(PaymentValidator.isNonPaymentOrPromotion("win up to ₹500 cashback on your next recharge"))
        assertTrue(PaymentValidator.isNonPaymentOrPromotion("your verification code is 883921"))
    }
}
