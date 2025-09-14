package com.chat.sr.dto;

import lombok.Data;

@Data
public class VetResponseDTO {
    private Long id;
    private String dvmRegId;
    private String university;
    private float result;
    private int passingYear;
    private String specialization;

    private Long userId;
    private String userName;
}
