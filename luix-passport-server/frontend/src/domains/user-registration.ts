import { z } from "zod"

export const PASSWORD_MIN_LENGTH = 5

export const userRegistrationFormSchema = z
  .object({
    email: z.string().email("Please enter a valid email address"),
    username: z.string().min(3, "Username must be at least 3 characters"),
    password: z
      .string()
      .min(PASSWORD_MIN_LENGTH, "Password must be at least 5 characters"),
    confirmPassword: z.string(),
  })
  .refine((data) => data.password === data.confirmPassword, {
    message: "Passwords don't match",
    path: ["confirmPassword"],
  });

export type UserRegistrationFormSchema = z.infer<typeof userRegistrationFormSchema>

