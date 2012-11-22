/**
 * Copyright (C) 2011 ArtiVisi Intermedia <info@artivisi.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.artivisi.training.ui.controller;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.with;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import groovyx.net.http.ContentType;

import org.junit.Test;

import com.artivisi.training.domain.ApplicationConfig;

public class ApplicationConfigControllerTestIT {

    private String target = "http://localhost:10000/config/";

	@Test public void testSaveUpdateDelete(){

		String id = testSave(target);
		System.out.println("Id : "+id);
		testGetExistingById(id, "coba", "Konfigurasi Percobaan", "test");
		testUpdateExisting(id, "coba", "Konfigurasi Percobaan 001", "test123");
		testGetExistingById(id, "coba", "Konfigurasi Percobaan 001", "test123");
		testDeleteExistingById(id);
	}

	private String testSave(String target) {
		ApplicationConfig config = new ApplicationConfig();
		config.setName("coba");
		config.setLabel("Konfigurasi Percobaan");
		config.setValue("test");

		String location =
		given().
		body(config).contentType(ContentType.JSON).
		expect().
		statusCode(201).
		when().post(target).getHeader("Location");

		assertNotNull(location);
		assertTrue(location.startsWith(target));

		String[] locationSplit = location.split("/");
		String id = locationSplit[locationSplit.length - 1];

		return id;
	}

	private void testGetExistingById(String id, String name, String label, String value){
		expect().
		statusCode(200).
		body(
				"name", equalTo(name),
				"label", equalTo(label),
				"value", equalTo(value)
		).
		when().get(target+id);
	}

	private void testUpdateExisting(String id, String name, String label, String value){
		ApplicationConfig config = new ApplicationConfig();
		config.setName(name);
		config.setLabel(label);
		config.setValue(value);

		given().body(config).contentType(ContentType.JSON).
		expect().
		statusCode(200).
		when().put(target+id);
	}

	private void testDeleteExistingById(String id){
		expect().
		statusCode(200).
		when().delete(target+id);

		expect().
		statusCode(404).
		when().get(target+id);
	}

	@Test
	public void testGetExistingConfigById(){
		expect().
		statusCode(200).
		body(
				"id", equalTo("abc123"),
				"name", equalTo("applicationname"),
				"label", equalTo("Application Name"),
				"value", equalTo("Belajar Restful")
		).
		when().get(target+"abc123");
	}

	@Test
	public void testGetNonExistentConfigById(){
		expect().
		statusCode(404).
		when().get(target+"/nonexistentconfig");
	}

	@Test
	public void testFindAll(){
		with()
		.header("Range", "items=0-5")
		.expect()
		.statusCode(200)
		.header("Content-Range", "items 0-1/2")
		.body(
				"id", hasItems("abc123", "def456")
		)
		.when().get(target);
	}
}
