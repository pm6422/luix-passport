import { z } from "zod"
import { BaseCriteria } from "./base/base-criteria"

export const roleSchema = z.object({
  id: z.string().trim().min(1, { message: "Required" }),
  remark: z.string().optional().nullable(),
  permissionIds: z.array(z.string()).min(1, { message: "Required" }),
  createdAt: z.string().optional().nullable(),
  modifiedAt: z.string().optional().nullable()
})

export type Role = z.infer<typeof roleSchema>

export const initialRoleState: Role = {
  id: "",
  remark: "",
  permissionIds: [],
  createdAt: "",
  modifiedAt: ""
}
export const roleCriteriaSchema = z.object({
  keyword: z.string().optional(),
})

export type RoleCriteriaSchema = z.infer<typeof roleCriteriaSchema> & BaseCriteria

export const initialRoleCriteriaState: RoleCriteriaSchema = {
  keyword: ""
}