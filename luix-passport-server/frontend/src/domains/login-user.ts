import { z } from "zod"

export const loginUserSchema = z.object({
  id: z.string(),
  email: z.string(),
  firstName: z.string().optional(),
  lastName: z.string().optional(),
  signInAt: z.string()
})

export type LoginUser = z.infer<typeof loginUserSchema>