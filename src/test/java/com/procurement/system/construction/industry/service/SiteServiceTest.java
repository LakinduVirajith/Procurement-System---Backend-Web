package com.procurement.system.construction.industry.service;

import com.procurement.system.construction.industry.common.CommonFunctions;
import com.procurement.system.construction.industry.common.ResponseMessage;
import com.procurement.system.construction.industry.dto.SiteDTO;
import com.procurement.system.construction.industry.dto.UserDTO;
import com.procurement.system.construction.industry.entity.Site;
import com.procurement.system.construction.industry.entity.User;
import com.procurement.system.construction.industry.enums.UserRole;
import com.procurement.system.construction.industry.exception.ConflictException;
import com.procurement.system.construction.industry.exception.NotFoundException;
import com.procurement.system.construction.industry.repository.SiteRepository;
import com.procurement.system.construction.industry.repository.UserRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SiteServiceTest {

    @InjectMocks
    private SiteServiceImpl siteService;

    @Mock
    private SiteRepository siteRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommonFunctions commonFunctions;

    @BeforeEach
    void setUp() {
    }

    @Test
    @SneakyThrows
    public void addSite_shouldReturnSuccessResponse_whenSiteAddedSuccessfully() {
        // Given
        SiteDTO siteDTO = new SiteDTO();
        siteDTO.setSiteManagerId(1L);

        User siteManager = new User();
        siteManager.setUserId(1L);
        siteManager.setRole(UserRole.SITE_MANAGER);

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(siteManager));
        Mockito.when(siteRepository.save(Mockito.any(Site.class))).thenReturn(new Site());

        // When
        ResponseEntity<ResponseMessage> responseEntity = siteService.add(siteDTO);

        // Then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("site data has been added successfully", responseEntity.getBody().getMessage());
    }

    @Test
    public void addSite_shouldThrowConflictException_whenSiteManagerAlreadyAssigned() {
        // Given
        SiteDTO siteDTO = new SiteDTO();
        siteDTO.setSiteManagerId(1L);

        User siteManager = new User();
        siteManager.setUserId(1L);
        siteManager.setRole(UserRole.SITE_MANAGER);

        Site existingSite = new Site();
        existingSite.setSiteManager(siteManager);

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(siteManager));
        Mockito.when(siteRepository.findBySiteManagerUserId(1L)).thenReturn(Optional.of(existingSite));

        // When and Then
        assertThrows(ConflictException.class, () -> siteService.add(siteDTO));
    }

    @Test
    @SneakyThrows
    public void allSiteInfo_shouldReturnListOfSiteDTOs_whenSitesExist() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        List<Site> sites = new ArrayList<>();
        sites.add(new Site());
        Page<Site> sitePage = new PageImpl<>(sites);

        Mockito.when(siteRepository.findAll(pageable)).thenReturn(sitePage);

        // When
        Page<SiteDTO> siteDTOPage = siteService.allSiteInfo(pageable);

        // Then
        assertNotNull(siteDTOPage);
        assertFalse(siteDTOPage.isEmpty());
    }

    @Test
    @SneakyThrows
    public void siteInfo_shouldReturnSiteDTO_whenSiteExists() {
        // Given
        Long siteId = 1L;

        Site site = new Site();
        site.setSiteId(1L);
        site.setSiteManager(new User());
        site.getSiteManager().setUserId(2L);

        Mockito.when(siteRepository.findById(siteId)).thenReturn(Optional.of(site));

        // When
        SiteDTO siteDTO = siteService.siteInfo(siteId);

        // Then
        assertNotNull(siteDTO);
        assertEquals(siteId, siteDTO.getSiteId());
    }

    @Test
    public void siteInfo_shouldThrowNotFoundException_whenSiteDoesNotExist() {
        // Given
        Long siteId = 1L;

        Mockito.when(siteRepository.findById(siteId)).thenReturn(Optional.empty());

        // When and Then
        assertThrows(NotFoundException.class, () -> siteService.siteInfo(siteId));
    }

    @Test
    @SneakyThrows
    public void updateSite_shouldReturnSuccessResponse_whenSiteUpdatedSuccessfully() {
        // Given
        SiteDTO siteDTO = new SiteDTO();
        siteDTO.setSiteId(1L);
        siteDTO.setSiteManagerId(2L);

        User siteManager = new User();
        siteManager.setUserId(2L);
        siteManager.setRole(UserRole.SITE_MANAGER);

        Site existingSite = new Site();
        existingSite.setSiteId(1L);

        Mockito.when(siteRepository.findById(1L)).thenReturn(Optional.of(existingSite));
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(siteManager));
        Mockito.when(siteRepository.save(Mockito.any(Site.class))).thenReturn(existingSite);

        // When
        ResponseEntity<ResponseMessage> responseEntity = siteService.updateSite(siteDTO);

        // Then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("site data has been updated successfully", responseEntity.getBody().getMessage());
    }

    @Test
    public void updateSite_shouldThrowNotFoundException_whenSiteDoesNotExist() {
        // Given
        SiteDTO siteDTO = new SiteDTO();
        siteDTO.setSiteId(1L);
        siteDTO.setSiteManagerId(2L);

        Mockito.when(siteRepository.findById(1L)).thenReturn(Optional.empty());

        // When and Then
        assertThrows(NotFoundException.class, () -> siteService.updateSite(siteDTO));
    }

    @Test
    public void updateSite_shouldThrowConflictException_whenSiteManagerAlreadyAssigned() {
        // Given
        SiteDTO siteDTO = new SiteDTO();
        siteDTO.setSiteId(1L);
        siteDTO.setSiteManagerId(2L);

        User siteManager = new User();
        siteManager.setUserId(2L);
        siteManager.setRole(UserRole.SITE_MANAGER);

        Site existingSite = new Site();
        existingSite.setSiteManager(new User());
        existingSite.getSiteManager().setUserId(3L);

        Mockito.when(siteRepository.findById(1L)).thenReturn(Optional.of(existingSite));
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(siteManager));

        // When and Then
        assertThrows(ConflictException.class, () -> siteService.updateSite(siteDTO));
    }

    @Test
    @SneakyThrows
    public void deleteSite_shouldReturnSuccessResponse_whenSiteDeletedSuccessfully() {
        // Given
        Long siteId = 1L;

        Site existingSite = new Site();
        existingSite.setSiteId(1L);

        Mockito.when(siteRepository.findById(siteId)).thenReturn(Optional.of(existingSite));

        // When
        ResponseEntity<ResponseMessage> responseEntity = siteService.deleteSite(siteId);

        // Then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("site has been deleted successfully", responseEntity.getBody().getMessage());
    }

    @Test
    public void deleteSite_shouldThrowNotFoundException_whenSiteDoesNotExist() {
        // Given
        Long siteId = 1L;

        Mockito.when(siteRepository.findById(siteId)).thenReturn(Optional.empty());

        // When and Then
        assertThrows(NotFoundException.class, () -> siteService.deleteSite(siteId));
    }

    @Test
    @SneakyThrows
    public void allocateSite_shouldReturnSuccessResponse_whenUserAllocatedSuccessfully() {
        // Given
        Long siteId = 1L;
        String userEmail = "user@example.com";

        Site site = new Site();
        site.setSiteId(1L);

        User user = new User();
        user.setUserId(2L);

        Mockito.when(siteRepository.findById(siteId)).thenReturn(Optional.of(site));
        Mockito.when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));

        // When
        ResponseEntity<ResponseMessage> responseEntity = siteService.allocateSite(siteId, userEmail);

        // Then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("user has been allocate successfully", responseEntity.getBody().getMessage());
    }

    @Test
    @SneakyThrows
    public void deAllocateSite_shouldReturnSuccessResponse_whenUserDeallocatedSuccessfully() {
        // Given
        Long siteId = 1L;
        String userEmail = "user@example.com";

        Site site = new Site();
        site.setSiteId(1L);

        User user = new User();
        user.setUserId(2L);
        user.setSite(site);

        Mockito.when(siteRepository.findById(siteId)).thenReturn(Optional.of(site));
        Mockito.when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));

        // When
        ResponseEntity<ResponseMessage> responseEntity = siteService.deAllocateSite(siteId, userEmail);

        // Then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("user has been deallocate successfully", responseEntity.getBody().getMessage());
    }

    @Test
    @SneakyThrows
    public void getAllUsers_shouldReturnListOfUserDTOs_whenUsersExist() {
        // Given
        Long siteId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        Site site = new Site();
        site.setSiteId(1L);

        List<User> users = new ArrayList<>();
        users.add(new User());
        Page<User> userPage = new PageImpl<>(users);

        Mockito.when(siteRepository.findById(siteId)).thenReturn(Optional.of(site));
        Mockito.when(userRepository.findBySiteSiteId(siteId, pageable)).thenReturn((List<User>) userPage);

        // When
        Page<UserDTO> userDTOPage = siteService.getAllUsers(siteId, pageable);

        // Then
        assertNotNull(userDTOPage);
        assertFalse(userDTOPage.isEmpty());
    }
}