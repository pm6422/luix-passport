import { ReactNode } from "react"
import { FormDialog } from "@/components/custom/dialog/form-dialog"
import { Option } from "@/components/custom/form-field/multi-selector"
import { type Auth2Client, auth2ClientSchema, initialAuth2ClientState } from "@/domains/auth2-client"
import { Oauth2ClientService } from "@/services/oauth2-client-service"
import { Auth2ClientFormFields } from "./auth2-client-form-fields"

interface EditDialogProps {
  children: ReactNode,
  entityName: string,
  id?: string | null,
  save: (formData: Auth2Client) => Promise<void>
}

export function EditDialog({
  children,
  entityName,
  id,
  save
}: EditDialogProps) {
  const loadEntity = async (entityId?: string | null) => {
    const methodsRes = await Oauth2ClientService.findClientAuthenticationMethods()
    const authenticationMethodOptions = methodsRes.data.map((item: string) => ({ label: item, value: item }))
    const grantTypesRes = await Oauth2ClientService.findAuthorizationGrantTypes()
    const grantTypeOptions = grantTypesRes.data.map((item: string) => ({ label: item, value: item }))
    const scopesRes = await Oauth2ClientService.findScopes()
    const scopeOptions = scopesRes.data.map((item: string) => ({ label: item, value: item }))
    if (entityId) {
      const { data } = await Oauth2ClientService.findById(entityId)
      return { options: { authenticationMethodOptions, grantTypeOptions, scopeOptions }, entity: data }
    }
    return { options: { authenticationMethodOptions, grantTypeOptions, scopeOptions }, entity: initialAuth2ClientState }
  }

  return (
    <FormDialog<Auth2Client, { authenticationMethodOptions: Option[], grantTypeOptions: Option[], scopeOptions: Option[] }>
      entityName={entityName}
      id={id}
      schema={auth2ClientSchema}
      defaultValues={initialAuth2ClientState}
      save={save}
      onLoad={loadEntity}
      renderFields={(form, options) => (
        <Auth2ClientFormFields
          form={form}
          id={id}
          authenticationMethodOptions={options.authenticationMethodOptions}
          grantTypeOptions={options.grantTypeOptions}
          scopeOptions={options.scopeOptions}
        />
      )}
    >
      {children}
    </FormDialog>
  )
}
