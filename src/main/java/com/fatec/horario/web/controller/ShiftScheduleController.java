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

import com.fatec.horario.domain.service.ShiftScheduleService;
import com.fatec.horario.dto.ShiftSchedule.ShiftScheduleRequest;
import com.fatec.horario.dto.ShiftSchedule.ShiftScheduleResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/shift-schedules")
@CrossOrigin
public class ShiftScheduleController {

    @Autowired
    private ShiftScheduleService service;

    @GetMapping
    public ResponseEntity<List<ShiftScheduleResponse>> getAll() {
        List<ShiftScheduleResponse> list = service.getAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShiftScheduleResponse> getById(@PathVariable Long id) {
        ShiftScheduleResponse response = service.getById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ShiftScheduleResponse> create(
            @Valid @RequestBody ShiftScheduleRequest request) {

        ShiftScheduleResponse response = service.create(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShiftScheduleResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ShiftScheduleRequest request) {

        ShiftScheduleResponse response = service.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
