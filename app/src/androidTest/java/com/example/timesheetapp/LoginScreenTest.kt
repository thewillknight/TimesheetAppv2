package com.example.timesheetapp

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {

    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()

    private lateinit var emailAddressTextField: SemanticsMatcher
    private lateinit var passwordTextField: SemanticsMatcher
    private lateinit var loginButton: SemanticsMatcher
    private lateinit var signUpButton: SemanticsMatcher
    private lateinit var emptyErrorText: SemanticsMatcher
    @Before
    fun setUp(){
        emailAddressTextField = hasText(rule.activity.getString(R.string.email_field_hint_text))
        passwordTextField = hasText(rule.activity.getString(R.string.password_field_hint_text))
        loginButton = hasText(rule.activity.getString(R.string.login_button_text)) and hasClickAction()
        signUpButton = hasText(rule.activity.getString(R.string.sign_up_button_text)) and hasClickAction()
        emptyErrorText = hasText(rule.activity.getString(R.string.empty_email_or_password_error))
    }

    @Test
    fun check_state_of_the_login_page() {
        rule.onNode(emailAddressTextField).assertExists()
        rule.onNode(passwordTextField).assertExists()
        rule.onNode(loginButton).assertExists()
        rule.onNode(signUpButton).assertExists()
    }

    @Test
    fun check_initial_ui_state() {
        rule.onNode(emailAddressTextField).assertExists()
        rule.onNode(passwordTextField).assertExists()
        rule.onNode(loginButton).assertExists()
        rule.onNode(signUpButton).assertExists()
    }

    @Test
    fun show_error_if_email_or_password_empty() {
        rule.onNode(loginButton).performClick()
        rule.onNode(emptyErrorText).assertExists()
    }

    @Test
    fun enter_email_and_password_updates_text_fields() {
        val testEmail = "test@example.com"
        val testPassword = "secret123"

        rule.onNode(emailAddressTextField).performTextInput(testEmail)
        rule.onNode(passwordTextField).performTextInput(testPassword)

        rule.onNodeWithText(testEmail).assertExists()
        rule.onNodeWithText(testPassword, substring = true).assertDoesNotExist() // password hidden
    }

    @Test
    fun navigate_to_sign_up_screen_on_button_click() {
        rule.onNode(signUpButton).performClick()

        val firstNameField = hasText(
            rule.activity.getString(R.string.first_name_field_hint_text)
        )

        // Wait for recomposition and check if the sign-up screen's first name field is displayed
        rule.waitUntil(timeoutMillis = 5_000) {
            rule.onAllNodes(firstNameField).fetchSemanticsNodes().isNotEmpty()
        }

        rule.onNode(firstNameField).assertIsDisplayed()
    }

}