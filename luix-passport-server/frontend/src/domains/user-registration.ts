import { z } from "zod"

export const userRegistrationFormSchema = z
  .object({
    email: z.string().email("Please enter a valid email address"),
    username: z.string().min(3, "Username must be at least 3 characters"),
    password: z
      .string()
      .min(5, "Password must be at least 5 characters")
      .max(10, "Password must be at most 10 characters"),
    confirmPassword: z.string(),
  })
  .refine((data) => data.password === data.confirmPassword, {
    message: "Passwords don't match",
    path: ["confirmPassword"],
  });

export type UserRegistrationFormSchema = z.infer<typeof userRegistrationFormSchema>

