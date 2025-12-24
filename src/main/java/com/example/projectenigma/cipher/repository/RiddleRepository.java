package com.example.projectenigma.cipher.repository;

import com.example.projectenigma.cipher.entity.Riddle;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Riddle EntityへのDBアクセスを行うリポジトリインターフェース。
 * Spring Data JPAにより、基本的なCRUD操作（findById, saveAll等）が自動提供される。
 *
 * @author R.Morioka
 * @version 1.0
 * @since 1.0
 */
public interface RiddleRepository extends JpaRepository<Riddle, Integer> {
    // stageIdで検索するメソッドがあれば便利
    // (findByIdがあるから実はなくてもいけるけど、分かりやすさのために)
}