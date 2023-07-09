package repository;
import exception.RepositoryException;
import model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;
import java.util.*;

public class UserDBRepository implements IUserRepository{

    private static final Logger logger= LogManager.getLogger();
    private Session session;

    public UserDBRepository() {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
        SessionFactory factory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        session = factory.openSession();
    }

    @Override
    public User add(User entity) {
        Transaction transaction = session.beginTransaction();
        Long id = (Long) session.save(entity);
        entity.setId(id);
        transaction.commit();
        return entity;
    }

    @Override
    public void delete(User user) {
        logger.traceEntry();
        Transaction transaction = session.beginTransaction();
        session.delete(user);
        transaction.commit();
        logger.traceExit();
    }

    @Override
    public void update(User entity, Long id) {
        logger.traceEntry();
        Transaction transaction = session.beginTransaction();
        entity.setId(id);
        session.update(entity);
        transaction.commit();
        logger.traceExit();
    }

    @Override
    public User findById(Long idToFind) throws RepositoryException {
        logger.traceEntry();
        Transaction transaction = session.beginTransaction();
        User entity = session.get(User.class, idToFind);
        transaction.commit();
        return entity;
    }

    @Override
    public Iterable<User> getAll() {
        //todo fix original template funtion with hinernate queries
        Query query = session.createQuery("from User");
        List<User> entities = query.list();
        return entities;
    }

    @Override
    public User findUserByUsername(String username) throws RepositoryException {
        Query query = session.createQuery("from User where username = :u");
        query.setParameter("u", username);
        User user = (User) query.uniqueResult();

        if (user == null) {
            throw new RepositoryException("No user found!");
        }
        return user;
    }
}
