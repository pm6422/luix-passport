import axios, { AxiosResponse } from "axios"
import http from "@/axios"
import type { LoginUser } from "@/stores/login-user-store"
import { type SupportedTimezone } from "@/domains/supported-timezone"
import { type SupportedDateTimeFormat } from "@/domains/supported-date-time-format"
import { AccountFormSchema } from "@/views/account-settings/account/account-form"
import { ChangePasswordFormSchema } from "@/views/account-settings/change-password/change-password-form"
import { ResetPasswordFormSchema } from "@/views/bak/auth/reset-password"

export class AccountService {
  constructor() {
  }

  public static async getCurrentAccount(): Promise<LoginUser> {
    try {
      const res = await axios.get<LoginUser>("/open-api/accounts/user")
      return res.data
    } catch (error) {
      console.error(error)
      return {} as LoginUser
    }
  }

  public static update(model: AccountFormSchema): Promise<void> {
    return http.put("/api/accounts/user", model)
  }

  public static sendEmailChangeVerificationCode(email: string): Promise<void> {
    return http.post("/api/accounts/request-email-change-verification-code?email=" + email)
  }

  public static sendPasswordChangeVerificationCode(): Promise<void> {
    return http.post("/api/accounts/request-password-change-verification-code")
  }

  public static updatePassword(model: ChangePasswordFormSchema): Promise<void> {
    return http.put("/api/accounts/password", model)
  }

  public static changeEmail(verificationCode: string): Promise<void> {
    return http.post("/api/accounts/change-email?verificationCode=" + verificationCode)
  }

  public static uploadProfilePicture(formData: FormData): Promise<void> {
    return http.post("/api/accounts/profile-pic/upload", formData)
  }

  public static activate(code: string): Promise<void> {
    return http.get("/open-api/accounts/activate/" + code)
  }

  public static requestPasswordRecovery(email: string): Promise<void> {
    return http.post("/open-api/accounts/request-password-recovery?email=" + email)
  }

  public static completePasswordRecovery(model: ResetPasswordFormSchema): Promise<void> {
    return http.post("/open-api/accounts/complete-password-recovery", model)
  }

  public static async signOut(): Promise<void> {
    await http.post("/api/accounts/sign-out")
    window.location.reload()
  }

  public static findSupportedTimezones(): Promise<AxiosResponse<Array<SupportedTimezone>>> {
    return http.get("/api/accounts/all-supported-time-zones")
  }

  public static findSupportedDateTimeFormats(): Promise<AxiosResponse<Array<SupportedDateTimeFormat>>> {
    return http.get("/api/accounts/all-supported-date-time-formats")
  }
}