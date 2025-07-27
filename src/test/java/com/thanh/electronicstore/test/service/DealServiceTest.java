package com.thanh.electronicstore.test.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.thanh.electronicstore.dto.DealDTO;
import com.thanh.electronicstore.model.Deal;
import com.thanh.electronicstore.model.DealType;
import com.thanh.electronicstore.repository.DealRepository;
import com.thanh.electronicstore.service.DealService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DealServiceTest {

    @Mock
    private DealRepository dealRepository;
    @InjectMocks
    private DealService dealService;

    @Test
    void getAllDeals_shouldReturnListOfDealDTOs() {
        Deal deal = Deal.builder()
            .id(UUID.randomUUID())
            .type(DealType.PERCENTAGE_DISCOUNT)
            .description("10% off")
            .expiration(LocalDateTime.now().plusDays(5))
            .build();

        when(dealRepository.findAll()).thenReturn(List.of(deal));

        List<DealDTO> deals = dealService.getAllDeals();

        assertThat(deals).hasSize(1);
        assertThat(deals.get(0).getDescription()).isEqualTo("10% off");
    }

    @Test
    void createDeal_shouldSaveNewDeal() {
        DealDTO dto = DealDTO.builder()
            .description("15% off")
            .type(String.valueOf(DealType.PERCENTAGE_DISCOUNT))
            .expiration(LocalDateTime.now().plusDays(3).toString())
            .build();

        dealService.createDeal(dto);

        verify(dealRepository).save(any(Deal.class));
    }

    @Test
    void updateDeal_shouldUpdateExistingDeal() {
        UUID id = UUID.randomUUID();
        Deal existing = Deal.builder()
            .id(id)
            .description("Old desc")
            .expiration(LocalDateTime.now().plusDays(1))
            .build();

        DealDTO updated = DealDTO.builder()
            .description("Updated desc")
            .expiration(LocalDateTime.now().plusDays(5).toString())
            .build();

        when(dealRepository.findById(id)).thenReturn(Optional.of(existing));

        dealService.updateDeal(id.toString(), updated);

        assertThat(existing.getDescription()).isEqualTo("Updated desc");
        verify(dealRepository).save(existing);
    }

    @Test
    void updateDeal_shouldThrowExceptionIfDealNotFound() {
        UUID id = UUID.randomUUID();
        DealDTO updated = DealDTO.builder()
            .description("Updated desc")
            .expiration(LocalDateTime.now().plusDays(5).toString())
            .build();

        when(dealRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> dealService.updateDeal(id.toString(), updated))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Deal is not found");
    }

    @Test
    void updateDeal_shouldThrowExceptionIfInvalidDate() {
        UUID id = UUID.randomUUID();
        Deal existing = Deal.builder()
            .id(id)
            .description("Old desc")
            .expiration(LocalDateTime.now().plusDays(1))
            .build();

        DealDTO updated = DealDTO.builder()
            .description("Updated desc")
            .expiration("invalid-date")
            .build();

        when(dealRepository.findById(id)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> dealService.updateDeal(id.toString(), updated))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid expiration date time!");
    }
}