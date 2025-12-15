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

