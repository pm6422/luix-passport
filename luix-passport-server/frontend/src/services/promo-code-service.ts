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

  public static updateEnabled(id: string, enabled: boolean): Promise<void> {
    return http.patch(`/api/admin/promo-codes/${id}/enabled`, null, { params: { enabled } })
  }

  public static sendEmail(id: string, email: string): Promise<void> {
    return http.post(`/api/admin/promo-codes/${id}/send-email`, null, { params: { email } })
  }
}
