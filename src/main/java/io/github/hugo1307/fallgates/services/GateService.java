package io.github.hugo1307.fallgates.services;

import com.google.inject.Inject;
import io.github.hugo1307.fallgates.data.domain.Gate;
import io.github.hugo1307.fallgates.repositories.GateRepository;

public final class GateService implements DataService {

    private final GateRepository gateRepository;

    @Inject
    public GateService(GateRepository gateRepository) {
        this.gateRepository = gateRepository;
    }

    /**
     * Save a gate to the database.
     *
     * @param gate the gate to save
     */
    public void saveGate(Gate gate) {
        gateRepository.save(gate.toDataModel());
    }

}
