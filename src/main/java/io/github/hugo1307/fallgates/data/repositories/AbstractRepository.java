package io.github.hugo1307.fallgates.data.repositories;

import io.github.hugo1307.fallgates.data.HibernateHandler;
import io.github.hugo1307.fallgates.data.models.DataModel;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@Log4j2
public abstract class AbstractRepository<T, C extends DataModel<T, ?>> {

    protected final HibernateHandler dataHandler;

    protected AbstractRepository(HibernateHandler dataHandler) {
        this.dataHandler = dataHandler;
    }

    /**
     * Find a model by its id.
     *
     * @param id          the id of the model
     * @param returnClass the class of the model
     * @return the model found
     */
    public CompletableFuture<Optional<C>> findById(T id, Class<C> returnClass) {
        return CompletableFuture.supplyAsync(() -> {
            AtomicReference<Optional<C>> result = new AtomicReference<>(Optional.empty());
            execute(session -> result.set(Optional.ofNullable(session.find(returnClass, id)))).join();
            return result.get();
        }).handle((result, ex) -> {
            if (ex != null) {
                log.error("Error fetching entity by id.", ex);
                return Optional.empty();
            }
            return result;
        });
    }

    /**
     * Do an HQL query to fetch a single record.
     *
     * @param hqlQuery the String containing the HQL query
     * @return the object obtained
     */
    public CompletableFuture<Optional<C>> fetchSingleQuery(String hqlQuery, List<?> parameters, Class<C> returnClass) {

        return CompletableFuture.supplyAsync(() -> {
            AtomicReference<Optional<C>> result = new AtomicReference<>(Optional.empty());

            execute(session -> {
                Query<C> query = session.createQuery(hqlQuery, returnClass);
                for (int idx = 0; idx < parameters.size(); idx++)
                    query.setParameter(idx + 1, parameters.get(idx));
                result.set(query.uniqueResultOptional());
            }).join();

            return result.get();
        }).handle((result, ex) -> {
            if (ex != null) {
                log.error("Error fetching single entity.", ex);
                return Optional.empty();
            }
            return result;
        });

    }

    /**
     * Do an HQL query to fetch records.
     *
     * @param hqlQuery the String containing the HQL query
     * @return the list of objects obtained
     */
    public CompletableFuture<List<C>> fetchListQuery(String hqlQuery, List<?> parameters, Class<C> returnClass) {

        return CompletableFuture.supplyAsync(() -> {
            List<C> results = new ArrayList<>();

            execute(session -> {
                Query<C> query = session.createQuery(hqlQuery, returnClass);
                for (int idx = 0; idx < parameters.size(); idx++)
                    query.setParameter(idx + 1, parameters.get(idx));
                results.addAll(query.getResultList());
            }).join();

            return results;
        }).handle((results, ex) -> {
            if (ex != null) {
                log.error("Error fetching list of entities.", ex);
                return new ArrayList<>();
            }
            return results;
        });

    }

    /**
     * Save model.
     *
     * <p>
     * Implementation of Hibernate (and JPA) persist method. It is transactional.
     *
     * @param dataModel the model to save
     */
    public CompletableFuture<C> save(C dataModel) {
        return transaction(session -> session.persist(dataModel))
                .thenApply(v -> dataModel);
    }

    /**
     * Delete entity.
     *
     * <p>
     * Implementation of Hibernate (and JPA) remove method. It is transactional.
     *
     * @param dataModel the model to delete
     */
    public CompletableFuture<Void> delete(DataModel<?, ?> dataModel) {
        return transaction(session -> session.remove(dataModel));
    }

    /**
     * Delete entity by id.
     *
     * <p>
     * Implementation of Hibernate (and JPA) remove method. It is transactional.
     *
     * @param id          the id of the model to delete
     * @param returnClass the class of the model
     */
    public CompletableFuture<Void> deleteById(T id, Class<C> returnClass) {
        return transaction(session -> {
            C model = session.find(returnClass, id);
            if (model != null) {
                session.remove(model);
            }
        });
    }

    /**
     * Update entity.
     *
     * <p>
     * Implementation of Hibernate merge method. It is transactional.
     *
     * @param dataModel the model to update
     */
    @SuppressWarnings("deprecation") // We want update, not merge because we want to update the entity in the session
    public CompletableFuture<Void> update(DataModel<?, ?> dataModel) {
        return transaction(session -> session.update(dataModel));
    }

    /**
     * Encapsulate code in transaction
     *
     * @param consumer a consumer with code to be executed within a transaction.
     */
    public CompletableFuture<Void> transaction(Consumer<Session> consumer) {

        return CompletableFuture.runAsync(() -> dataHandler.useSession(session -> {

            Transaction tx = null;

            try {
                tx = session.beginTransaction();
                consumer.accept(session);
                tx.commit();
            } catch (RuntimeException e) {
                if (tx != null) {
                    tx.rollback();
                }
                log.error("Error performing a database transaction.", e);
                throw e;
            }

        }));

    }

    /**
     * Execute code without transactional logic
     *
     * @param consumer a consumer with code to be executed.
     */
    public CompletableFuture<Void> execute(Consumer<Session> consumer) {
        return CompletableFuture.runAsync(() -> dataHandler.useSession(consumer));
    }

}
