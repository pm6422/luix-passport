import { z } from "zod"

export const PASSWORD_MIN_LENGTH = 5
export const PASSWORD_MAX_LENGTH = 15

export const userRegistrationFormSchema = z
  .object({
    email: z.string().email("Please enter a valid email address"),
    username: z.string().min(3, "Username must be at least 3 characters"),
    password: z
      .string()
      .min(PASSWORD_MIN_LENGTH, "Password must be at least ${PASSWORD_MIN_LENGTH} characters")
      .max(PASSWORD_MAX_LENGTH, "Password must be at most ${PASSWORD_MAX_LENGTH} characters"),
    confirmPassword: z.string(),
  })
  .refine((data) => data.password === data.confirmPassword, {
    message: "Passwords don't match",
    path: ["confirmPassword"],
  });

export type UserRegistrationFormSchema = z.infer<typeof userRegistrationFormSchema>


export const resetPasswordFormSchema = z
  .object({
    newRawPassword: z
      .string()
      .min(PASSWORD_MIN_LENGTH, { message: "Password must be at least ${PASSWORD_MIN_LENGTH} characters" })
      .max(PASSWORD_MAX_LENGTH, { message: "Password must be at most ${PASSWORD_MAX_LENGTH} characters" }),
    confirmPassword: z.string(),
    resetCode: z.string().min(1, { message: "Please enter your reset code" }),
  })
  .refine((data) => data.newRawPassword === data.confirmPassword, {
    message: "Passwords don't match.",
    path: ["confirmPassword"],
  });

export type ResetPasswordFormSchema = z.infer<typeof resetPasswordFormSchema>


export const changePasswordFormSchema = z.object({
  currentPassword: z.string().trim().min(1, { message: "Required" }),
  newPassword: z
    .string()
    .min(PASSWORD_MIN_LENGTH, "New password must be at least ${PASSWORD_MIN_LENGTH} characters")
    .max(PASSWORD_MAX_LENGTH, "New password must be at most ${PASSWORD_MAX_LENGTH} characters"),
  email: z.string().trim(),
  verificationCode: z.string().trim().min(1, { message: "Required" })
})

export type ChangePasswordFormSchema = z.infer<typeof changePasswordFormSchema>




