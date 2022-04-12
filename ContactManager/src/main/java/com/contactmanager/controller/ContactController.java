package com.contactmanager.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.contactmanager.dao.UserRepository;
import com.contactmanager.entities.User;
import com.contactmanager.helper.Message;
@Controller
public class ContactController {
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	@Autowired
	private UserRepository userRepository;
	@GetMapping("/")
	
	public String HomePage(Model model) {
		model.addAttribute("title","Home-Contact Manager");
		return "Home";
	}
	@GetMapping("/about")
	
	public String About(Model model) {
		model.addAttribute("title","About-Contact Manager");
		return "about";
	}
	@GetMapping("/signup")
	
	public String Signup(Model model) {
		model.addAttribute("title","Sign Up-Contact Manager");
		model.addAttribute("user",new User());
		return "signup";
	}
	
	
	
	@PostMapping("/get_register")
	public String register(@Valid @ModelAttribute("user") User user,BindingResult res,@RequestParam("profilepic") MultipartFile file,@RequestParam(value = "checkin",defaultValue = "false") boolean check,Model model,HttpSession session) {
		
		
		
		try {
			
			if(!check) {
				throw new Exception("You are not agreed to the terms and conditions.");
			}
			
			if(res.hasErrors()) {
				
			model.addAttribute("user",user);
			return Signup(model);
			}
			if(file.isEmpty()) {
				System.out.println("File is empty.");
				user.setImageUrl("profile.png");
			}
			else {
				
				user.setImageUrl(file.getOriginalFilename());
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
			}
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			System.out.println(check);
			System.out.println(user);
			User result = this.userRepository.save(user);
			model.addAttribute("user", new User());
			
			session.setAttribute("Msg", new Message("alert-success","You are Successfully registered."));
			return HomePage(model);
			
		}
		catch(Exception e) {
		e.printStackTrace();
		model.addAttribute("user", user);
		session.setAttribute("Msg", new Message("alert-danger","Oops!Something went wrong" + e.getMessage()));
		}
		return "signup";
	}
	
	@GetMapping("/loging")
	public String customLogin(Model model) {
		model.addAttribute("title","Sign Up-Contact Manager");
		
		return "login";
	}
}
