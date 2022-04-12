package com.contactmanager.dao;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.contactmanager.entities.Contact;
import com.contactmanager.entities.User;

public interface ContactRepository extends JpaRepository<Contact,Integer> {
	
	//In Pageable we have current Page and number of contacts per page values in it
	@Query("from Contact as c where c.user.id=:userId")
	public Page<Contact> findContactsByUsername(@Param("userId") Integer userId,Pageable pageble);
	
	//@Query("from Contact as c where c.user=:user and c.name=:name")
	//public List<Contact> findByNameContainingAndUser(@Param("name") String name,@Param("user") User user);
	public List<Contact> findByNameContainingAndUser(String name,User user);
	
}



