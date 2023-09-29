package com.procurement.system.construction.industry.service;

import com.procurement.system.construction.industry.common.CommonFunctions;
import com.procurement.system.construction.industry.common.ResponseMessage;
import com.procurement.system.construction.industry.dto.SiteDTO;
import com.procurement.system.construction.industry.dto.UserDTO;
import com.procurement.system.construction.industry.entity.Site;
import com.procurement.system.construction.industry.entity.User;
import com.procurement.system.construction.industry.exception.BadRequestException;
import com.procurement.system.construction.industry.exception.NotFoundException;
import com.procurement.system.construction.industry.repository.SiteRepository;
import com.procurement.system.construction.industry.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SiteServiceImpl implements SiteService{

    private final SiteRepository siteRepository;
    private final UserRepository userRepository;
    private final CommonFunctions commonFunctions;
    private final ModelMapper modelMapper;

    @Override
    public ResponseEntity<ResponseMessage> add(SiteDTO siteDTO) throws BadRequestException, NotFoundException {
        Long siteManagerId = siteDTO.getSiteManagerId();

        // SITE MANAGER EXCEPTIONS HANDLE
        User siteManager = siteManagerExceptions(siteManagerId);

        // SAVE INFO
        siteDTO.setSiteId(null);
        Site site = new Site();
        setSiteInfo(siteDTO, siteManager, site);

        return commonFunctions.successResponse("The site data has been added successfully.");
    }

    private User siteManagerExceptions(Long siteManagerId) throws BadRequestException, NotFoundException {
        if(siteManagerId == null){
            throw new BadRequestException("The site manager information is required to proceed");
        }

        Optional<User> optionalSiteManager = userRepository.findById(siteManagerId);
        if(optionalSiteManager.isEmpty()){
            throw new NotFoundException("Sorry, couldn't find the site manager's user data");
        }

        return optionalSiteManager.get();
    }

    private void setSiteInfo(SiteDTO siteDTO, User siteManager, Site site) {
        modelMapper.map(siteDTO, site);
        site.setSiteManager(siteManager);

        Long procurementManagerId = siteDTO.getProcurementManagerId();
        Optional<User> procurementManager = userRepository.findById(procurementManagerId);
        procurementManager.ifPresent(site::setProcurementManager);

        siteRepository.save(site);
    }

    @Override
    public Page<SiteDTO> allSiteInfo(Pageable pageable) throws NotFoundException {
        Page<Site> sitePage = siteRepository.findAll(pageable);
        if (sitePage.isEmpty()) {
            throw new NotFoundException("Currently, there are no sites found in our system.");
        }

        // SET SITE DETAILS
        List<SiteDTO> siteDTOS = sitePage.getContent()
                .stream()
                .map(site -> {
                    SiteDTO siteDTO = modelMapper.map(site, SiteDTO.class);
                    siteDTO.setSiteManagerId(site.getSiteManager().getUserId());
                    siteDTO.setProcurementManagerId(site.getProcurementManager().getUserId());

                    return siteDTO;
                })
                .collect(Collectors.toList());

        return new PageImpl<>(siteDTOS, pageable, sitePage.getTotalElements());
    }

    @Override
    public SiteDTO siteInfo(Long siteId) throws NotFoundException {
        Optional<Site> siteOptional = siteRepository.findById(siteId);
        if(siteOptional.isEmpty()){
            throw new NotFoundException("Couldn't find any site with the provided ID");
        }

        Site site = siteOptional.get();
        SiteDTO siteDTO = modelMapper.map(site, SiteDTO.class);
        siteDTO.setSiteManagerId(site.getSiteManager().getUserId());
        siteDTO.setProcurementManagerId(site.getProcurementManager().getUserId());

        return siteDTO;
    }

    @Override
    public ResponseEntity<ResponseMessage> updateSite(SiteDTO siteDTO) throws BadRequestException, NotFoundException {
        Long siteManagerId = siteDTO.getSiteManagerId();

        // SITE MANAGER EXCEPTIONS HANDLE
        User siteManager = siteManagerExceptions(siteManagerId);

        Optional<Site> siteOptional = siteRepository.findById(siteDTO.getSiteId());
        if(siteOptional.isEmpty()){
            throw new NotFoundException("Couldn't find any site with the provided ID");
        }

        Site site = siteOptional.get();
        setSiteInfo(siteDTO, siteManager, site);

        return commonFunctions.successResponse("The site data has been updated successfully.");
    }

    @Override
    public ResponseEntity<ResponseMessage> deleteSite(Long siteId, Boolean deleteAllOption) throws NotFoundException {
        Optional<Site> siteOptional = siteRepository.findById(siteId);
        if(siteOptional.isEmpty()){
            throw new NotFoundException("Couldn't find any site with the provided ID");
        }

        Site site = siteOptional.get();
        site.setSiteManager(null);
        site.setProcurementManager(null);
        if(deleteAllOption){
            siteRepository.save(site);
            siteRepository.deleteById(siteId);
        }else{
            site.setUsers(null);
            siteRepository.save(site);
        }

        return commonFunctions.successResponse("The site has been deleted successfully");
    }

    @Override
    public ResponseEntity<ResponseMessage> assignSite(Long siteId, String userEmail) throws NotFoundException {
        Optional<Site> siteOptional = siteRepository.findById(siteId);
        if(siteOptional.isEmpty()){
            throw new NotFoundException("Couldn't find any site with the provided ID");
        }

        Optional<User> optionalUser = userRepository.findByEmail(userEmail);
        if(optionalUser.isEmpty()){
            throw new NotFoundException("Couldn't find any user with the provided email address");
        }

        Site site = siteOptional.get();
        List<User> users = site.getUsers();
        users.add(optionalUser.get());
        site.setUsers(users);

        siteRepository.save(site);

        return commonFunctions.successResponse("The user has been assigned successfully");
    }

    @Override
    public Page<UserDTO> getAllUsers(Long siteId, Pageable pageable) throws NotFoundException {
        Optional<Site> siteOptional = siteRepository.findById(siteId);
        if(siteOptional.isEmpty()){
            throw new NotFoundException("Couldn't find any site with the provided ID");
        }

        Site site = siteOptional.get();
        List<User> users = site.getUsers();
        if(users.isEmpty()){
            throw new NotFoundException("Couldn't find any users in this site");
        }

        // PAGINATE THE USER LIST BASED ON THE PROVIDED PAGEABLE
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;

        List<UserDTO> userDTOs = users.stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());

        List<UserDTO> pageUserDTOs;

        if (startItem < userDTOs.size()) {
            int toIndex = Math.min(startItem + pageSize, userDTOs.size());
            pageUserDTOs = userDTOs.subList(startItem, toIndex);
        } else {
            pageUserDTOs = Collections.emptyList();
        }

        return new PageImpl<>(pageUserDTOs, pageable, userDTOs.size());
    }
}
