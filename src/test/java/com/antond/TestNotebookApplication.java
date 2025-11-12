package com.antond;

import org.springframework.boot.SpringApplication;

public class TestNotebookApplication {

	public static void main(String[] args) {
		SpringApplication.from(NotebookApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
