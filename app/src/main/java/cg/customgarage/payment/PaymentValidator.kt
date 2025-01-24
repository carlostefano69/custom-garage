package cg.customgarage.payment

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentValidator @Inject constructor() {
    
    fun validatePayPalEmail(email: String): ValidationResult {
        return when {
            email.isBlank() -> ValidationResult.Error("L'email non può essere vuota")
            !email.matches(Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")) -> 
                ValidationResult.Error("Email non valida")
            else -> ValidationResult.Success
        }
    }

    fun validateBankTransfer(
        accountHolder: String,
        iban: String,
        swift: String
    ): ValidationResult {
        return when {
            accountHolder.isBlank() -> 
                ValidationResult.Error("Il nome dell'intestatario non può essere vuoto")
            !isValidIban(iban) -> 
                ValidationResult.Error("IBAN non valido")
            !isValidSwift(swift) -> 
                ValidationResult.Error("SWIFT/BIC non valido")
            else -> ValidationResult.Success
        }
    }

    private fun isValidIban(iban: String): Boolean {
        val cleanIban = iban.replace(" ", "").uppercase()
        return cleanIban.matches(Regex("^[A-Z]{2}[0-9]{2}[A-Z0-9]{4,}$")) &&
               cleanIban.length in 15..34
    }

    private fun isValidSwift(swift: String): Boolean {
        val cleanSwift = swift.replace(" ", "").uppercase()
        return cleanSwift.matches(Regex("^[A-Z]{6}[A-Z0-9]{2}([A-Z0-9]{3})?$"))
    }
}

sealed class ValidationResult {
    object Success : ValidationResult()
    data class Error(val message: String) : ValidationResult()
} 