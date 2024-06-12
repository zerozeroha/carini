package com.car.persistence;

import java.util.List;
import java.util.Optional;

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
}
