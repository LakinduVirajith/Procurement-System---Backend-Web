package com.procurement.system.construction.industry.service;

import com.procurement.system.construction.industry.common.ResponseMessage;
import com.procurement.system.construction.industry.dto.SiteDTO;
import com.procurement.system.construction.industry.dto.UserDTO;
import com.procurement.system.construction.industry.exception.BadRequestException;
import com.procurement.system.construction.industry.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface SiteService {
    ResponseEntity<ResponseMessage> add(SiteDTO siteDTO) throws BadRequestException, NotFoundException;

    Page<SiteDTO> allSiteInfo(Pageable pageable) throws NotFoundException;

    SiteDTO siteInfo(Long siteId) throws NotFoundException;

    ResponseEntity<ResponseMessage> updateSite(SiteDTO siteDTO) throws BadRequestException, NotFoundException;

    ResponseEntity<ResponseMessage> deleteSite(Long siteId, boolean deleteAllOption) throws NotFoundException;

    ResponseEntity<ResponseMessage> assignSite(Long siteId, String userEmail) throws NotFoundException;

    Page<UserDTO> getAllUsers(Long siteId, Pageable pageable) throws NotFoundException;
}
