package repository;

import exception.RepositoryException;
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

public class ParticipantDBRepository implements IParticipantDBRepository {
    private static final Logger logger= LogManager.getLogger();
    private Session session;

    public ParticipantDBRepository() {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
        SessionFactory factory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        session = factory.openSession();
    }

    @Override
    public Participant add(Participant entity) {
        Transaction transaction = session.beginTransaction();
        Long id = (Long) session.save(entity);
        entity.setId(id);
        transaction.commit();
        return entity;
    }

    @Override
    public void delete(Participant entity) {

    }

    @Override
    public void update(Participant entity, Long aLong) {

    }

    @Override
    public Participant findById(Long id) throws RepositoryException {
        Transaction transaction = session.beginTransaction();
        Participant entity = session.get(Participant.class, id);
        transaction.commit();
        return entity;
    }

    @Override
    public Iterable<Participant> getAll() {
        Query query = session.createQuery("from Participant");
        List<Participant> entities = query.list();
        return entities;
    }
}
