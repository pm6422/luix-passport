import { z } from "zod"

export const userLoginCountSchema = z.object({
  loginCount: z.number(),
  calculatedAt: z.string()
})

export type UserLoginCount = z.infer<typeof userLoginCountSchema>