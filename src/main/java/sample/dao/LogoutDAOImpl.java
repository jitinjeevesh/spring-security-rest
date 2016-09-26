package sample.dao;

import com.oauth.dao.LogoutDAO;
import com.oauth.data.User;
import com.oauth.data.UserRole;
import com.oauth.exception.BusinessException;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sample.controller.UserProfileController;
import sample.model.UserProfile;
import sample.model.UserSession;

import java.util.ArrayList;
import java.util.List;

@Repository
public class LogoutDAOImpl implements LogoutDAO {

    private final static Logger logger = LoggerFactory.getLogger(LogoutDAOImpl.class);
    @Autowired
    private SessionFactory sessionFactory;

    private Session getCurrentSession() throws BusinessException {
        if (sessionFactory != null && sessionFactory.getCurrentSession() != null) {
            return sessionFactory.getCurrentSession();
        }
        return null;

    }

    @Override
    @Transactional
    public void session(String token) {
        Criteria criteria = getCurrentSession().createCriteria(UserSession.class);
        criteria.add(Restrictions.eq("token", token));
        List<UserSession> userSessions = (ArrayList<UserSession>) criteria.list();
        if (userSessions != null && !userSessions.isEmpty()) {
            getCurrentSession().delete(userSessions.get(0));
        }
    }

    @Override
    @Transactional
    public void user(String username) {
        Criteria criteria = getCurrentSession().createCriteria(UserProfile.class);
        criteria.add(Restrictions.eq("userMail", username));
        List<UserProfile> usersList = (ArrayList<UserProfile>) criteria.list();
        if (usersList != null && !usersList.isEmpty()) {
            UserProfile userProfile = usersList.get(0);
            Criteria criteria1 = getCurrentSession().createCriteria(UserSession.class);
            System.out.println(userProfile.getId());
            criteria1.add(Restrictions.eq("userId.id", userProfile.getId()));
            List<UserSession> userSessions = (ArrayList<UserSession>) criteria1.list();
            if (userSessions != null && !userSessions.isEmpty()) {
                for (UserSession userSession : userSessions) {
                    getCurrentSession().delete(userSession);
                }
            }
        }
    }
}
