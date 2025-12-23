import axios, { AxiosResponse } from "axios"
import http from "@/axios"
import type { LoginUser } from "@/stores/login-user-store"
import { type DataDict } from "@/domains/data-dict"
import { type DateTimeFormat } from "@/domains/date-time-format"
import { SettingsFormSchema } from "@/views/account/settings/settings-form"
import { type ChangePasswordFormSchema } from "@/domains/user-registration"
import { type ResetPasswordFormSchema } from "@/domains/user-registration"
import { type UserRegistrationFormSchema } from "@/domains/user-registration"
import { SignInFormSchema } from "@/views/account/sign-in/sign-in-form"

export class AccountService {
  constructor() {
  }

  public static async getCurrentUser(): Promise<LoginUser> {
    try {
      const res = await axios.get<LoginUser>("/open-api/accounts/user")
      return (res.data ?? ({} as LoginUser)) as LoginUser
    } catch (_error) {
      return {} as LoginUser
    }
  }

  public static async signIn(model: SignInFormSchema): Promise<LoginUser> {
    const res = await http.post<LoginUser>("/open-api/accounts/sign-in", model)
    return res.data
  }


  public static async getAvatar(): Promise<AxiosResponse<Blob>> {
    return axios.get("/api/accounts/avatar", {
      responseType: "blob",
      withCredentials: true,
      timeout: 5000
    })
  }

  public static async signOut(): Promise<void> {
    http.post("/api/accounts/sign-out").then(() => {
      // Redirect to sign-in page instead of reloading
      window.location.href = "/sign-in"
    })
  }

  public static register(model: UserRegistrationFormSchema): Promise<void> {
    return http.post("/open-api/accounts/register", model)
  }

  public static update(model: SettingsFormSchema): Promise<void> {
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

  public static uploadAvatar(formData: FormData): Promise<void> {
    return http.post("/api/accounts/avatar", formData)
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

  public static findSupportedTimezones(): Promise<AxiosResponse<Array<DataDict>>> {
    return http.get("/api/accounts/all-supported-time-zones")
  }

  public static findDateTimeFormats(): Promise<AxiosResponse<Array<DateTimeFormat>>> {
    return http.get("/api/accounts/date-time-formats")
  }
}
