package com.example.timesheetapp;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite::class)
@Suite.SuiteClasses(
    LoginScreenTest::class,
    SignUpScreenTest::class
)
class TestSuite {
}
