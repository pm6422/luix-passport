import { z } from "zod"
import { BaseCriteria } from "./base/base-criteria"

export const ROLE_USER = "ROLE_USER"
export const ROLE_ADMIN = "ROLE_ADMIN"
export const ROLE_DEVELOPER = "ROLE_DEVELOPER"
export const ROLE_ANONYMOUS = "ROLE_ANONYMOUS"

export const ROLE_TEAM_OWNER = "ROLE_TEAM_OWNER"
export const ROLE_TEAM_MEMBER = "ROLE_TEAM_MEMBER"

export const SYSTEM_ROLES = [ROLE_USER, ROLE_ANONYMOUS]
export const TEAM_ROLES = [ROLE_TEAM_OWNER, ROLE_TEAM_MEMBER]

export const roleSchema = z.object({
  id: z.string().trim().min(1, { message: "Required" }),
  remark: z.string().optional().nullable(),
  permissionIds: z.array(z.string()).min(1, { message: "Required" }),
  createdAt: z.string().optional().nullable(),
  updatedAt: z.string().optional().nullable()
})

export type Role = z.infer<typeof roleSchema>

export const initialRoleState: Role = {
  id: "",
  remark: "",
  permissionIds: [],
  createdAt: "",
  updatedAt: ""
}
export const roleCriteriaSchema = z.object({
  keyword: z.string().optional(),
})

export type RoleCriteriaSchema = z.infer<typeof roleCriteriaSchema> & BaseCriteria

export const initialRoleCriteriaState: RoleCriteriaSchema = {
  keyword: ""
}


