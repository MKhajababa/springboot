package com.contactmanager.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.contactmanager.dao.ContactRepository;
import com.contactmanager.dao.UserRepository;
import com.contactmanager.entities.Contact;
import com.contactmanager.entities.User;
import com.contactmanager.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ContactRepository contactRepository;
	@ModelAttribute
	public void addCommonData(Model model,Principal principal) {
		String userName = principal.getName();
		User user = userRepository.getUserByUserName(userName);
		model.addAttribute("user",user);
	}
	//Home dashboard
	@RequestMapping("/index")
	public String user_dashboard(Model model,Principal principal) {
		
		return "user_dashboard";
	}
	
	//Adding a contact
	@GetMapping("/add-contact")
	public String add_contact(Model model) {
		model.addAttribute("title","Add Contact");
		model.addAttribute("contact",new Contact());
		
		return "add_contact";
	}
	
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact,@RequestParam("profileimage") MultipartFile file,
			Principal principal,
			HttpSession session
			) {
		try {
		String name = principal.getName();
		User user = this.userRepository.getUserByUserName(name);
		
		if(file.isEmpty()) {
			System.out.println("File is empty.");
			contact.setImage("profile.png");
		}
		else {
			
			
			contact.setImage(file.getOriginalFilename());
			File saveFile = new ClassPathResource("static/img").getFile();
			Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
			Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
		}
		
		contact.setUser(user);
		user.getContacts().add(contact);
		this.userRepository.save(user);
		
		session.setAttribute("message", new Message("success","Contact is added succesfully."));
		
		
		
		
		}
		catch(Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Message("danger","Something went wrong!Please try again later."));
			
		}
		return "add_contact";
	}
	
	//show contacts
	
	@GetMapping("/contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page,Model model,Principal principal) {
		model.addAttribute("title","show contacts");
		String name = principal.getName();
		User user = this.userRepository.getUserByUserName(name);
		Pageable pageb = PageRequest.of(page, 5);
		Page<Contact> listing = this.contactRepository.findContactsByUsername(user.getId(),pageb);
		model.addAttribute("conts",listing);
		model.addAttribute("currentpage",page);
		model.addAttribute("totalpages",listing.getTotalPages());
		
		
		System.out.println(listing);
		return "showcontacts";
	}
	
	@GetMapping("/contactdetail/{id}")
	public String contactDetails(@PathVariable("id") Integer id,Model model,Principal principal) {
		
		Contact contact = contactRepository.getById(id);
		
		String userName= principal.getName();
		
		User user = this.userRepository.getUserByUserName(userName);
		
		if(user.getId()==contact.getUser().getId()) {
			model.addAttribute("contact",contact);

		}
		
		
		return "contact_detail";
	}
	
	@GetMapping("/deletecontact/{id}")
	public String deleteContact(@PathVariable("id") Integer id,Model model,Principal principal) {
		
		Optional<Contact> contactOptional = this.contactRepository.findById(id);
		Contact contact = contactOptional.get();
		contact.setUser(null);
		
		this.contactRepository.delete(contact);
		
		
		return "redirect:/user/contacts/0";
	}
	
	@PostMapping("/update/{id}")
	
	public String updateContact(@PathVariable("id") Integer id,Model model) {
		
		model.addAttribute("title","Update Contact");
		Contact contact = this.contactRepository.findById(id).get();
		model.addAttribute("contact",contact);
		
		return "contact_update";
	}
	
	@PostMapping("/update-processing")
	public String updateChange(@ModelAttribute Contact contact,@RequestParam("profileimage") MultipartFile file,
			Principal principal,
			HttpSession session) {
		try {
			
			Contact oldcontact =this.contactRepository.getById(contact.getcId());
			File deleteFile = new ClassPathResource("static/img").getFile();
			File file1 = new File(deleteFile,oldcontact.getImage());
			file1.delete();
			
		if(file.isEmpty()) {
			System.out.println("File is empty.");
			contact.setImage("profile.png");
		}
		else {
			
			
			contact.setImage(file.getOriginalFilename());
			File saveFile = new ClassPathResource("static/img").getFile();
			Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
			Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
		}
		
		String name = principal.getName();
		User user = this.userRepository.getUserByUserName(name);
		contact.setUser(user);
		
		this.contactRepository.save(contact);
		session.setAttribute("message", new Message("success","Contact is updated succesfully."));
		}
		catch (Exception e) {
			
			session.setAttribute("message", new Message("danger","Something went wrong!Please try again later."));
		}
		
		return "redirect:/user/contacts/0";
	}
	
	
	@GetMapping("/profile")
	
	public String profilePage(Model model,Principal principal) {
		
		return "profile";
	}
	
	
	//user settings
	@GetMapping("/settings")
	public String changeSettings() {
	
		return "setting";
	}
	
	@PostMapping("/password-change")
	public String passwordChange(@RequestParam("oldpassword") String oldpassword,@RequestParam("newpassword") String newpassword,Principal principal,HttpSession session) {
		
		System.out.println("Old Password="+oldpassword);
		System.out.println("New Password="+newpassword);
		User user = this.userRepository.getUserByUserName(principal.getName());
		
		
		if(this.passwordEncoder.matches(oldpassword, user.getPassword())) {
			user.setPassword(this.passwordEncoder.encode(newpassword));
			this.userRepository.save(user);
			session.setAttribute("message", new Message("success","Your password is successfully updated..."));
		}
		else {
			session.setAttribute("message", new Message("danger","Your hav entered wrong password!!!!!"));
			return "redirect:/user/settings";
		}
		return "redirect:/user/index";
	}
	
}
