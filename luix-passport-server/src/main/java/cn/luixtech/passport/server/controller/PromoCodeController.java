package cn.luixtech.passport.server.controller;

import cn.luixtech.passport.server.domain.PromoCode;
import cn.luixtech.passport.server.dto.GeneratePromoCodeDto;
import cn.luixtech.passport.server.dto.RedeemPromoCodeDto;
import cn.luixtech.passport.server.service.PromoCodeService;
import cn.luixtech.passport.server.utils.AuthUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
@Tag(name = "promo-codes", description = "Promo Code Management")
public class PromoCodeController {

    private final PromoCodeService promoCodeService;

    @Operation(summary = "List promo codes")
    @GetMapping("/admin/promo-codes")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<PromoCode>> find(Pageable pageable,
                                                @RequestParam(required = false) String batchId,
                                                @RequestParam(required = false) Boolean enabled) {
        return ResponseEntity.ok(promoCodeService.find(pageable, batchId, enabled));
    }

    @Operation(summary = "Generate promo codes")
    @PostMapping("/admin/promo-codes/generate")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<PromoCode>> generate(@Valid @RequestBody GeneratePromoCodeDto dto) {
        List<PromoCode> codes = promoCodeService.generateCodes(
                dto.getCount(), 
                dto.getType(), 
                dto.getDurationDays(), 
                dto.getExpiry(),
                dto.getBatchId()
        );
        return ResponseEntity.ok(codes);
    }

    @Operation(summary = "Redeem promo code")
    @PostMapping("/promo-codes/redeem")
    public ResponseEntity<Void> redeem(@Valid @RequestBody RedeemPromoCodeDto dto) {
        String username = AuthUtils.getCurrentUsername();
        promoCodeService.redeemCode(dto.getCode(), username);
        return ResponseEntity.ok().build();
    }
}
