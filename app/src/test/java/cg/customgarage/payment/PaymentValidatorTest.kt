package cg.customgarage.payment

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class PaymentValidatorTest {
    private lateinit var validator: PaymentValidator

    @Before
    fun setup() {
        validator = PaymentValidator()
    }

    @Test
    fun `validatePayPalEmail returns success for valid email`() {
        val result = validator.validatePayPalEmail("test@example.com")
        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun `validatePayPalEmail returns error for empty email`() {
        val result = validator.validatePayPalEmail("")
        assertTrue(result is ValidationResult.Error)
        assertEquals("L'email non può essere vuota", (result as ValidationResult.Error).message)
    }

    @Test
    fun `validatePayPalEmail returns error for invalid email format`() {
        val invalidEmails = listOf(
            "test",
            "test@",
            "@example.com",
            "test@example",
            "test.example.com"
        )

        invalidEmails.forEach { email ->
            val result = validator.validatePayPalEmail(email)
            assertTrue("Email $email dovrebbe essere invalida", result is ValidationResult.Error)
            assertEquals("Email non valida", (result as ValidationResult.Error).message)
        }
    }

    @Test
    fun `validateBankTransfer returns success for valid data`() {
        val result = validator.validateBankTransfer(
            accountHolder = "Mario Rossi",
            iban = "IT60X0542811101000000123456",
            swift = "UNCRITM1234"
        )
        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun `validateBankTransfer returns error for empty account holder`() {
        val result = validator.validateBankTransfer(
            accountHolder = "",
            iban = "IT60X0542811101000000123456",
            swift = "UNCRITM1234"
        )
        assertTrue(result is ValidationResult.Error)
        assertEquals(
            "Il nome dell'intestatario non può essere vuoto",
            (result as ValidationResult.Error).message
        )
    }

    @Test
    fun `validateBankTransfer returns error for invalid IBAN`() {
        val invalidIbans = listOf(
            "IT123", // troppo corto
            "123456789", // manca il codice paese
            "ITXX1234567890123456", // formato non valido
            "IT00X0542811101000000123456789012345" // troppo lungo
        )

        invalidIbans.forEach { iban ->
            val result = validator.validateBankTransfer(
                accountHolder = "Mario Rossi",
                iban = iban,
                swift = "UNCRITM1234"
            )
            assertTrue("IBAN $iban dovrebbe essere invalido", result is ValidationResult.Error)
            assertEquals("IBAN non valido", (result as ValidationResult.Error).message)
        }
    }

    @Test
    fun `validateBankTransfer returns error for invalid SWIFT`() {
        val invalidSwifts = listOf(
            "UNC", // troppo corto
            "UNCRIT", // troppo corto
            "123RITM1", // deve iniziare con lettere
            "UNCRITM12345678" // troppo lungo
        )

        invalidSwifts.forEach { swift ->
            val result = validator.validateBankTransfer(
                accountHolder = "Mario Rossi",
                iban = "IT60X0542811101000000123456",
                swift = swift
            )
            assertTrue("SWIFT $swift dovrebbe essere invalido", result is ValidationResult.Error)
            assertEquals("SWIFT/BIC non valido", (result as ValidationResult.Error).message)
        }
    }
} 