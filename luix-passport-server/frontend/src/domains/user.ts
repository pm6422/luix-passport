import { z } from "zod"
import { isValidPhoneNumber } from "react-phone-number-input"

export const userSchema = z.object({
  id: z.string().optional().nullable(),
  username: z.string().trim().min(1, { message: "Required" }),
  email: z.string().trim().min(1, { message: "Required" }).email("Invalid email format"),
  mobileNo: z.string().trim().min(1, { message: "Required" }).refine(isValidPhoneNumber, { message: "Invalid phone number" }),
  firstName: z.string().optional().nullable(),
  lastName: z.string().optional().nullable(),
  activationCode: z.string().optional().nullable(),
  resetAt: z.string().optional(),
  remark: z.string().optional(),
  locale: z.string().trim().min(1, { message: "Required" }),
  timeZoneId: z.string().trim().min(1, { message: "Required" }),
  dateTimeFormatId: z.string().trim().min(1, { message: "Required" }),
  profilePhotoEnabled: z.boolean().optional().nullable(),
  activated: z.boolean().optional().nullable(),
  enabled: z.boolean().optional().nullable(),
  accountExpiresAt: z.string().optional().nullable(),
  passwordExpiresAt: z.string().optional().nullable(),
  lastSignInAt: z.string().optional().nullable(),
  roles: z.array(z.string()).min(1, { message: "Required" }),
  createdAt: z.string().optional().nullable(),
  modifiedAt: z.string().optional().nullable()
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
  timeZoneId: "Asia/Shanghai",
  dateTimeFormatId: "1",
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