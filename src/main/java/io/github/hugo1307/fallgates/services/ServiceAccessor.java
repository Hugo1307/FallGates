package io.github.hugo1307.fallgates.services;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import io.github.hugo1307.fallgates.FallGates;

@Singleton
public class ServiceAccessor {

    private final Injector injector;

    @Inject
    public ServiceAccessor(FallGates plugin) {
        this.injector = plugin.getGuiceInjector();
    }

    /**
     * Retrieves an instance of a service class from the Guice injector.
     *
     * @param serviceClass the class of the service to retrieve
     * @return an instance of the specified service class
     */
    public <T extends Service> T accessService(Class<T> serviceClass) {
        return injector.getInstance(serviceClass);
    }

}
