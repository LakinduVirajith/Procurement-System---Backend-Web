package com.procurement.system.construction.industry.service;

import com.procurement.system.construction.industry.common.CommonFunctions;
import com.procurement.system.construction.industry.common.ResponseMessage;
import com.procurement.system.construction.industry.dto.SiteDTO;
import com.procurement.system.construction.industry.dto.UserDTO;
import com.procurement.system.construction.industry.entity.Site;
import com.procurement.system.construction.industry.entity.User;
import com.procurement.system.construction.industry.exception.BadRequestException;
import com.procurement.system.construction.industry.exception.InternalServerException;
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
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public ResponseEntity<ResponseMessage> add(SiteDTO siteDTO) throws BadRequestException, NotFoundException, InternalServerException {
        Long siteManagerId = siteDTO.getSiteManagerId();

        // SITE MANAGER EXCEPTIONS HANDLE
        User siteManager = siteManagerExceptions(siteManagerId);

        // SAVE INFO
        siteDTO.setSiteId(null);
        setSiteInfo(siteDTO, siteManager);

        return commonFunctions.successResponse("The site data has been added successfully.");
    }

    private User siteManagerExceptions(Long siteManagerId) throws BadRequestException, NotFoundException {
        if(siteManagerId == null){
            throw new BadRequestException("The site manager information is required to proceed");
        }

        User siteManager = userRepository.findById(siteManagerId)
                .orElseThrow(() -> new NotFoundException("Sorry, couldn't find the site manager's user data"));

        if(!siteManager.getRole().name().equals("SITE_MANAGER")){
            throw new BadRequestException("Not a valid site manager role");
        }

        return siteManager;
    }

    private User procurementManagerExceptions(Long procurementManagerId) throws BadRequestException, NotFoundException {
        User procurementManager = userRepository.findById(procurementManagerId)
                .orElseThrow(() -> new NotFoundException("Sorry, couldn't find the procurement manager's user data"));

        if(!procurementManager.getRole().name().equals("PROCUREMENT_MANAGER")){
            throw new BadRequestException("Not a valid procurement manager role");
        }

        return procurementManager;
    }

    private void setSiteInfo(SiteDTO siteDTO, User siteManager) throws InternalServerException, BadRequestException, NotFoundException {
        Site site = modelMapper.map(siteDTO, Site.class);
        site.setSiteManager(siteManager);

        Long procurementManagerId = siteDTO.getProcurementManagerId();
        if(procurementManagerId != null) {
            User procurementManager = procurementManagerExceptions(procurementManagerId);
            site.setProcurementManager(procurementManager);
        }

        updateUserRef(site, siteManager);
    }

    private void updateUserRef(Site site, User siteManager) throws InternalServerException {
        Site executedSite = siteRepository.save(site);

        siteManager.setSite(executedSite);
        userRepository.save(siteManager);

        if(executedSite.getProcurementManager() != null) {
            User procurementManager = userRepository.findById(executedSite.getProcurementManager().getUserId())
                    .orElseThrow(() -> new InternalServerException("Internal server error occurred"));

            procurementManager.setSite(executedSite);
            userRepository.save(procurementManager);
        }
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
                    if(site.getProcurementManager() != null){
                        siteDTO.setProcurementManagerId(site.getProcurementManager().getUserId());
                    }

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
        if(site.getProcurementManager() != null){
            siteDTO.setProcurementManagerId(site.getProcurementManager().getUserId());
        }

        return siteDTO;
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseMessage> updateSite(SiteDTO siteDTO) throws BadRequestException, NotFoundException, InternalServerException {
        Site site = siteRepository.findById(siteDTO.getSiteId())
                .orElseThrow(() -> new NotFoundException("Couldn't find any site with the provided ID"));

        // IF SITE MANAGER EXIST
        if(site.getSiteManager() != null) {
            User user = userRepository.findById(site.getSiteManager().getUserId())
                    .orElseThrow(() -> new InternalServerException("Internal server error occurred"));
            user.setSite(null);
            userRepository.save(user);
        }

        // IF PROCUREMENT MANAGER EXIST
        if(site.getProcurementManager() != null){
            User user = userRepository.findById(site.getProcurementManager().getUserId())
                    .orElseThrow(() -> new InternalServerException("Internal server error occurred"));
            user.setSite(null);
            userRepository.save(user);
        }

        // SITE MANAGER EXCEPTIONS HANDLE
        Long siteManagerId = siteDTO.getSiteManagerId();
        User siteManager = siteManagerExceptions(siteManagerId);

        // SET SITE DATA
        if(siteDTO.getSiteName() != null){
            site.setSiteName(siteDTO.getSiteName());
        }
        if(siteDTO.getLocation() != null){
            site.setLocation(siteDTO.getLocation());
        }
        if(siteDTO.getStartDate() != null){
            site.setStartDate(siteDTO.getStartDate());
        }
        if(siteDTO.getContactNumber() != null){
            site.setContactNumber(siteDTO.getContactNumber());
        }
        if(siteDTO.getAllocatedBudget() != 0){
            site.setAllocatedBudget(siteDTO.getAllocatedBudget());
        }
        if(siteDTO.getSiteManagerId() != null){
            site.setSiteManager(siteManager);
        }
        if(siteDTO.getProcurementManagerId() != null){
            User procurementManager = procurementManagerExceptions(siteDTO.getProcurementManagerId());
            site.setProcurementManager(procurementManager);
        }

        updateUserRef(site, siteManager);
        return commonFunctions.successResponse("The site data has been updated successfully.");
    }

    @Override
    public ResponseEntity<ResponseMessage> deleteSite(Long siteId) throws NotFoundException {
        Optional<Site> siteOptional = siteRepository.findById(siteId);
        if(siteOptional.isEmpty()){
            throw new NotFoundException("Couldn't find any site with the provided ID");
        }

        Site site = siteOptional.get();
        site.setSiteManager(null);
        site.setProcurementManager(null);
        siteRepository.save(site);
        siteRepository.deleteById(siteId);

        return commonFunctions.successResponse("The site has been deleted successfully");
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseMessage> allocateSite(Long siteId, String userEmail) throws NotFoundException, BadRequestException {
        Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new NotFoundException("Couldn't find any site with the provided ID"));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("Couldn't find any user with the provided email address"));

        if(user.getSite() != null){
            throw new BadRequestException("The user has been already allocated into site");
        }

        user.setSite(site);
        userRepository.save(user);

        List<User> users = site.getUsers();
        users.add(user);
        site.setUsers(users);
        siteRepository.save(site);

        return commonFunctions.successResponse("The user has been allocate successfully");
    }

    @Override
    public ResponseEntity<ResponseMessage> deAllocateSite(Long siteId, String userEmail) throws NotFoundException, BadRequestException {
        Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new NotFoundException("Couldn't find any site with the provided ID"));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("Couldn't find any user with the provided email address"));

        if(user.getSite() == null){
            throw new BadRequestException("the user has already been deallocated");
        }

        if(site.getSiteManager().getUserId().equals(user.getUserId())){
            throw new BadRequestException("Site manager can't be deallocated");
        }

        user.setSite(null);
        userRepository.save(user);

        return commonFunctions.successResponse("The user has been deallocate successfully");
    }

    @Override
    public Page<UserDTO> getAllUsers(Long siteId, Pageable pageable) throws NotFoundException {
        Optional<Site> siteOptional = siteRepository.findById(siteId);
        if(siteOptional.isEmpty()){
            throw new NotFoundException("Couldn't find any site with the provided ID");
        }

        List<User> users = userRepository.findBySiteSiteId(siteId, pageable);
        if(users.isEmpty()){
            throw new NotFoundException("Couldn't find any users in this site");
        }

        List<UserDTO> userDTOs = users.stream()
                .map(user -> {
                    UserDTO userDTO = modelMapper.map(user, UserDTO.class);
                    userDTO.setPassword(null);
                    return userDTO;
                })
                .collect(Collectors.toList());

        return new PageImpl<>(userDTOs, pageable, userDTOs.size());
    }
}
