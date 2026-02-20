package com.fatec.horario.web.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fatec.horario.domain.service.ShiftScheduleDetailService;
import com.fatec.horario.dto.ShiftScheduleDetail.ShiftScheduleDetailRequest;
import com.fatec.horario.dto.ShiftScheduleDetail.ShiftScheduleDetailResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/shift-schedule-details")
@CrossOrigin
public class ShiftScheduleDetailController {

    @Autowired
    private ShiftScheduleDetailService service;

    @GetMapping
    public ResponseEntity<List<ShiftScheduleDetailResponse>> getAll() {
        List<ShiftScheduleDetailResponse> details = service.getAll();
        return ResponseEntity.ok(details);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShiftScheduleDetailResponse> getById(@PathVariable Long id) {
        ShiftScheduleDetailResponse detail = service.getById(id);
        return ResponseEntity.ok(detail);
    }

    @PostMapping
    public ResponseEntity<ShiftScheduleDetailResponse> create(@Valid @RequestBody ShiftScheduleDetailRequest request) {
        ShiftScheduleDetailResponse detail = service.create(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(detail.id())
                .toUri();
        return ResponseEntity.created(location).body(detail);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShiftScheduleDetailResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ShiftScheduleDetailRequest request) {
        ShiftScheduleDetailResponse detail = service.update(id, request);
        return ResponseEntity.ok(detail);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
