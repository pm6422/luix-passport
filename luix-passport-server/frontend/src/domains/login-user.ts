import { z } from "zod"

export const loginUserSchema = z.object({
  id: z.string().optional(),
  email: z.string().trim().min(1, { message: "Required" }).email("Invalid email format"),
  firstName: z.string().optional(),
  lastName: z.string().optional(),
  lastSignInAt: z.string().optional(),
  profilePic: z.string().optional()
})

export type LoginUser = z.infer<typeof loginUserSchema>