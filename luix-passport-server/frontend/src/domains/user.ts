import { z } from "zod"
import { isValidPhoneNumber } from "react-phone-number-input"

export const userSchema = z.object({
  id: z.string().optional(),
  username: z.string().trim().min(1, { message: "Required" }),
  email: z.string().trim().min(1, { message: "Required" }).email("Invalid email format"),
  mobileNo: z.string().trim().min(1, { message: "Required" }).refine(isValidPhoneNumber, { message: "Invalid phone number" }),
  firstName: z.string().optional(),
  lastName: z.string().optional(),
  activationCode: z.string().optional(),
  resetAt: z.string().optional(),
  remark: z.string().optional(),
  locale: z.string().trim().min(1, { message: "Required" }),
  timeZone: z.string().trim().min(1, { message: "Required" }),
  dateTimeFormat: z.string().trim().min(1, { message: "Required" }),
  profilePhotoEnabled: z.boolean().optional(),
  activated: z.boolean().optional(),
  enabled: z.boolean().optional(),
  accountExpiresAt: z.string().optional(),
  passwordExpiresAt: z.string().optional(),
  lastSignInAt: z.string().optional(),
  roles: z.array(z.string()).min(1, { message: "Required" }),
  createdAt: z.string().optional(),
  modifiedAt: z.string().optional()
})

export type User = z.infer<typeof userSchema>

export const initialUserState: User = {
  id: "",
  username: "",
  email: "",
  mobileNo: "",
  firstName: "",
  lastName: "",
  activationCode: "",
  resetAt: "",
  remark: "",
  locale: "en_US",
  timeZone: "Asia/Shanghai",
  dateTimeFormat: "1",
  profilePhotoEnabled: false,
  activated: false,
  enabled: true,
  roles: ["ROLE_ANONYMOUS", "ROLE_USER"],
  createdAt: "",
  modifiedAt: ""
}
export const userCriteriaSchema = z.object({
  username: z.string().optional(),
  email: z.string().optional(),
  mobileNo: z.string().optional(),
  enabled: z.string().optional()
})

export type UserCriteriaSchema = z.infer<typeof userCriteriaSchema>

export const initialUserCriteriaState: UserCriteriaSchema = {
  username: "",
  email: "",
  mobileNo: "",
  enabled: ""
}