package sample.dao;

import com.oauth.constants.CommonConstant;
import com.oauth.dao.AuthenticationTokenDAO;
import com.oauth.data.AuthenticationToken;
import com.oauth.data.User;
import com.oauth.exception.BusinessException;
import com.oauth.authentication.algorithm.JWTSigner;
import com.oauth.service.RESTSpringSecurityService;
import org.springframework.transaction.annotation.Transactional;
import sample.controller.UserProfileController;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Repository;
import sample.model.UserSession;
import sample.service.UserProfileService;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@Repository
@Transactional
public class UserSessionIDAOImpl implements AuthenticationTokenDAO {


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


    @Autowired
    private UserProfileDAO userProfileDAO;

    @Autowired
    private UserProfileService userProfileService;
    /**
     * The locale.
     */
    Locale locale = LocaleContextHolder.getLocale();

    /**
     * The session factory.
     */
    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private RESTSpringSecurityService restSpringSecurityService;

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

    //TODO:Move to spring security
    @SuppressWarnings("unchecked")
    @Override
    public AuthenticationToken find(String token) {
        logger.info("Entering in UserSessionIDAOImpl findByAccessToken method");
        Criteria criteria = getCurrentSession().createCriteria(UserSession.class);
        criteria.add(Restrictions.eq("token", token));
        ArrayList<UserSession> userSessionList = (ArrayList<UserSession>) criteria.list();
        if (userSessionList != null && !userSessionList.isEmpty()) {
            UserSession userSession = userSessionList.get(0);
            logger.info("UserSesion Exiting ");
            return new AuthenticationToken(userSession.getToken_id(), userSession.getToken(), userSession.getExpiryDateTime(), userSession.getUserId().getId());
        }
        return null;
    }

    @Override
    public AuthenticationToken generate(String username) {
        logger.info("Entering createSession() of UserProfileServiceImpl ");
        User user = userProfileDAO.fetchUser(username);
        if (user != null) {
            System.out.println("..............................Generate Token................................");
            System.out.println(restSpringSecurityService.generateToken());
            UserSession session = new UserSession();
            session.setUserId(userProfileService.fetchUser(username));
            Calendar cal = Calendar.getInstance(); // creates calendar
            cal.setTime(new Date()); // sets calendar time/date
            cal.add(Calendar.HOUR_OF_DAY, 6); // adds six hour

            // Sets the time in UTC
            SimpleDateFormat sdf = new SimpleDateFormat();
            sdf.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));
            sdf.setCalendar(cal);
            session.setExpiryDateTime(new Timestamp(sdf.getCalendar().getTime().getTime()));
            // returns new date object, six hour in the future
            Map<String, Object> claims = new HashMap<String, Object>();
            claims.put(CommonConstant.ISS, "raastech");
            claims.put(CommonConstant.SUB, "raastech");
            claims.put(CommonConstant.EXP, sdf.getCalendar().getTime().getTime());

            JWTSigner jwt = new JWTSigner("salt");
            String token = jwt.sign(claims);
            session.setToken(token);
            Integer id = (Integer) getCurrentSession().save(session);
            return new AuthenticationToken(token);
        }
        logger.info("Exiting createSession() of UserProfileServiceImpl ");
        return null;
    }
}
