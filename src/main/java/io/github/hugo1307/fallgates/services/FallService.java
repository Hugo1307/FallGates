package io.github.hugo1307.fallgates.services;

import com.google.inject.Inject;
import io.github.hugo1307.fallgates.data.domain.Fall;
import io.github.hugo1307.fallgates.data.repositories.GateRepository;

public final class FallService implements Service {

    private final GateRepository gateRepository;

    @Inject
    public FallService(GateRepository gateRepository) {
        this.gateRepository = gateRepository;
    }

    /**
     * Save a gate to the database.
     *
     * @param fall the gate to save
     */
    public void saveFall(Fall fall) {
        gateRepository.save(fall.toDataModel());
    }

}
