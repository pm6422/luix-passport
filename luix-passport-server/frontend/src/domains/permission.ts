import { z } from "zod"

export const permissionSchema = z.object({
  id: z.string().trim().min(1, { message: "Required" }),
  description: z.string().optional().nullable(),
  resourceType: z.string().trim().min(1, { message: "Required" }),
  action: z.string().trim().min(1, { message: "Required" }),
  createdAt: z.string().optional().nullable(),
  modifiedAt: z.string().optional().nullable()
})

export type Permission = z.infer<typeof permissionSchema>

export const initialPermissionState: Permission = {
  id: "",
  description: "",
  resourceType: "",
  action: "",
  createdAt: "",
  modifiedAt: ""
}
export const permissionCriteriaSchema = z.object({
  resourceType: z.string().optional().nullable(),
  action: z.string().optional().nullable()
})

export type PermissionCriteriaSchema = z.infer<typeof permissionCriteriaSchema>

export const initialPermissionCriteriaState: PermissionCriteriaSchema = {
  resourceType: null,
  action: null
}