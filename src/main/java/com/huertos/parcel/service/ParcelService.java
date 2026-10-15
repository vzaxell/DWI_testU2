package com.huertos.parcel.service;

import com.huertos.parcel.dto.CreateParcelRequest;
import com.huertos.parcel.dto.ParcelDetailResponse;
import com.huertos.parcel.dto.ParcelResponse;
import com.huertos.parcel.exception.DuplicateParcelException;
import com.huertos.parcel.exception.InvalidParcelDataException;
import com.huertos.parcel.exception.ParcelNotFoundException;
import com.huertos.parcel.model.GpsLocation;
import com.huertos.parcel.model.Parcel;
import com.huertos.parcel.model.SoilType;
import com.huertos.parcel.repository.ParcelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ParcelService {

    private final ParcelRepository repository;

    public ParcelService(ParcelRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ParcelResponse create(CreateParcelRequest req) {
        double lat = req.location().latitude();
        double lon = req.location().longitude();

        // Regla de negocio: (0,0) suele ser el valor por defecto de un GPS sin señal.
        if (lat == 0.0 && lon == 0.0) {
            throw new InvalidParcelDataException("location",
                    "Las coordenadas (0, 0) no son válidas para una parcela. Verifique la señal del GPS.");
        }
        if (repository.existsByNameIgnoreCase(req.name())) {
            throw new DuplicateParcelException(req.name());
        }

        Parcel parcel = new Parcel(
                req.name(),
                new GpsLocation(lat, lon),
                SoilType.fromText(req.soilType()),
                req.ownerOrLead());
        return ParcelResponse.from(repository.save(parcel));
    }

    @Transactional(readOnly = true)
    public List<ParcelResponse> findAll() {
        return repository.findAll().stream().map(ParcelResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ParcelDetailResponse findById(UUID id) {
        return repository.findById(id)
                .map(ParcelDetailResponse::from)
                .orElseThrow(() -> new ParcelNotFoundException(id));
    }
}
