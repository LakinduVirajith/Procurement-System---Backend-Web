package com.procurement.system.construction.industry.controller;

import com.procurement.system.construction.industry.common.ResponseMessage;
import com.procurement.system.construction.industry.dto.SiteDTO;
import com.procurement.system.construction.industry.dto.UserDTO;
import com.procurement.system.construction.industry.exception.BadRequestException;
import com.procurement.system.construction.industry.exception.NotFoundException;
import com.procurement.system.construction.industry.service.SiteService;
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

    @PostMapping("site/add")
    public ResponseEntity<ResponseMessage> add(@Valid @RequestBody SiteDTO siteDTO) throws BadRequestException, NotFoundException {
        return siteService.add(siteDTO);
    }

    @GetMapping("site/all/info")
    public Page<SiteDTO> allSiteInfo(@PageableDefault Pageable pageable) throws NotFoundException {
        return siteService.allSiteInfo(pageable);
    }

    @GetMapping("site/info/{id}")
    public SiteDTO siteInfo(@PathVariable("id") Long siteId) throws NotFoundException {
        return siteService.siteInfo(siteId);
    }

    @PutMapping("site/update")
    public ResponseEntity<ResponseMessage> updateSite(@Valid @RequestBody SiteDTO siteDTO) throws BadRequestException, NotFoundException {
        return siteService.updateSite(siteDTO);
    }

    @DeleteMapping("site/delete/{id}")
    public ResponseEntity<ResponseMessage> deleteSite(@PathVariable("id") Long siteId, @RequestBody boolean deleteAllOption) throws NotFoundException {
        return siteService.deleteSite(siteId, deleteAllOption);
    }

    @PutMapping("site/assign")
    public ResponseEntity<ResponseMessage> assignSite(@RequestBody Long siteId, String userEmail) throws NotFoundException {
        return siteService.assignSite(siteId, userEmail);
    }

    @GetMapping("site/all/users/{id}")
    public Page<UserDTO> getAllUsers(@PathVariable("id") Long siteId, Pageable pageable) throws NotFoundException {
        return siteService.getAllUsers(siteId, pageable);
    }
}
