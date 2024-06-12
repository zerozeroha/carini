package com.car.dto;

import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;

public class BoardListeners {
	@PostLoad
	public void postLoad(Board board) {
		System.out.println("@PostLoad: {}" + board);
	}
	
	@PrePersist
	public void prePersist(Board board) {
		System.out.println("@PrePersist: {}" + board);
	}
	
	@PostPersist
	public void postPersist(Board board) {
		System.out.println("@PostPersist: {}" + board);
	}
	
	@PreUpdate
	public void preUpdate(Board board) {
		System.out.println("@PreUpdate: {}" + board);
	}
	
	@PostUpdate
	public void postUpdate(Board board) {
		System.out.println("@postUpdate: {}" + board);
	}
	
	@PreRemove
	public void preRemove(Board board) {
		System.out.println("@PreRemove: {}" + board);
	}
	
	@PostRemove
	public void postRemove(Board board) {
		System.out.println("@PostRemove: {}" + board);
	}
}
