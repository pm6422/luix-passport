import { z } from "zod"

export const springSessionSchema = z.object({
  primaryId: z.string().optional(),
  principalName: z.string().optional(),
  lastAccessTime: z.number().optional()
})

export type SpringSession = z.infer<typeof springSessionSchema>