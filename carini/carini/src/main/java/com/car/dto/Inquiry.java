package com.car.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import java.util.Date;

@Data
@NoArgsConstructor
@Entity
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //기본 키가 데이터베이스에 의해 자동으로 생성되도록 지정
    @Column(name = "re_id")
    private Long reId;
    
    @Column(name = "member_id")
    private String memberId;
    
    @Column(name = "re_content")
    private String reContent;
    
    @Column(name = "re_content_rq")
    private String reContentRq;
    
    @Column(name = "re_title")
    private String reTitle;
    
    @Column(name = "re_title_rq")
    private String reTitleRq;
    
    @Column(name = "re_date", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date reDate;
    
    @Column(name = "re_check_rq")
    private boolean reCheckRq;
    
    @Column(name = "re_hero")
    private String reHero;
    
    @Column(name = "re_date_rq", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date reDateRq;
    
    @PreUpdate
    public void preUpdate() {
        this.reDateRq = new Date();
    }


}
