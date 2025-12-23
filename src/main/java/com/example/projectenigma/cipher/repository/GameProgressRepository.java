package com.example.projectenigma.cipher.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.projectenigma.cipher.entity.GameProgress;

/**
 * ゲームの進行状況を操作するためのリポジトリインターフェース。
 * JpaRepositoryを継承しており、基本的なCRUD操作が利用可能。
 * @param <GameProgress> エンティティの型
 * @param <String> エンティティのIDの型
 * @author R.Morioka
 * @version 1.0
 * @since 1.0
 */
@Repository
public interface GameProgressRepository extends JpaRepository<GameProgress, String> {
}