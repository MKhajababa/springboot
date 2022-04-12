package com.contactmanager.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.contactmanager.dao.ContactRepository;
import com.contactmanager.dao.UserRepository;
import com.contactmanager.entities.Contact;
import com.contactmanager.entities.User;

@RestController
public class SearchController {
	
	@Autowired
	private ContactRepository contactRepository;
	@Autowired
	private UserRepository userRepository;
	
	
	@GetMapping("/search/{query}")
	public ResponseEntity<List<Contact>>  contactSearch(@PathVariable("query") String query,Principal principal) {
		
		User user = this.userRepository.getUserByUserName(principal.getName());
		
		List<Contact> contacts=this.contactRepository.findByNameContainingAndUser(query, user);
		
		
		return ResponseEntity.ok(contacts);
	}
}
