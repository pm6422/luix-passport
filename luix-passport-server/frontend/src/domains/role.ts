import { z } from "zod"

export const roleSchema = z.object({
  id: z.string().optional().nullable(),
  description: z.string().optional().nullable(),
  createdAt: z.string().optional().nullable(),
  modifiedAt: z.string().optional().nullable()
})

export type Role = z.infer<typeof roleSchema>