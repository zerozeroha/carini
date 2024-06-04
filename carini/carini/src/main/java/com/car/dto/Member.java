package com.car.dto;

import java.util.Date;

import org.springframework.web.multipart.MultipartFile;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Member {
	
	@Id
	@Column(name="memer_id")
    private String memberId;
	
	@Column(name="memer_pw")
    private String memberPw;
	
	@Column(name="memer_name")
    private String memberName;
	
	@Column(name="memer_nickname")
    private String memberNickname;
	
	@Column(name="memer_email")
    private String memberEmail;
	
	@Column(name="memer_phone_num")
    private String memberPhoneNum;

    @Column(name="member_date", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date memberDate;

    @Column(name="memer_social")
    private String memberSocial;
    
    @Column(name="memer_role")
    private String memberRole;

    // Getters and setters
}
