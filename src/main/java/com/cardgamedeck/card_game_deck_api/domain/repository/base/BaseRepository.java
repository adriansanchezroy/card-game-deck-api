package com.cardgamedeck.card_game_deck_api.domain.repository.base;

import com.cardgamedeck.card_game_deck_api.domain.model.base.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.UUID;

@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity> extends JpaRepository<T, UUID> {
}