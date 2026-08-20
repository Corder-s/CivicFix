package com.example

import com.example.data.models.UserRole
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthUnitTest {

    @Test
    fun testDemoUserCredentials() {
        val demoEmail = "demo@civicfix.com"
        val demoPass = "Demo@123"

        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        assertTrue(emailRegex.matches(demoEmail))
        assertTrue(demoPass.length >= 8)
        assertTrue(demoPass.any { it.isUpperCase() })
        assertTrue(demoPass.any { it.isDigit() })
    }

    @Test
    fun testDemoAdminCredentials() {
        val adminEmail = "admin@civicfix.com"
        val adminPass = "Admin@123"

        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        assertTrue(emailRegex.matches(adminEmail))
        assertTrue(adminPass.length >= 8)
        assertTrue(adminPass.any { it.isUpperCase() })
        assertTrue(adminPass.any { it.isDigit() })
    }

    @Test
    fun testIndianPhoneValidation() {
        val phoneRegex = "^(\\+91)?[6-9]\\d{9}$".toRegex()
        assertTrue(phoneRegex.matches("+919876543210"))
        assertTrue(phoneRegex.matches("9876543210"))
        assertFalse(phoneRegex.matches("12345"))
        assertFalse(phoneRegex.matches("abcde"))
    }

    @Test
    fun testUserRoleAssignment() {
        val citizenRole = UserRole.CITIZEN
        val adminRole = UserRole.ADMIN
        assertTrue(citizenRole != adminRole)
    }
}
