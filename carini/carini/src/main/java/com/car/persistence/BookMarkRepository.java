package com.car.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.car.dto.Bookmark;
import com.car.dto.Member;

@Repository
public interface BookMarkRepository extends JpaRepository<Bookmark, Integer>{
	
	List<Bookmark> findBookmarkByMemberId(String memberId);
	
	@Modifying
	@Query("DELETE FROM Bookmark b WHERE b.carId = :carId AND b.memberId = :memberId")
	void deleteByBookmarkIdAndMemberId(@Param("carId") int carId, @Param("memberId") String memberId);

	boolean existsByMemberIdAndCarId(String memberId, int carId);
	
	@Query("SELECT b FROM Bookmark b WHERE b.memberId = :memberId")
	List<Bookmark> findAllByMemberId(@Param("memberId") String memberId);
	
	@Query("SELECT COUNT(*) FROM Bookmark b WHERE b.memberId = :memberId")
	int getBookmarkCount(@Param("memberId") String memberId);
	
	long countByCarId(int carId);
	
	@Query("SELECT b.carId FROM Bookmark b GROUP BY b.carId ORDER BY COUNT(b.carId) DESC")
	List<Integer> findTop10CarIdsWithCount();

}
