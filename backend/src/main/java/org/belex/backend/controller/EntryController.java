package org.belex.backend.controller;

import jakarta.validation.Valid;
import org.belex.backend.dto.EntryDtos;
import org.belex.backend.service.EntryService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/entries")
public class EntryController {
    private final EntryService entryService;
    public EntryController(EntryService entryService) {
        this.entryService = entryService;
    }

    @GetMapping
    public List<EntryDtos.EntryResponse> list(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam(required = false) LocalDate date){
        return entryService.findForDate(principal.getUsername(), date == null ? LocalDate.now() : date);
    }

    @PostMapping
    public EntryDtos.EntryResponse createEntry(
            @AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody EntryDtos.AddEntryRequest request){
        return entryService.add(principal.getUsername(), request);
    }

    @DeleteMapping("/{id}")
    public void deleteEntry(@AuthenticationPrincipal UserDetails principal, @PathVariable Long id){
        entryService.delete(principal.getUsername(), id);
    }
}
