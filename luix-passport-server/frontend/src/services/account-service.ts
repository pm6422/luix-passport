import axios, { AxiosResponse } from 'axios'
import http from "@/axios"
import type { AuthUser } from "@/stores/auth-user-store"
import { type SupportedTimezone } from "@/domains/supported-timezone"
import { type SupportedDateTimeFormat } from "@/domains/supported-date-time-format"

export class AccountService {
  constructor() {
  }

  public static async getCurrentAccount(): Promise<AuthUser> {
    try {
      const res = await axios.get<AuthUser>("open-api/accounts/user")
      return res.data
    } catch (error) {
      console.error(error)
      return {} as AuthUser
    }
  }

  public static update(model: any): Promise<void> {
    return http.put("api/accounts/user", model)
  }

  public static sendEmailChangeVerificationCode(email: string): Promise<void> {
    return http.post("api/accounts/request-email-change-verification-code?email=" + email)
  }

  public static sendPasswordChangeVerificationCode(): Promise<void> {
    return http.post("api/accounts/request-password-change-verification-code")
  }

  public static updatePassword(model: any): Promise<void> {
    return http.put("api/accounts/password", model)
  }

  public static changeEmail(verificationCode: string): Promise<void> {
    return http.post("api/accounts/change-email?verificationCode=" + verificationCode)
  }

  public static uploadProfilePicture(formData: FormData): Promise<void> {
    return http.post("api/accounts/profile-pic/upload", formData)
  }

  public static activate(code: string): Promise<void> {
    return http.get("open-api/accounts/activate/" + code)
  }

  public static requestPasswordRecovery(email: string): Promise<void> {
    return http.post("open-api/accounts/request-password-recovery?email=" + email)
  }

  public static completePasswordRecovery(model: any): Promise<void> {
    return http.post("open-api/accounts/complete-password-recovery", model)
  }

  public static async signOut(): Promise<void> {
    await http.get("api/sse/disconnect")
    await http.post("api/accounts/sign-out")
    window.location.reload()
  }

  public static findSupportedTimezones(): Promise<AxiosResponse<Array<SupportedTimezone>>> {
    return http.get("api/accounts/all-supported-time-zones")
  }

  public static findSupportedDateTimeFormats(): Promise<AxiosResponse<Array<SupportedDateTimeFormat>>> {
    return http.get("api/accounts/all-supported-date-time-formats")
  }
}