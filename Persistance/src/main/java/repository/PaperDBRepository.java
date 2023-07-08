package repository;

import exception.RepositoryException;
import model.Grade;
import model.Paper;
import model.Participant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;

import java.util.List;

public class PaperDBRepository implements IPaperDBRepository {
    private static final Logger logger= LogManager.getLogger();
    private Session session;

    public PaperDBRepository() {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
        SessionFactory factory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        session = factory.openSession();
    }

    @Override
    public Paper add(Paper entity) {
        Transaction transaction = session.beginTransaction();
        Long id = (Long) session.save(entity);
        entity.setId(id);
        transaction.commit();
        return entity;
    }

    @Override
    public void delete(Paper entity) {

    }

    @Override
    public void update(Paper entity, Long aLong) {

    }

    @Override
    public Paper findById(Long id) throws RepositoryException {
        Transaction transaction = session.beginTransaction();
        Paper entity = session.get(Paper.class, id);
        transaction.commit();
        return entity;
    }

    @Override
    public Iterable<Paper> getAll() {
        Query query = session.createQuery("from Paper");
        List<Paper> entities = query.list();
        return entities;
    }
}
