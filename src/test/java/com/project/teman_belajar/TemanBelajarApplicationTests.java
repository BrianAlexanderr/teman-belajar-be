package com.project.teman_belajar;

import com.project.teman_belajar.module.object_storage.service.ObjectStorageService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
class TemanBelajarApplicationTests {

	@MockitoBean
	private ObjectStorageService objectStorageService;

	@Test
	void contextLoads() {
	}

}
