import { z } from "zod"

export const permissionSchema = z.object({
  id: z.string().optional().nullable(),
  description: z.string().optional().nullable(),
  resourceType: z.string().trim().min(1, { message: "Required" }),
  action: z.string().trim().min(1, { message: "Required" }),
  createdAt: z.string().optional().nullable(),
  modifiedAt: z.string().optional().nullable()
})

export type Permission = z.infer<typeof permissionSchema>