package com.example.timesheetapp

import android.content.Context
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import org.junit.Before
import org.junit.Rule

open abstract class TestFunctions {
    @get:Rule

    val rule = createAndroidComposeRule<MainActivity>()
    protected lateinit var ctx: Context

    lateinit var signUpButton: SemanticsMatcher

    @Before
    open fun setUp() {
        ctx = rule.activity

        signUpButton =
            hasText(ctx.getString(R.string.sign_up_button_text)) and hasClickAction()
    }

    open fun click_sign_up(){
        rule.onNode(signUpButton).performClick()
    }

}