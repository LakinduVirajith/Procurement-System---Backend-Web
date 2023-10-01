package com.procurement.system.construction.industry.controller;

import com.procurement.system.construction.industry.common.ResponseMessage;
import com.procurement.system.construction.industry.dto.SiteDTO;
import com.procurement.system.construction.industry.dto.UserDTO;
import com.procurement.system.construction.industry.exception.BadRequestException;
import com.procurement.system.construction.industry.exception.InternalServerException;
import com.procurement.system.construction.industry.exception.NotFoundException;
import com.procurement.system.construction.industry.service.SiteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/")
@RequiredArgsConstructor
@Tag(name = "Site Controllers")
public class SiteController {

    private final SiteService siteService;

    @Operation(summary = "Create Site", description = "Create site using necessary site information.")
    @PostMapping("site/add")
    public ResponseEntity<ResponseMessage> add(@Valid @RequestBody SiteDTO siteDTO) throws BadRequestException, NotFoundException, InternalServerException {
        return siteService.add(siteDTO);
    }

    @Operation(summary = "Get All Site Info", description = "Get all site information's.")
    @GetMapping("site/all/info")
    public Page<SiteDTO> allSiteInfo(@PageableDefault Pageable pageable) throws NotFoundException {
        return siteService.allSiteInfo(pageable);
    }

    @Operation(summary = "Get Site Info", description = "Get site info using site ID.")
    @GetMapping("site/info/{id}")
    public SiteDTO siteInfo(@PathVariable("id") Long siteId) throws NotFoundException {
        return siteService.siteInfo(siteId);
    }

    @Operation(summary = "Update Site Info", description = "Update site info using necessary site information.")
    @PutMapping("site/update")
    public ResponseEntity<ResponseMessage> updateSite(@Valid @RequestBody SiteDTO siteDTO) throws BadRequestException, NotFoundException, InternalServerException {
        return siteService.updateSite(siteDTO);
    }

    @Operation(summary = "Delete Site Info", description = "Delete site info using siteId.")
    @DeleteMapping("site/delete/{id}")
    public ResponseEntity<ResponseMessage> deleteSite(@PathVariable("id") Long siteId) throws NotFoundException {
        return siteService.deleteSite(siteId);
    }

    @Operation(summary = "Allocate User To Site", description = "Allocate a user to a site using their email and site id.")
    @PutMapping("site/allocate")
    public ResponseEntity<ResponseMessage> allocateSite(@RequestBody Long siteId, String userEmail) throws NotFoundException, BadRequestException {
        return siteService.allocateSite(siteId, userEmail);
    }

    @Operation(summary = "Deallocate User To Site", description = "Deallocate a user to a site using their email and site id.")
    @PutMapping("site/deallocate")
    public ResponseEntity<ResponseMessage> deAllocateSite(@RequestBody Long siteId, String userEmail) throws NotFoundException, BadRequestException {
        return siteService.deAllocateSite(siteId, userEmail);
    }

    @Operation(summary = "Get All Site Users", description = "Retrieve all site users using site ID with pagination.")
    @GetMapping("site/all/users/{id}")
    public Page<UserDTO> getAllUsers(@PathVariable("id") Long siteId, Pageable pageable) throws NotFoundException {
        return siteService.getAllUsers(siteId, pageable);
    }
}
