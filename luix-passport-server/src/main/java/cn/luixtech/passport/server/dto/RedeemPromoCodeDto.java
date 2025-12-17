package cn.luixtech.passport.server.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class RedeemPromoCodeDto {
    @NotEmpty
    private String code;
}
