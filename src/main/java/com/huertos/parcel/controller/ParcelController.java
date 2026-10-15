package com.huertos.parcel.controller;

import com.huertos.parcel.dto.CreateParcelRequest;
import com.huertos.parcel.dto.ParcelDetailResponse;
import com.huertos.parcel.dto.ParcelResponse;
import com.huertos.parcel.service.ParcelService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/parcels")
public class ParcelController {

    private final ParcelService service;

    public ParcelController(ParcelService service) {
        this.service = service;
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<ParcelResponse> create(@Valid @RequestBody CreateParcelRequest request) {
        ParcelResponse created = service.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping(produces = "application/json")
    public List<ParcelResponse> list() {
        return service.findAll();
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public ParcelDetailResponse detail(@PathVariable UUID id) {
        return service.findById(id);
    }
}
