import { z } from "zod"

export const auth2ClientSchema = z.object({
  id: z.string().optional(),
  clientId: z.string().trim().min(1, { message: "Required" }),
  clientName: z.string().trim().min(1, { message: "Required" }),
  rawClientSecret: z.string().trim().optional(),
  clientAuthenticationMethods: z.array(z.string()).min(1, { message: "Required" }),
  authorizationGrantTypes: z.array(z.string()).min(1, { message: "Required" }),
  redirectUris: z.array(z.string().url({ message: "Invalid redirect URI" })).nullable(),
  postLogoutRedirectUris: z.array(z.string().url({ message: "Invalid post logout redirect URI" })).optional(),
  scopes: z.array(z.string()).min(1, { message: "Required" }),
  clientSettings: z.string().optional(),
  tokenSettings: z.string().optional(),
  clientIdIssuedAt: z.string().optional(),
  clientSecretExpiresAt: z.string().optional(),
  enabled: z.boolean().optional(),
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

export type Auth2ClientCriteriaSchema = z.infer<typeof auth2ClientCriteriaSchema>

export const initialAuth2ClientCriteriaState: Auth2ClientCriteriaSchema = {
  keyword: ""
}