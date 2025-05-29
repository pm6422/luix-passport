import { z } from "zod"

export const loginUserSchema = z.object({
  username: z.string(),
  email: z.string(),
  firstName: z.string().optional(),
  lastName: z.string().optional(),
  signInAt: z.string()
})

export type LoginUser = z.infer<typeof loginUserSchema>