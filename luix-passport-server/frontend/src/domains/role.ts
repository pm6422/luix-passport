import { z } from "zod"

export const roleSchema = z.object({
  id: z.string().trim().min(1, { message: "Required" }),
  remark: z.string().optional().nullable(),
  createdAt: z.string().optional().nullable(),
  modifiedAt: z.string().optional().nullable()
})

export type Role = z.infer<typeof roleSchema>