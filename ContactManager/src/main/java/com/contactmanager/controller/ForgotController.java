package com.contactmanager.controller;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.contactmanager.Service.EmailService;
import com.contactmanager.dao.UserRepository;
import com.contactmanager.entities.User;

@Controller
public class ForgotController {
	@Autowired
	private EmailService emailService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BCryptPasswordEncoder bCrypt;
	
	@GetMapping("/forgotpassword")
	public String openEmail() {
		return "forgot_email_form";
	}
	@PostMapping("/send-otp")
	public String sendOtp(@RequestParam("email") String email,HttpSession session) {
		System.out.println(email);
		//generating random otp
		int rdn = 0;
		Random r = new Random();
		 while(true) {
			 int x = r.nextInt(999999);
			 if(x>100000) {
				 rdn = x;
				 break;
			 }
		 }
		
		String subject ="OTP from Contact Manager.";
		String msg = "The OTP for your change password is:" + rdn + ".Please don't share with others";
		 boolean flag =this.emailService.sendEmail(email, subject, msg);
		if(flag) {
			session.setAttribute("otp", rdn);
			session.setAttribute("email",email);
			
			return "verify_otp";
		}
		
		else {
			return "forgot_email_form";
		}
	}
	@PostMapping("/resend-otp")
	public String resendOtp(HttpSession session) {
		int rdn = 0;
		Random r = new Random();
		 while(true) {
			 int x = r.nextInt(999999);
			 if(x>100000) {
				 rdn = x;
				 break;
			 }
		 }
		String email = (String) session.getAttribute("email");
		String subject ="OTP from Contact Manager.";
		String msg = "The OTP for your change password is:" + rdn + ".Please don't share with others";
		 boolean flag =this.emailService.sendEmail(email, subject, msg);
		if(flag) {
			session.setAttribute("otp", rdn);
			session.setAttribute("email",email);
			
			return "verify_otp";
		}
		
		else {
			return "forgot_email_form";
		}
	}
		
		
	@PostMapping("/check-otp")
	public String checkOtp(@RequestParam("otp") Integer otp ,HttpSession session) {
		 
		Integer sotp = (int) session.getAttribute("otp");
		String email = (String)session.getAttribute("email");
		if(sotp.equals(otp)) {
			User user = this.userRepository.getUserByUserName(email);
			
			if(user==null) {
				return "forgot_email_form";
			}
			
			else {
				return "password_change_form";
			}
			}
			
		else {
			System.out.println("Newton");
			return "verify_otp";
		}
	}
	
	@PostMapping("/change")
	public String passwordChanging(@RequestParam("password") String pass,@RequestParam("password2") String pass2,HttpSession session) {
		
		if(pass.equals(pass2)) {
			String email = (String)session.getAttribute("email");
			User user = this.userRepository.getUserByUserName(email);
			user.setPassword(this.bCrypt.encode(pass));
			this.userRepository.save(user);
			return "redirect:/loging";
		}
		else {
		return "password_change_form";
		}
	}
	
}
