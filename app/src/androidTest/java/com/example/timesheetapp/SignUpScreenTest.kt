package com.example.timesheetapp

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SignUpScreenTest : TestFunctions() {

    @get:Rule

    private lateinit var firstNameField: SemanticsMatcher
    private lateinit var lastNameField: SemanticsMatcher
    private lateinit var emailField: SemanticsMatcher
    private lateinit var passwordField: SemanticsMatcher
    private lateinit var createAccountButton: SemanticsMatcher
    private lateinit var loginRedirectText: SemanticsMatcher
    private lateinit var emptyFieldsError: String

    @Before
    override fun setUp() {
        super.setUp()

        firstNameField = hasText(ctx.getString(R.string.first_name_field_hint_text))
        lastNameField = hasText(ctx.getString(R.string.last_name_field_hint_text))
        emailField = hasText(ctx.getString(R.string.email_field_hint_text))
        passwordField = hasText(ctx.getString(R.string.password_field_hint_text))
        createAccountButton = hasText(ctx.getString(R.string.create_account_button_text)) and hasClickAction()
        loginRedirectText = hasText(ctx.getString(R.string.already_have_account_text)) and hasClickAction()
        emptyFieldsError = ctx.getString(R.string.signup_empty_fields_error)

        click_sign_up()
    }

    @Test
    fun check_initial_ui_state() {
        rule.onNode(firstNameField).assertExists()
        rule.onNode(lastNameField).assertExists()
        rule.onNode(emailField).assertExists()
        rule.onNode(passwordField).assertExists()
        rule.onNode(createAccountButton).assertExists()
        rule.onNode(loginRedirectText).assertExists()
    }

    @Test
    fun show_error_when_fields_are_empty() {
        rule.onNode(createAccountButton).performClick()
        rule.onNodeWithText(emptyFieldsError).assertExists()
    }

    @Test
    fun typing_updates_fields() {
        val firstName = "Alice"
        val lastName = "Smith"
        val email = "alice@example.com"
        val password = "password123"

        rule.onNode(firstNameField).performTextInput(firstName)
        rule.onNode(lastNameField).performTextInput(lastName)
        rule.onNode(emailField).performTextInput(email)
        rule.onNode(passwordField).performTextInput(password)

        rule.onNodeWithText(firstName).assertExists()
        rule.onNodeWithText(lastName).assertExists()
        rule.onNodeWithText(email).assertExists()
        // Password field is hidden (visual transformation), skip direct match
    }

    @Test
    fun navigate_to_login_screen_on_link_click() {
        rule.onNode(loginRedirectText).performClick()

        val loginField = hasText(ctx.getString(R.string.email_field_hint_text))

        rule.waitUntil(timeoutMillis = 5_000) {
            rule.onAllNodes(loginField).fetchSemanticsNodes().isNotEmpty()
        }

        rule.onNode(loginField).assertIsDisplayed()
    }
}
