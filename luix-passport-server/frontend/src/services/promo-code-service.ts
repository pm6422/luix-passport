import http from "@/axios"
import { GeneratePromoCode, PromoCode, RedeemPromoCode } from "@/domains/promo-code"
import { AxiosResponse } from "axios"
import { Page } from "@/domains/page"

export class PromoCodeService {
  
  public static find(reqParams: object): Promise<AxiosResponse<Page<PromoCode>>> {
    return http.get("/api/admin/promo-codes", { params: reqParams })
  }

  public static generate(data: GeneratePromoCode): Promise<AxiosResponse<PromoCode[]>> {
    return http.post("/api/admin/promo-codes/generate", data)
  }

  public static redeem(data: RedeemPromoCode): Promise<void> {
    return http.post("/api/promo-codes/redeem", data)
  }
}
