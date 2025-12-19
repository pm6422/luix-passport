import { z } from "zod"

export const loginLogSchema = z.object({
  id: z.string(),
  username: z.string(),
  ip: z.string(),
  location: z.string(),
  device: z.string(),
  browser: z.string().optional().nullable(),
  os: z.string().optional().nullable(),
  status: z.enum(['success', 'failed']),
  loginAt: z.string()
})

export type LoginLog = z.infer<typeof loginLogSchema>
