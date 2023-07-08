package repository;

import exception.RepositoryException;
import model.Grade;
import model.Paper;
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
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class GradeDBRepository implements IGradeDBRepository {
    private static final Logger logger= LogManager.getLogger();
    private Session session;

    public GradeDBRepository() {

        StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
        SessionFactory factory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        session = factory.openSession();
    }

    @Override
    public Grade add(Grade entity) {
        Transaction transaction = session.beginTransaction();
        Long id = (Long) session.save(entity);
        entity.setId(id);
        transaction.commit();
        return entity;
    }

    @Override
    public void delete(Grade entity) {

    }

    @Override
    public void update(Grade entity, Long aLong) {

    }

    @Override
    public Grade findById(Long id) throws RepositoryException {
        Transaction transaction = session.beginTransaction();
        Grade entity = session.get(Grade.class, id);
        transaction.commit();
        return entity;
    }

    @Override
    public Iterable<Grade> getAll() {
        Query query = session.createQuery("from Grade");
        List<Grade> entities = query.list();
        return entities;
    }
}
