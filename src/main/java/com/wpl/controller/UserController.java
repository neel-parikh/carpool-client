package com.wpl.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import com.wpl.model.User;
import com.wpl.model.ViewRide;

@Controller
@SessionAttributes({"user" , "uride"})
public class UserController
{
	@Autowired
	private SearchController sc;
	
	@RequestMapping(value="/login",method=RequestMethod.POST) 
	public String checkUser(@RequestParam("userId") String userId,
			@RequestParam("password") String password, ModelMap model,HttpServletRequest req)
	{
		RestTemplate template = new RestTemplate();
		//String url = "https://localhost:8180/user/checkUser?oauth_token="+req.getAttribute("oauth_token");
		String url = "https://localhost:8180/user/checkUser";
		/*Map<String,String> params = new HashMap<String,String>();
		params.put("userId", userId);
		params.put("password", password);-
		*/
		User user = new User();
		List<ViewRide> vr = new ArrayList<ViewRide>();
		user.setUserId(userId);
		user.setPassword(password);
		Boolean result = template.postForObject(url, user,Boolean.class);
		model.addAttribute("result",result);
		if(!result)
			return "login";			
		else
		{
			user = getUser(userId);
			vr = sc.getRide(userId);
			if(vr!=null)
				model.addAttribute("uride",vr);
			model.addAttribute("user",user);
			System.out.println(user.getEmailId()+"   "+user.getFirstName());
			return "profile";
		}
	}
	
	@RequestMapping(value="/register", method=RequestMethod.POST)
	public String registerUser(@ModelAttribute("register") User user, ModelMap map){
		String url = "https://localhost:8180/user/createUser";
		RestTemplate template = new RestTemplate();
		User valid_user = getUser(user.getUserId());
		if(valid_user==null){
			template.postForEntity(url, user, Boolean.class);
			map.addAttribute("success","true");
			return "login";
		}else{
			map.addAttribute("success","false");
			return "login";
		}
	}
	
	@RequestMapping(value="/getProfile",method=RequestMethod.GET,headers="Accept=application/json")
	public User getUser(@RequestParam("userId") String userId)
	{
		String url = "https://localhost:8180/user/getUser?userId={userId}";
		RestTemplate template = new RestTemplate();
		Map<String,String> params = new HashMap<String,String>();
		params.put("userId", userId);
		ResponseEntity<User> user = template.getForEntity(url, User.class,params);
		return user.getBody();
	}
	
	@RequestMapping(value="/updateProfile",method=RequestMethod.POST)
	public @ResponseBody String updateProfile(@RequestBody User user)
	{
		String url = "https://localhost:8180/user/updateUserProfile";
		RestTemplate template = new RestTemplate();
		/*Map<String,String> params = new HashMap<String,String>();
		params.put("userId", userId);
		params.put("firstName", firstName);
		params.put("lastName", lastName);
		params.put("emailId", emailId);
		params.put("phoneNo", phoneNo);*/
		/*User user = new User();
		user.setUserId(userId);
		user.setEmailId(emailId);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setPhoneNo(phoneNo);
		user.setEmailId(emailId);*/
		System.out.println(user.getEmailId());
		System.out.println(user.toString());
		Boolean result = template.postForObject(url, user,Boolean.class);
		System.out.println("Called");
		return "Profile Updated Successfully";
	}
	
	@RequestMapping(value="/logout")
	public String logout(HttpServletRequest request, SessionStatus status) {
		
		HttpSession session = request.getSession();
		System.out.println(session.getId());
		status.setComplete();
		session.invalidate();
        System.out.println(session.getId());
        RequestMappingHandlerAdapter rmha = new RequestMappingHandlerAdapter();
        rmha.setCacheSeconds(0);
		return "login";
	}
}