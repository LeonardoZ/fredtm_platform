package com.fredtm.core;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
		OperationTest.class, ActivityTest.class,
		TimeActivityTest.class, FormatElapsedTimeTest.class,
		ValidationTest.class })
public class AllTests {

}