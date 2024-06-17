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
import jakarta.validation.constraints.NotEmpty;
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
	@Column(name="member_id")
    private String memberId;

	@Column(name="member_pw")
    private String memberPw;
	
	@Column(name="member_name")
    private String memberName;
	
	@Column(name="member_nickname")
    private String memberNickname;
	
	@Column(name="member_email")
    private String memberEmail;
	
	@Column(name="member_phone_num")
    private String memberPhoneNum;

    @Column(name="member_date", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date memberDate;

    @Column(name="member_social")
    private String memberSocial;
    
    @Column(name="member_role")
    private String memberRole;
    
    @Column(name="member_Social_Nickname")
    private String memberSocialNickname;
    
    

    // Getters and setters
}

