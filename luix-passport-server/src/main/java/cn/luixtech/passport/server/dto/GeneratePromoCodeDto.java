package cn.luixtech.passport.server.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.time.Instant;

@Data
public class GeneratePromoCodeDto {
    @Min(1)
    private int count;
    
    @NotEmpty
    private String type;
    
    @Min(1)
    private int durationDays;
    
    private Instant expiry;
}
