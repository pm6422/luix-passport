import { z } from "zod"
import { BaseCriteria } from "./base/base-criteria"

export const auth2ClientSchema = z.object({
  id: z.string().optional().nullable(),
  clientId: z.string().trim().min(1, { message: "Required" }),
  clientName: z.string().trim().min(1, { message: "Required" }),
  rawClientSecret: z.string().trim().optional().nullable(),
  clientAuthenticationMethods: z.array(z.string()).min(1, { message: "Required" }),
  authorizationGrantTypes: z.array(z.string()).min(1, { message: "Required" }),
  redirectUris: z.array(z.string().url({ message: "Invalid redirect URI" })).optional().nullable(),
  postLogoutRedirectUris: z.array(z.string().url({ message: "Invalid post logout redirect URI" })).optional().nullable(),
  scopes: z.array(z.string()).min(1, { message: "Required" }),
  clientSettings: z.string().optional().nullable(),
  tokenSettings: z.string().optional().nullable(),
  clientIdIssuedAt: z.string().optional().nullable(),
  clientSecretExpiresAt: z.string().optional().nullable(),
  enabled: z.boolean().optional().nullable(),
})

export type Auth2Client = z.infer<typeof auth2ClientSchema>

export const initialAuth2ClientState: Auth2Client = {
  id: "",
  clientId: "",
  clientName: "",
  rawClientSecret: "",
  clientAuthenticationMethods: [],
  authorizationGrantTypes: [],
  redirectUris: null,
  postLogoutRedirectUris: [],
  scopes: [],
  clientSettings: "",
  tokenSettings: "",
  clientIdIssuedAt: "",
  clientSecretExpiresAt: "",
  enabled: true
}
export const auth2ClientCriteriaSchema = z.object({
  keyword: z.string().optional(),
})

export type Auth2ClientCriteriaSchema = z.infer<typeof auth2ClientCriteriaSchema> & BaseCriteria

export const initialAuth2ClientCriteriaState: Auth2ClientCriteriaSchema = {
  keyword: ""
}