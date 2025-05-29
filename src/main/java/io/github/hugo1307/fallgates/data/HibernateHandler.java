package io.github.hugo1307.fallgates.data;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import io.github.hugo1307.fallgates.config.ConfigHandler;
import io.github.hugo1307.fallgates.data.databases.DatabaseConfiguration;
import io.github.hugo1307.fallgates.data.models.DataModel;
import io.github.hugo1307.fallgates.data.models.FallModel;
import io.github.hugo1307.fallgates.data.models.PositionModel;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Logger;

@Singleton
public class HibernateHandler {

    private final Logger pluginLogger;
    private final ConfigHandler configHandler;
    private final DatabaseConfiguration databaseConfiguration;
    private final Set<Class<?>> annotatedClasses;

    private SessionFactory sessionFactory;

    @Inject
    public HibernateHandler(@Named("pluginLogger") Logger pluginLogger, ConfigHandler configHandler, DatabaseConfiguration databaseConfiguration) {
        this.pluginLogger = pluginLogger;
        this.configHandler = configHandler;
        this.databaseConfiguration = databaseConfiguration;
        this.annotatedClasses = Set.of(
                FallModel.class,
                PositionModel.class
        );
    }

    public void useSession(Consumer<Session> consumer) {

        if (sessionFactory == null) {
            sessionFactory = buildSessionFactory();
        }

        try (Session session = sessionFactory.openSession()) {
            consumer.accept(session);
        } catch (HibernateException e) {
            pluginLogger.severe("Error accessing Hibernate session: " + e);
            throw e;
        }

    }

    public void registerEntity(Class<? extends DataModel<?, ?>> entity) {
        boolean entityAdded = annotatedClasses.add(entity);
        if (entityAdded) {
            sessionFactory.close();
            sessionFactory = buildSessionFactory();
        }
    }

    private SessionFactory buildSessionFactory() {

        try {

            Configuration configuration = databaseConfiguration.getConfiguration(configHandler);
            registerModels(configuration);
            StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties()).build();

            SessionFactory sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            sessionFactory.openSession();

            return sessionFactory;

        } catch (HibernateException e) {
            pluginLogger.severe("Error creating Hibernate SessionFactory: " + e);
            throw e;
        }

    }

    private void registerModels(Configuration configuration) {
        annotatedClasses.forEach(configuration::addAnnotatedClass);
    }

}
