import { z } from "zod"
import { BaseCriteria } from "./base/base-criteria"

export const permissionSchema = z.object({
  id: z.string().trim().min(1, { message: "Required" }),
  resourceType: z.string().trim().min(1, { message: "Required" }),
  action: z.string().trim().min(1, { message: "Required" }),
  remark: z.string().optional().nullable(),
  createdAt: z.string().optional().nullable(),
  modifiedAt: z.string().optional().nullable()
})

export type Permission = z.infer<typeof permissionSchema>

export const initialPermissionState: Permission = {
  id: "",
  resourceType: "",
  action: "",
  remark: "",
  createdAt: "",
  modifiedAt: ""
}
export const permissionCriteriaSchema = z.object({
  resourceType: z.string().optional().nullable(),
  action: z.string().optional().nullable()
})

export type PermissionCriteriaSchema = z.infer<typeof permissionCriteriaSchema> & BaseCriteria

export const initialPermissionCriteriaState: PermissionCriteriaSchema = {
  resourceType: null,
  action: null
}