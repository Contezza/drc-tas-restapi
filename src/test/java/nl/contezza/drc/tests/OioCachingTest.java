package nl.contezza.drc.tests;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import lombok.extern.log4j.Log4j2;
import nl.contezza.drc.rest.RestTest;

@Log4j2
public class OioCachingTest extends RestTest {

	/**
	 * Create necessary dependencies when creating enkelvoudiginformatieobject.
	 */
	@BeforeTest(groups = "OioCaching")
	public void init() {

	}

	// TODO: create test
	@Test(groups = "OioCaching")
	public void test_oio_get_cache_header() {

	}

	// TODO: create test
	@Test(groups = "OioCaching")
	public void test_oio_head_cache_header() {

	}

	// TODO: create test
	@Test(groups = "OioCaching")
	public void test_head_in_apischema() {

	}

	// TODO: create test
	@Test(groups = "OioCaching")
	public void test_conditional_get_304() {

	}

	// TODO: create test
	@Test(groups = "OioCaching")
	public void test_conditional_get_stale() {

	}

}
