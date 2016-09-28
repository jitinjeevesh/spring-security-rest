package sample.service;


import com.oauth.constants.CommonConstant;
import com.oauth.data.User;
import com.oauth.data.UserRole;
import com.oauth.exception.AuthenticationException;
import com.oauth.exception.BusinessException;
import com.oauth.exception.ErrorCodes;
import com.oauth.service.RESTSecurityUserDetails;
import com.oauth.service.RESTSpringSecurityService;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import sample.dao.UserProfileDAO;
import sample.model.UserProfile;
import sample.model.UserSession;
import sample.controller.UserProfileController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

// TODO: Auto-generated Javadoc

/**
 * The Class UserProfileServiceImpl.
 */
@Service
public class UserProfileServiceImpl implements UserProfileService {

    /**
     * Logger
     */
    private final static Logger logger = LoggerFactory.getLogger(UserProfileController.class);


    /**
     * The message source.
     */
    @Autowired
    @Qualifier(value = "messageSource")
    MessageSource messageSource;

    /**
     * The locale.
     */
    Locale locale = LocaleContextHolder.getLocale();

    /**
     * The user profile dao.
     */
    @Autowired
    public UserProfileDAO userProfileDAO;
    @Autowired
    private RESTSpringSecurityService restSpringSecurityService;

    /**
     * The iss.
     */
    @Value("${iss}")
    private String iss;

    /**
     * The sub.
     */
    @Value("${sub}")
    private String sub;

    /**
     * The salt key.
     */
    @Value("${saltKey}")
    private String saltKey;

    @Value("${expirationTime}")
    private Integer expirationTime;


    /**
     * The session factory.
     */
    @Autowired
    private SessionFactory sessionFactory;

    /**
     * Gets the current session.
     *
     * @return the current session
     * @throws BusinessException the business exception
     */
    private Session getCurrentSession() throws BusinessException {
        if (sessionFactory != null && sessionFactory.getCurrentSession() != null) {
            return sessionFactory.getCurrentSession();
        }
        return null;

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.raastech.mobile.rest.service.UserProfileService#doLogin(com.raastech.mobile.rest.entity
     * .Users)
     */
    @Override
    public UserSession doLogin(UserProfile userProfile, String deviceId) {
        logger.info("Entering doLogin() of UserProfileServiceImpl ");
        sample.model.UserRole userRole = userProfile.getUserRole();
        UserRole userRoleDTO = new UserRole(userRole.getRoleId(), userRole.getUserRole());
        User userProfileDTO = new User(userProfile.getId(), userProfile.getUserMail(), userProfile.getFirstName(), userProfile.getLastName(), userProfile.getPassword(), userRoleDTO);
        String result = userProfileDAO.getLogin(userProfileDTO);
        UserSession token = null;
        if (result.equals(CommonConstant.OK)) {
            return createSession(userProfile, token, deviceId);
        } else if (result.equals(CommonConstant.LOCKED)) {
            logger.info("Entering doLogin() of UserProfileServiceImpl ");
            throw new AuthenticationException(messageSource.getMessage(
                    CommonConstant.LOCKED_USER, null, locale), ErrorCodes.CODE_AUTHENTICATION_EXCEPTION);
        } else if (result.equals(CommonConstant.INVALID_WARNING)) {
            logger.info("Entering doLogin() of UserProfileServiceImpl ");
            throw new AuthenticationException(messageSource.getMessage(CommonConstant.INVALID_WARNING, null, null),
                    ErrorCodes.CODE_INVALID_WARNING);
        } else {
            logger.info("Entering doLogin() of UserProfileServiceImpl ");
            throw new AuthenticationException(messageSource.getMessage(
                    CommonConstant.INVALID_USERNAME_AND_PASSWORD, null, null), ErrorCodes.CODE_INVALID_USERNAME_AND_PASSWORD);
        }

    }


    @SuppressWarnings("unchecked")
    @Override
    @Transactional
    public UserProfile fetchUser(String username) {
        logger.info("Entering in UserProfileDAOImpl fetchUser method");
        Criteria criteria = getCurrentSession().createCriteria(UserProfile.class);
        criteria.add(Restrictions.eq("userMail", username));
        List<UserProfile> usersList = (ArrayList<UserProfile>) criteria.list();
        if (usersList != null && !usersList.isEmpty()) {
            logger.info("Exiting in UserProfileDAOImpl fetchUser method user is" + usersList.get(0));
            return usersList.get(0);
        }
        logger.info("Exiting in UserProfileDAOImpl getLogin method");
        return null;
    }


    @SuppressWarnings("unchecked")
    @Override
    @Transactional
    public Map register(String username, String password) {
        sample.model.UserRole userRole = (sample.model.UserRole) getCurrentSession().get(sample.model.UserRole.class, 1);
        UserProfile userProfile = new UserProfile();
        userProfile.setFirstName(username);
        userProfile.setLastName(username);
        userProfile.setUserMail(username);
        userProfile.setPassword(restSpringSecurityService.encodePassword(password));
        userProfile.setPhone("8586825188");
        userProfile.setUserRole(userRole);
        getCurrentSession().save(userProfile);
        final RESTSecurityUserDetails restSecurityUserDetails = restSpringSecurityService.authenticate(userProfile.getUserMail(), userProfile.getPassword());
        System.out.println("..................................Access token..........................................................");
        System.out.println(restSecurityUserDetails.getAccessToken());
        return new HashMap<String, Object>() {{
            put("username", restSecurityUserDetails.getUsername());
            put("accessToken", restSecurityUserDetails.getAccessToken().getToken());
        }};
    }


    //

    /**
     * Creates the session.the session is created on the basic of the device id if the device id i not available
     * in the database then the generated token is saved in the database and if it is available in database
     * then it is updated by the new token.
     *
     * @param userProfile the users
     * @param token       the token
     * @param deviceId    the device id
     * @return the response entity
     */
    private UserSession createSession(UserProfile userProfile, UserSession token, String deviceId) {
        UserSession session = null;
  /*  logger.info("Entering createSession() of UserProfileServiceImpl ");
    UserRole userRole = userProfile.getUserRole();
    UserRole userRoleDTO = new UserRole(userRole.getRoleId(), userRole.getUserRole());
    User userProfileDTO= new User(userProfile.getId(), userProfile.getUserMail(), userProfile.getFirstName(), userProfile.getLastName(), userProfile.getPassword(), userRoleDTO);

    User user = userProfileDAO.fetchUser(userProfileDTO);
    if (user != null) {
      logger.info("Entered createSession() inside for session****************************" + userProfile);
      session = new UserSession();
      session.setUsername(user);;
      Calendar cal = Calendar.getInstance(); // creates calendar
      cal.setTime(new Date()); // sets calendar time/date
      cal.add(Calendar.HOUR_OF_DAY, expirationTime); // adds six hour
     
     // Sets the time in UTC
      SimpleDateFormat sdf = new SimpleDateFormat();
      sdf.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));
      sdf.setCalendar(cal);
      session.setExpiryDateTime(new Timestamp(sdf.getCalendar().getTime().getTime()));
   // returns new date object, six hour in the future
      Map<String, Object> claims = new HashMap<String, Object>();
      claims.put(CommonConstant.ISS, iss);
      claims.put(CommonConstant.SUB, sub);
      claims.put(CommonConstant.EXP, sdf.getCalendar().getTime().getTime());
      
      JWTSigner jwt = new JWTSigner(saltKey);
      session.setToken(jwt.sign(claims));
      session = userProfileDAO.updateUserSession(session);
      if (session != null) {
    	return session;
      }
    }
    logger.info("Exiting createSession() of UserProfileServiceImpl ");*/
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.raastech.mobile.rest.service.UserProfileService#getUsersObject(javax.servlet.http.
     * HttpServletRequest)
     */
    @Override
    public UserProfile getUsersObject(HttpServletRequest request) {
/*	  logger.info("Entering createSession() of UserProfileServiceImpl ");
    //validate secret token
    UserSession session = userProfileDAO.validateToken(request.getHeader(CommonConstant.SECRET_TOKEN));
    if (session != null) {
    	UserProfile user = userProfileDAO.fetchUserByUserId(session.getUsername().getId());
      if(user!=null){
    	  logger.info("Exiting createSession() of UserProfileServiceImpl ");
        return user;
      }
    }
    logger.info("Exiting createSession() of UserProfileServiceImpl user object not found");
    throw new ObjectNotFoundException(CommonConstant.USER_OBJECT_NOT_FOUND,
        ErrorCodes.OBJECT_NOT_FOUND);*/
        return null;
    }

    /* (non-Javadoc)
     * @see com.raastech.mobile.rest.service.UserProfileService#validateSecretKey(java.lang.String)
     */
    @Override
    @Transactional
    public String validateSecretKey(String token) {
     /* logger.info("Entering createSession() of UserProfileServiceImpl ");
    UserSession session = userProfileDAO.validateToken(token);
    if (session != null) {
    	  
    	 Calendar cal = Calendar.getInstance(); // creates calendar
         cal.setTime(new Date()); // sets calendar time/date
         cal.add(Calendar.HOUR_OF_DAY, expirationTime); // adds six hour
      
        // Sets the time in UTC
         SimpleDateFormat sdf = new SimpleDateFormat();
         sdf.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));
         sdf.setCalendar(cal);
         long expireTime = sdf.getCalendar().getTime().getTime();
		 session.setExpiryDateTime(new Timestamp(expireTime));
    	 Map<String, Object> claims = new HashMap<String, Object>();
         claims.put(CommonConstant.ISS, iss);
         claims.put(CommonConstant.SUB, sub);
         claims.put(CommonConstant.EXP, expireTime);
    	JWTSigner jwt = new JWTSigner(saltKey);
        session.setToken(jwt.sign(claims));
        session = userProfileDAO.updateUserSession(session);
        logger.info("Entering createSession() of UserProfileServiceImpl got session object"+session);
        return session.getToken();
    }
    logger.info("Exiting createSession() of UserProfileServiceImpl ");*/
        return null;
    }


}
