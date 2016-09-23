package sample.controller;

import com.oauth.constants.SecurityConstants;
import com.oauth.exception.ErrorCodes;
import com.oauth.exception.RequiredFieldMissingException;
import com.oauth.json.AccessToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sample.model.UserProfile;
import sample.model.UserSession;
import sample.service.UserProfileService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


// TODO: Auto-generated Javadoc

/**
 * The Class UserProfileController.
 */
@RestController
@RequestMapping("/api")
public class UserProfileController {

	public static final String REQUIRED_FIELD_MISSING = "Please provide all required fields";

	/** Logger */
	private final static Logger logger = LoggerFactory.getLogger(UserProfileController.class);


	/** The user profile service. */
	@Autowired
	public UserProfileService userProfileService;


	/**
	 * This API is used to do login and generate token for the user using Users and UserSession class. The token expiration  time is 6 hours
	 * This API also takes device id for the consuming parties where they will provide the unique device id with username and password.
	 * The same token will be returned id the provided device already exist in the database and if not they we will generate the token for the User.
	 *
	 * @param request the request
	 * @param response the response
	 * @param users the users
	 * @param deviceId the device id
	 * @return the response entity
	 */

	@RequestMapping(value = "/session/{deviceId}", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public AccessToken login(HttpServletRequest request,HttpServletResponse response, @RequestBody UserProfile users,@PathVariable String deviceId) {
		logger.info("Entering in UserProfileController login method");

		if(users.getUserMail()==null || users.getUserMail().trim().equals("") || users.getPassword()==null || users.getPassword().trim().equals("") || deviceId.trim().equals("")){
			throw new RequiredFieldMissingException(REQUIRED_FIELD_MISSING, ErrorCodes.MISSING_ARGUMENT);
		}
		logger.info("Exiting in UserProfileController login method");

		UserSession session = userProfileService.doLogin(users,deviceId);
		AccessToken token = new AccessToken(session.getToken(), null);

//		populateAccessTokenCookie(true, response,token);

//		System.out.println("Token : " + token);
		return token;
	}


	/**
	 * Logout.
	 *
	 * @param request the request
	 * @param response the response
	 */
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public void logout(HttpServletRequest request, HttpServletResponse response) {
		System.out.println("hiu");
	}


//	public static void populateAccessTokenCookie(boolean supportCookie, HttpServletResponse response, AccessToken body) {
//	    // supportCookie ?
//	    if (supportCookie && body != null && StringUtils.isEmpty(body.getAccessToken())) {
//	        CookieGenerator cookieGenerator = new CookieGenerator();
//	        cookieGenerator.setCookieSecure(true);
//
//	        cookieGenerator.setCookieName(PreAuthenitcationFilter.AUTH_TYPE_OAUTH);
//	        cookieGenerator.setCookiePath("/");
//	        long millis = body.getExpiresIn() - System.currentTimeMillis();
//	        cookieGenerator.setCookieMaxAge((int) TimeUnit.SECONDS.convert(millis, TimeUnit.MILLISECONDS));
//	        cookieGenerator.addCookie(response, body.getAccessToken());
//	    }
//	}
}