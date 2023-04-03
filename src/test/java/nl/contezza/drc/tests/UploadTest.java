package nl.contezza.drc.tests;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import lombok.extern.log4j.Log4j2;
import nl.contezza.drc.rest.RestTest;

@Log4j2
public class UploadTest extends RestTest {

	/**
	 * Create necessary dependencies when creating enkelvoudiginformatieobject.
	 */
	@BeforeTest(groups = "Upload")
	public void init() {

	}

	// TODO: create test
	@Test(groups = "Upload")
	public void test_create_eio() {

	}

	// TODO: create test
	// @Test(groups = "Upload")
	public void test_create_without_file() {

	}

	// TODO: create test
	// @Test(groups = "Upload")
	public void test_create_empty_file() {

	}

	// TODO: create test
	// @Test(groups = "Upload")
	public void test_create_without_size() {

	}

	// TODO: create test
	// @Test(groups = "Upload")
	public void test_update_eio_metadata() {

	}

	// TODO: create test
	// @Test(groups = "Upload")
	public void test_update_eio_file() {

	}

	// TODO: create test
	// @Test(groups = "Upload")
	public void test_update_eio_file_set_empty() {

	}

	// TODO: create test
	// @Test(groups = "Upload")
	public void test_update_eio_only_size() {

	}

	// TODO: create test
	// @Test(groups = "Upload")
	public void test_update_eio_only_file_without_size() {

	}

	// TODO: create test
	// @Test(groups = "Upload")
	public void test_update_eio_put() {

	}

	// TODO: create test
	// @Test(groups = "Upload")
	public void test_create_eio_full_process() {

	}

	// TODO: create test
	// @Test(groups = "Upload")
	public void test_upload_part_wrong_size() {

	}

	// TODO: create test
	// @Test(groups = "Upload")
	public void test_upload_part_twice_correct() {

	}

	// TODO: create test
	// @Test(groups = "Upload")
	public void test_unlock_without_uploading() {

	}

	// TODO: create test
	// @Test(groups = "Upload")
	public void test_unlock_not_finish_upload() {

	}

	// TODO: create test
	// @Test(groups = "Upload")
	public void test_update_metadata_without_upload() {

	}

	// TODO: create test
	// @Test(groups = "Upload")
	public void test_update_metadata_after_unfinished_upload() {

	}

	// TODO: create test
	// @Test(groups = "Upload")
	public void test_update_metadata_set_size() {

	}

	// TODO: create test
	// @Test(groups = "Upload")
	public void test_update_metadata_set_size_zero() {

	}

	// TODO: create test
	// @Test(groups = "Upload")
	public void test_update_metadata_set_size_null() {

	}
}
